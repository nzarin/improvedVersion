package peersim.kademlia;

/**
 * A Kademlia implementation for PeerSim extending the EDProtocol class.<br>
 * See the Kademlia bibliografy for more information about the protocol.
 */

import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.UnreliableTransport;

public class KademliaProtocol implements Cloneable, EDProtocol {

	// VARIABLE PARAMETERS
	final String PAR_K = "K";
	final String PAR_ALPHA = "ALPHA";
	final String PAR_BETA = "BETA";
	final String PAR_BITS = "BITS";

	private static final String PAR_TRANSPORT = "transport";
	private static String prefix = null;
	private UnreliableTransport transport;
	private int tid;
	private int kademliaid;

	/**
	 * allow to call the service initializer only once
	 */
	private static boolean _ALREADY_INSTALLED = false;

	/**
	 * nodeId of this node
	 */
	public BigInteger nodeId;

	/**
	 * routing table of this node
	 */
	public RoutingTable routingTable;

	/**
	 * trace message sent for timeout purpose
	 */
	private TreeMap<Long, Long> sentMsg;

	/**
	 * find operations set
	 */
	private LinkedHashMap<Long, FindOperation> findOp;

	/**
	 * Replicate this object by returning an identical copy.
	 * It is called by the initializer and do not fill any particular field.
	 * 
	 * @return Object
	 */
	public Object clone() {
		KademliaProtocol dolly = new KademliaProtocol(KademliaProtocol.prefix);
		return dolly;
	}

	/**
	 * Used only by the initializer when creating the prototype. Every other instance call CLONE to create the new object.
	 * 
	 * @param prefix
	 *            String
	 */
	public KademliaProtocol(String prefix) {
		this.nodeId = null; // empty nodeId
		KademliaProtocol.prefix = prefix;

		_init();

		routingTable = new RoutingTable();

		sentMsg = new TreeMap<Long, Long>();

		findOp = new LinkedHashMap<Long, FindOperation>();

		tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
	}

	/**
	 * This procedure is called only once and allow to initialize the internal state of KademliaProtocol. Every node shares the
	 * same configuration, so it is sufficient to call this routine once.
	 */
	private void _init() {
		// execute once
		if (_ALREADY_INSTALLED)
			return;

		// read parameters
		KademliaCommonConfig.K = Configuration.getInt(prefix + "." + PAR_K, KademliaCommonConfig.K);
		KademliaCommonConfig.ALPHA = Configuration.getInt(prefix + "." + PAR_ALPHA, KademliaCommonConfig.ALPHA);
		KademliaCommonConfig.BETA = Configuration.getInt(prefix + "." + PAR_BETA, KademliaCommonConfig.BETA);
		KademliaCommonConfig.BITS = Configuration.getInt(prefix + "." + PAR_BITS, KademliaCommonConfig.BITS);

		_ALREADY_INSTALLED = true;
	}

	/**
	 * Search through the network the Node having a specific node Id, by performing binary search (we concern about the ordering
	 * of the network).
	 * 
	 * @param searchNodeId
	 *            BigInteger
	 * @return Node
	 */
	private Node nodeIdtoNode(BigInteger searchNodeId) {
		if (searchNodeId == null)
			return null;

		int inf = 0;
		int sup = Network.size() - 1;
		int m;

		while (inf <= sup) {
			m = (inf + sup) / 2;

			BigInteger mId = ((KademliaProtocol) Network.get(m).getProtocol(kademliaid)).nodeId;

			if (mId.equals(searchNodeId))
				return Network.get(m);

			if (mId.compareTo(searchNodeId) < 0)
				inf = m + 1;
			else
				sup = m - 1;
		}

		// perform a traditional search for more reliability (maybe the network is not ordered)
		BigInteger mId;
		for (int i = Network.size() - 1; i >= 0; i--) {
			mId = ((KademliaProtocol) Network.get(i).getProtocol(kademliaid)).nodeId;
			if (mId.equals(searchNodeId))
				return Network.get(i);
		}

		return null;
	}

