package peersim.kademlia;
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

/**
 * The actual protocol.
 */
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
	
	
	private KadNode kadNode;

	/**
	 * allow to call the service initializer only once
	 */
	private static boolean _ALREADY_INSTALLED = false;


	/**
	 * domain this node belongs to
	 */
	public int domainId;

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
		this.kadNode = null; // empty nodeId
		this.domainId = -1; //empty domain Id

		KademliaProtocol.prefix = prefix;

		_init();

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
	 * Perform the required operation upon receiving a message in response to a ROUTE message.
	 * Update the find operation record with the closest set of neighbour received. Then, send ALPHA ROUTE request.
	 * If no closest neighbour available and no outstanding messages, then stop the find operation.
	 * 
	 * @param m
	 *            Message
	 * @param myPid
	 *            The sender Pid
	 */
	private void handleResponse(Message m, int myPid) {
		// add message source to my routing table
		if (m.src != null) {
			this.kadNode.getRoutingTable().addNeighbour(m.src);
		}

		FindOperation fop = this.findOp.get(m.operationId);

		if (fop != null) {

//			System.err.println("My old closest set is : " + fop.getClosestSet().toString());

			// Step 1: update by saving received neighbour in the closest set of find operation
			try {
				fop.updateClosestSet((KadNode[]) m.body);
//				System.err.println("My new (updated) closest set is : " + fop.getClosestSet().toString());

			} catch (Exception ex) {
				fop.available_requests++;
			}

			// Step 2: send new requests if necessary
			while (fop.available_requests > 0) {

				// get an available neighbour
				KadNode neighbour = fop.getNeighbour();

				// if there is a node that still needs to be queried
				if (neighbour != null) {

//					System.err.println("I found a neighbor that still needs to be queried: node " + neighbour);
//					System.err.println("So I am going to send it a ROUTE message");

					// create a new request to send to neighbour
					Message request = new Message(Message.MSG_ROUTE);
					request.operationId = m.operationId;
					request.src = this.kadNode;
					request.dest = m.dest;

					// increment hop count
					fop.nrHops++;

					// send find request
//					System.err.println("the distance between the target node " + m.dest + " and the next node " + neighbour + " is : " + Util.distance(neighbour.getNodeId(), m.dest.getNodeId()));
					KademliaObserver.next_node_distance.add(Util.distance(neighbour.getNodeId(), m.dest.getNodeId()).doubleValue());
					sendMessage(request, neighbour, myPid);

					// no new neighbour and no outstanding requests
				} else if (fop.available_requests == KademliaCommonConfig.ALPHA) {

//					System.err.println("I have no new valid neighbours and no outstanding requests");
//					System.err.println("My available fop request is " + fop.available_requests);

					// Search operation finished. The lookup terminates when the initiator has queried and gotten responses
					// from the k closest nodes from the closest set it has seen.
					findOp.remove(fop.operationId);

					// if the find operation was not for bootstrapping purposes
					if (fop.body.equals("Automatically Generated Traffic")) {


						// if the target is found -> successful lookup
						if(fop.closestSet.containsKey(fop.destNode)){

							// update statistics
							long timeInterval = (CommonState.getTime()) - (fop.timestamp);
							KademliaObserver.timeStore.add(timeInterval);
							KademliaObserver.hopStore.add(fop.nrHops);
							KademliaObserver.finished_lookups.add(1);
							KademliaObserver.successful_lookups.add(1);
							System.err.println("!!!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP SUCCEEDED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

						} else{	// failed lookup?

							Node destNode = Util.nodeIdtoNode(fop.destNode.getNodeId(), kademliaid);
							Node me = Util.nodeIdtoNode(this.kadNode.getNodeId(), kademliaid);

							// check if both the destination node and me are still up.
							if(destNode.isUp() && me.isUp() ){

								KademliaObserver.finished_lookups.add(1);
								KademliaObserver.failed_lookups.add(1);
								System.err.println("!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP FAILED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

							}

						}
					} else { // it's a bootstrap message (let it be)
						System.err.println("This is a bootstrap message. Let it be. ");
					}

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
	 * Send a message with current transport layer and starting the timeout timer (which is an event) if the message is a request
	 *
	 * @param m
	 *            The message to send.
	 * @param destId
	 *            The Id of the destination node.
	 * @param myPid
	 *            The sender Pid.
	 */
	private void sendMessage(Message m, KadNode destId, int myPid) {
		// add destination to routing table
		this.kadNode.getRoutingTable().addNeighbour(destId);

		Node src = Util.nodeIdtoNode(this.kadNode.getNodeId(), kademliaid);
		Node dest = Util.nodeIdtoNode(destId.getNodeId(), kademliaid);

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

//			System.err.println("We see that the timeout is for a message that did not get a response on time. ");
//			//add to list of failed lookups
//			KademliaObserver.failed_lookups.add(1);

			// remove form sentMsg
			sentMsg.remove(timeout.msgID);
			// remove node from my routing table
			this.kadNode.getRoutingTable().removeNeighbour(timeout.node);
			// remove from closestSet of find operation
			this.findOp.get(timeout.opID).closestSet.remove(timeout.node);
			// try another node
			Message m1 = new Message();
			m1.operationId = timeout.opID;
			m1.src = this.kadNode;
			m1.dest = this.findOp.get(timeout.opID).destNode;

//			System.err.println("We try to resend it now to another node");
			this.handleResponse(m1, myPid);
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

		// todo: this code is not closed for modification. If the kademliaprotocol gets extended with new messages, then this we have to modify this.

//		System.err.println("");
//		System.err.println("-----------------------------------------------------------------------------------------------------");

		// Parse message content Activate the correct event manager for the particular event
		this.kademliaid = myPid;
		SimpleEvent ev = (SimpleEvent) event;

		//if it's a message
		if(ev instanceof Message){

			Message m = (Message) ev;
			Lookup lookup;

			if(this.kadNode.getDomain() == m.dest.getDomain()){
				lookup = new IntraDomainLookup(kademliaid, this.kadNode, findOp, m, tid, sentMsg);
			} else{
				lookup =  new InterDomainLookup();
			}

			// check what type of message and handle appropriately
			switch (m.getType()) {
				case Message.MSG_FINDNODE:
					lookup.find();
					break;
				case Message.MSG_ROUTE:
					lookup.respond();
					break;
				case Message.MSG_RESPONSE:
					sentMsg.remove(m.ackId);
					lookup.handleResponse();
					break;
			}

		} else {	//it is a timeout
			handleTimeOut((Timeout) ev, this.kademliaid);
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
	public void processEvent2(Node myNode, int myPid, Object event) {

		// todo: this code is not closed for modification. If the kademliaprotocol gets extended with new messages, then this we have to modify this.

//		System.err.println("");
//		System.err.println("-----------------------------------------------------------------------------------------------------");

		// Parse message content Activate the correct event manager for the particular event
		this.kademliaid = myPid;
		SimpleEvent ev = (SimpleEvent) event;

		//if it's a message
		if(ev instanceof Message){

			Message m = (Message) ev;
			Lookup lookup;

			if(this.kadNode.getDomain() == m.dest.getDomain()){
				lookup = new IntraDomainLookup(kademliaid, this.kadNode, findOp, m, tid, sentMsg);
			} else{
				lookup =  new InterDomainLookup();
			}

			// check what type of message and handle appropriately
			switch (m.getType()) {
				case Message.MSG_FINDNODE:
					lookup.find();
					break;
				case Message.MSG_ROUTE:
					lookup.respond();
					break;
				case Message.MSG_RESPONSE:
					sentMsg.remove(m.ackId);
					lookup.handleResponse();
					break;
			}

		} else {	//it is a timeout
			handleTimeOut((Timeout) ev, this.kademliaid);
		}

	}


	
	public void setNode(KadNode nid){
		this.kadNode = nid;
	}

	/**
	 * Get the current NodeID
	 * @return nodeId
	 */
	public KadNode getKadNode(){
		return this.kadNode;
	}

	/**
	 * Get the current transport ID
	 * @return tid
	 */
	public int getTransportID(){
		return this.tid;
	}


}
