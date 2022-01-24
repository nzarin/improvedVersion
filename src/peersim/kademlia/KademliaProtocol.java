package peersim.kademlia;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

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
	private int tid;
	private int kademliaid;
	private KademliaNode me;
	private String typeOfLookup;
	private Lookup currentLookup = null;
	private DHTProtocolStore prot;
	private int currentOperationId;

	/**
	 * allow to call the service initializer only once
	 */
	private static boolean _ALREADY_INSTALLED = false;




//	/**
//	 * trace message sent for timeout purpose
//	 */
//	private TreeMap<Long, Long> sentMsg;
//
//	/**
//	 * find operations set
//	 */
//	private LinkedHashMap<Long, FindOperation> findOps;

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

		this.me = null;

		KademliaProtocol.prefix = prefix;

		_init();

		tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);

		//determine whether the protocol is naive
		if(KademliaCommonConfig.NAIVE_KADEMLIA_PROTOCOL == 1){
			this.typeOfLookup = "naive";
		} else {
			this.typeOfLookup = "improved";
		}

		this.prot = new KademliaProtocolStore();

		//set the current operation id to 0
		this.currentOperationId = -1;
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
	 * Manage the peersim simulator receiving the events
	 * 
	 * @param myNode
	 *            Node
	 * @param myProtocolID
	 *            int
	 * @param event
	 *            Object
	 */
	public void processEvent(Node myNode, int myProtocolID, Object event) {

		// Parse message content Activate the correct event manager for the particular event
		this.kademliaid = myProtocolID;
		SimpleEvent ev = (SimpleEvent) event;

		//if it's a message (in the future there might be different types of events)
		if(ev instanceof Message){

			Message m = (Message) ev;

//			System.err.println(" current lookup is null?: " + (this.currentLookup == null));
//			System.err.println(" newLookup is:  " + m.newLookup);


			// if I do not have a lookup object yet OR THIS IS A NEW LOOKUP ->  create new one.
			if (this.currentLookup == null || m.newLookup){
				//create the correct lookup object
				System.err.println("Since we do not have a new lookup, we create a new lookup object");
				this.currentLookup = prot.orderLookup(this.typeOfLookup, this.kademliaid, m, this.tid);
				//initialize all the operations and prepare to perform one
//				System.err.println("  -> lookup.source:" + currentLookup.source.getNodeId());
//				System.err.println("  -> lookup.target:  " + currentLookup.target.getNodeId());
//				System.err.println("  -> lookup.sender:  " + currentLookup.sender.getNodeId());
//				System.err.println("  -> lookup.receiver:  " + currentLookup.receiver.getNodeId());
			}


			// check what type of message and handle appropriately
			switch (m.getType()) {
				case Message.MSG_FINDNODE:
					System.err.println("I am node " + this.me.getNodeId() + " and I have received a find message for " + m.target.getNodeId());
//					System.err.println("  --> this.me: " + this.me.getNodeId());
//					System.err.println("  --> m.source: " + m.src.getNodeId());
//					System.err.println("  --> m.target: " + m.target.getNodeId());
//					System.err.println("  --> m.sender: " + m.sender.getNodeId());
//					System.err.println("  --> m.receiver: " + m.receiver.getNodeId());
//					System.err.println("  --> m.msgID: " + m.msgId);
					System.err.println("  --> m.operationId: " + m.operationId);
					this.currentLookup.prepare(m.src, m.target, m.sender, m.receiver, kademliaid, m, tid);
					currentLookup.performFindOp();
					this.currentOperationId++;
					System.err.println("---------------------------------------------------------------------------------");
					break;
				case Message.MSG_ROUTE:
					System.err.println("I am node " + this.me.getNodeId() + " and I have received a route message from " + m.sender.getNodeId() + " to find node " + m.target.getNodeId());
//					System.err.println("  --> this.me: " + this.me.getNodeId());
//					System.err.println("  --> m.source: " + m.src.getNodeId());
//					System.err.println("  --> m.target: " + m.target.getNodeId());
//					System.err.println("  --> m.sender: " + m.sender.getNodeId());
//					System.err.println("  --> m.receiver: " + m.receiver.getNodeId());
//					System.err.println("  --> m.msgID: " + m.msgId);
					System.err.println("  --> m.operationId: " + m.operationId);
					this.currentLookup.prepare(m.src, m.target, m.sender, m.receiver, kademliaid, m, tid);
					currentLookup.performRespondOp();
					System.err.println("---------------------------------------------------------------------------------");
					break;
				case Message.MSG_RESPONSE:
					System.err.println("I am node " + this.me.getNodeId() + " and I have received response message  from  " +  m.sender.getNodeId() + " and we tried to find " + m.target.getNodeId());
//					System.err.println("  --> this.me: " + this.me.getNodeId());
//					System.err.println("  --> m.source: " + m.src.getNodeId());
//					System.err.println("  --> m.target: " + m.target.getNodeId());
//					System.err.println("  --> m.sender: " + m.sender.getNodeId());
//					System.err.println("  --> m.receiver: " + m.receiver.getNodeId());
//					System.err.println("  --> m.msgID: " + m.msgId);
					System.err.println("  --> m.operationId: " + m.operationId);
					this.currentLookup.prepare(m.src, m.target, m.sender, m.receiver, kademliaid, m, tid);
					currentLookup.performHandleResponseOp();
					System.err.println("---------------------------------------------------------------------------------");
					break;
				case Message.TIMEOUT:
					// the response msg is not arrived
					if(this.me.getSentMsgTracker().containsKey(m.msgId)){

						//remove from the sentMsg
						this.me.getSentMsgTracker().remove(m.msgId);

						//if it is a KadNode that is not responding
						if(m.src instanceof KadNode){

							//remove this node from routing table and from the closest set of findOperation
							this.me.getRoutingTable().removeNeighbour((KadNode) m.src);
							this.me.getFindOperationsMap().get(m.operationId).closestSet.remove((KadNode) m.src);
							System.err.println("TIME OUT, we gotta send a new message");
							//try another node
							Message m2 = new Message();
							m2.operationId = m.operationId;
							m2.src = this.me;
							m2.receiver = this.me.getFindOperationsMap().get(m.operationId).destNode;

							//todo: because all variables have changed, we need to initialized everything again???
//							currentLookup = prot.orderLookup(this.typeOfLookup, this.me, m2.receiver, this.kademliaid, m2, this.tid);
							currentLookup.performHandleResponseOp();

							//todo: it is a BridgeNode that is not responding
						} else {
						}
					}
					break;
			}

		} else {
			System.err.println("this event is of a type we don't know");
		}


	}

	/**
	 * Make it clear that this kademlia protocol has an owner that is a kadNode
	 * @param kadnode
	 */
	public void setKadNode(KadNode kadnode){ this.me = kadnode;}

	/**
	 * Make it clear that this kademlia protocol has an owner that is a bridgeNode
	 * @param bridgenode
	 */
	public void setBridgeNode(BridgeNode bridgenode){
		this.me = bridgenode;
	}


	public KademliaNode getCurrentNode(){
		return this.me;
	}



}