	/**
	 * Perform the required operation upon receiving a message in response to a ROUTE message.
	 * Update the find operation record with the closest set of neighbour received. Then, send ALPHA ROUTE request.
	 * If no closest neighbour available and no outstanding messages, then stop the find operation.
	 * 
	 * @param m
	 *            Message
	 * @param myPid
	 *            The sender Pid
	 */
	private void route(Message m, int myPid) {

		// add message source to my routing table
		if (m.src != null) {
			routingTable.addNeighbour(m.src);
		}


		// get corresponding find operation (using the message field operationId)
		FindOperation fop = this.findOp.get(m.operationId);

		if (fop != null) {

//			System.err.println("My old closest set is : " + fop.getClosestSet().toString());

			// Step 1: update by saving received neighbour in the closest set of find operation
			try {
				fop.updateClosestSet((BigInteger[]) m.body);
//				System.err.println("My new (updated) closest set is : " + fop.getClosestSet().toString());

			} catch (Exception ex) {
				fop.available_requests++;
//				System.err.println("And I did not updated it.");

			}

			// Step 2: send new requests if necessary
			while (fop.available_requests > 0) {

				// get an available neighbour
				BigInteger neighbour = fop.getNeighbour();

				// if there is a node that still needs to be queried
				if (neighbour != null) {

//					System.err.println("I found a neighbor that still needs to be queried: node " + neighbour);
//					System.err.println("So I am going to send it a ROUTE message");

					// create a new request to send to neighbour
					Message request = new Message(Message.MSG_ROUTE);
					request.operationId = m.operationId;
					request.src = this.nodeId;
					request.dest = m.dest;

					// increment hop count
					fop.nrHops++;

					// send find request
					sendMessage(request, neighbour, myPid);

					// no new neighbour and no outstanding requests
				} else if (fop.available_requests == KademliaCommonConfig.ALPHA) {

//					System.err.println("I have no new valid neighbours and no outstanding requests");
//					System.err.println("My available fop request is " + fop.available_requests);

					// search operation finished
					findOp.remove(fop.operationId);

					// if the find operation was not for bootstrapping purposes
					if (fop.body.equals("Automatically Generated Traffic")) {

						//only add the statistics of no-bootstrap lookups
						KademliaObserver.finished_lookups.add(1);

						// if the target is found -> successful lookup
						if(fop.closestSet.containsKey(fop.destNode)){
							// update statistics
							long timeInterval = (CommonState.getTime()) - (fop.timestamp);
							KademliaObserver.timeStore.add(timeInterval);
							KademliaObserver.hopStore.add(fop.nrHops);
							KademliaObserver.successful_lookups.add(1);
						} else{	// failed lookup
							KademliaObserver.failed_lookups.add(1);
						}
					}

					//let it be

					return;

				} else {
//					System.err.println(" I have no neighbour available, but there exist outstanding requests. So I wait.");
					return;
				}
			}
		} else {
			System.err.println("There has been some error in the protocol");
		}
	}

	/**
	 * Response to a route request.
	 * Find the ALPHA closest node consulting the k-buckets and return them to the sender.
	 * 
	 * @param m
	 *            Message
	 * @param senderPid
	 *            The sender Pid
	 */
	private void routeResponse(Message m, int senderPid) {

		// get the k closest nodes to target node
		BigInteger[] neighbours = this.routingTable.getKClosestNeighbours(m.dest, m.src);

		//get the BETA closest nodes from the neighbours
		BigInteger[] betaNeighbours = Arrays.copyOfRange(neighbours, 0, KademliaCommonConfig.BETA);


		// create a response message containing the neighbours (with the same id as of the request)
		Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
		response.operationId = m.operationId;
		response.dest = m.dest;
		response.src = this.nodeId;
		response.ackId = m.id; // set ACK number

//		System.err.println();
//		System.err.println("Node " + this.nodeId + " is sending a RESPONSE message to node " + m.src + " with the following list of neighbors: " + Arrays.toString(betaNeighbours));

		// send back the neighbours to the source of the message
		sendMessage(response, m.src, senderPid);
	}

	/**
	 * Start a find node operation.
	 * Find the ALPHA closest node and send find request to them.
	 * 
	 * @param m
	 *            Message received (contains the node to find)
	 * @param myPid
	 *            the sender Pid
	 */
	private void find(Message m, int myPid) {

		// increase the number of find operations
		KademliaObserver.find_op.add(1);

		// create find operation and add to operations array
		FindOperation fop = new FindOperation(m.dest, m.timestamp);
		fop.body = m.body;
		findOp.put(fop.operationId, fop);

//		System.err.println("The operationID is : " + fop.operationId);
//		System.err.println("Size of the findOP of node : " + this.nodeId +" is " + findOp.size());

		// get the K closest node to search key
		BigInteger[] neighbours = this.routingTable.getKClosestNeighbours(m.dest, this.nodeId);

		// update the list of closest nodes and re-initialize available requests
		fop.updateClosestSet(neighbours);
		fop.available_requests = KademliaCommonConfig.ALPHA;

		// set message operation id
		m.operationId = fop.operationId;
		m.type = Message.MSG_ROUTE;
		m.src = this.nodeId;

//		System.err.println("The operation id in the ROUTE message is now: " + m.operationId);

		// send ALPHA messages
		for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
			BigInteger nextNode = fop.getNeighbour();
			if (nextNode != null) {

//				System.err.println("A ROUTE message with id: " + m.id + " is sent next to node " + nextNode);

				sendMessage(m.copy(), nextNode, myPid);
				fop.nrHops++;
			}
		}

	}


	/**
	 * When a node receives a ping message it should reply or do nothing.
	 * @param m
	 * @param myPid
	 */
	private void ping(Message m, int myPid) {
		// if I am online -> send a reply message
		// if I am offline -> do nothing and wait until the ping message exceeds timeout deadline.

	}

	/**
	 * Send a message with current transport layer and starting the timeout timer (which is an event) if the message is a request
	 * 
	 * @param m
	 *            The message to send.
	 * @param destId
	 *            The Id of the destination node.
	 * @param myPid
	 *            The sender Pid.
	 */
	private void sendMessage(Message m, BigInteger destId, int myPid) {
		// add destination to routing table
		this.routingTable.addNeighbour(destId);

		Node src = nodeIdtoNode(this.nodeId);
		Node dest = nodeIdtoNode(destId);

		transport = (UnreliableTransport) (Network.prototype).getProtocol(tid);
		transport.send(src, dest, m, kademliaid);

		if (m.getType() == Message.MSG_ROUTE) { // is a request
			Timeout t = new Timeout(destId, m.id, m.operationId);

			// set delay at 2*RTT
			long latency = transport.getLatency(src, dest);
			long delay = 4*latency;

			// add to sent msg
			this.sentMsg.put(m.id, m.timestamp);
			EDSimulator.add(delay, t, src, myPid);
		}
	}


	/**
	 * If the message is not arrived before timeout, try to resend it.
	 * @param timeout
	 * 					The timeout event.
	 * @param myPid
	 * 					The sender Pid.
	 */
	private void handleTimeOut(Timeout timeout, int myPid) {

		// the response msg isn't arrived
		if (sentMsg.containsKey(timeout.msgID)) {

//			//add to list of failed lookups
//			KademliaObserver.failed_lookups.add(1);

			// remove form sentMsg
			sentMsg.remove(timeout.msgID);
			// remove node from my routing table
			this.routingTable.removeNeighbour(timeout.node);
			// remove from closestSet of find operation
			this.findOp.get(timeout.opID).closestSet.remove(timeout.node);
			// try another node
			Message m1 = new Message();
			m1.operationId = timeout.opID;
			m1.src = nodeId;
			m1.dest = this.findOp.get(timeout.opID).destNode;
			this.route(m1, myPid);
		}

	}

	/**
	 * Manage the peersim simulator receiving the events
	 * 
	 * @param myNode
	 *            Node
	 * @param myPid
	 *            int
	 * @param event
	 *            Object
	 */
	public void processEvent(Node myNode, int myPid, Object event) {

//		System.err.println("");
//		System.err.println("-----------------------------------------------------------------------------------------------------");

		// Parse message content Activate the correct event manager for the particular event
		this.kademliaid = myPid;

		Message m;

		switch (((SimpleEvent) event).getType()) {

			case Message.MSG_EMPTY:
				// todo
				break;

			case Message.MSG_STORE:
				// todo
				break;

			case Message.MSG_FINDNODE:
				m = (Message) event;

//				System.err.println("Node with protocol node ID " + this.nodeId +" received a FIND NODE message");
//				System.err.println("It should find node " + m.dest);
//				System.err.println("Its current routing table is as follows: " + this.routingTable.toString());

				find(m, myPid);
				break;


			case Message.MSG_ROUTE:
				m = (Message) event;

//				System.err.println("Node with protocol node ID " + this.nodeId +" has received a ROUTE message");
//				System.err.println("The src of this message is " + m.src);
//				System.err.println("It wants to find " + m.dest);
//				System.err.println("And its current routing table is as follows: " + this.routingTable.toString());

				routeResponse(m, myPid);
				break;

			case Message.MSG_RESPONSE:
				m = (Message) event;

//				System.err.println("Node with protocol node ID " + this.nodeId +" has received a RESPONSE message");
//				System.err.println("The src of this message is " + m.src);
//				System.err.println("We were looking for " + m.dest);

				sentMsg.remove(m.ackId);
				route(m, myPid);
				break;

			case Message.MSG_PING:
				m = (Message) event;

//				System.err.println("Node with protocol node ID " + this.nodeId +" has received a PING message");
//				System.err.println("The src of this message is " + m.src);
//				System.err.println("It wants to find " + m.dest);

				ping(m, myPid);
				break;


			case Timeout.TIMEOUT: // timeout
				Timeout t = (Timeout) event;

//				System.err.println("Node with protocol node ID " + this.nodeId +" has received a TIMEOUT message");

				handleTimeOut(t, myPid);
				break;

		}

	}



	/**
	 * Set the current NodeId
	 * 
	 * @param tmp
	 *            BigInteger
	 */
	public void setNodeId(BigInteger tmp) {
		this.nodeId = tmp;
		this.routingTable.nodeId = tmp;
	}

	/**
	 * Get the current NodeID
	 * @return nodeId
	 */
	public BigInteger getNodeId(){
		return this.nodeId;
	}

	/**
	 * Get the current transport ID
	 * @return tid
	 */
	public int getTransportID(){
		return this.tid;
	}

	/**
	 * Get the current kademlia protocol ID
	 * @return kademliaid
	 */
	public int getKademliaProtocolID(){
		return this.kademliaid;
	}



}
