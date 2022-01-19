package peersim.kademlia;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.kademlia.experiment.DHTProtocolStore;
import peersim.kademlia.experiment.KademliaProtocolStore;
import peersim.kademlia.experiment.Lookup;

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
	private String type;
	private Lookup currentLookup;
	private DHTProtocolStore prot;


	/**
	 * allow to call the service initializer only once
	 */
	private static boolean _ALREADY_INSTALLED = false;

	private KademliaNode me;

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
//		this.kadNode = null; // empty nodeId
//		this.domainId = -1; //empty domain Id

		KademliaProtocol.prefix = prefix;

		_init();

		sentMsg = new TreeMap<Long, Long>();

		findOp = new LinkedHashMap<Long, FindOperation>();

		tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);

		//determine whether the protocol is naive
		if(KademliaCommonConfig.NAIVE_KADEMLIA_PROTOCOL == 1){
			this.type = "naive";
		} else {
			this.type = "improved";
		}

		this.currentLookup = null;

		this.prot = new KademliaProtocolStore();
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
	 * If the message is not arrived before timeout, try to resend it.
	 * @param timeout
	 * 					The timeout event.
	 */
	private void handleTimeOut(Timeout timeout) {

		// the response msg isn't arrived
		if (sentMsg.containsKey(timeout.msgID)) {

			// remove form sentMsg
			sentMsg.remove(timeout.msgID);

			//if its a kadnode that is not responding
			if(timeout.node instanceof KadNode){

				//remove this node from routing table as it is not available
				this.me.getRoutingTable().removeNeighbour((KadNode) timeout.node);

				// remove from closestSet of find operation
				this.findOp.get(timeout.opID).closestSet.remove(timeout.node);

				// try another node
				Message m1 = new Message();
				m1.operationId = timeout.opID;
				m1.src = (KadNode) this.me;
				m1.dest = this.findOp.get(timeout.opID).destNode;

				//create the correct lookup object
//				currentLookup = prot.orderLookup(this.type, this.me, m1.dest, this.kademliaid, m1, findOp, sentMsg, this.tid);
				currentLookup.performHandleResponseOp();

				// todo: if it is a bridgenode that is not responding
			} else {

			}



		}

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

		//if it's a message
		if(ev instanceof Message){

			Message m = (Message) ev;

			//create the correct lookup object
			this.currentLookup = prot.orderLookup(this.type, this.me, m.dest, this.kademliaid, m, findOp, sentMsg, this.tid);

			// check what type of message and handle appropriately
			switch (m.getType()) {
				case Message.MSG_FINDNODE:
					currentLookup.performFindOp();
					break;
				case Message.MSG_ROUTE:
					currentLookup.performRespondOp();
					break;
				case Message.MSG_RESPONSE:
					sentMsg.remove(m.ackId);
					currentLookup.performHandleResponseOp();
					break;
			}

		} else {	//it is a timeout
			handleTimeOut((Timeout) ev);
		}


	}

	/**
	 * Make it clear that this kademlia protocol has an owner that is a kadNode
	 * @param nid
	 */
	public void setKadNode(KadNode nid){
		this.me = nid;
	}

	/**
	 * Make it clear that this kademlia protocol has an owner that is a bridgeNode
	 * @param nid
	 */
	public void setBridgeNode(BridgeNode nid){
		this.me = nid;
	}


	public KademliaNode getCurrentNode(){
		return this.me;
	}

	/**
	 * Get the current transport ID
	 * @return tid
	 */
	public int getTransportID(){
		return this.tid;
	}


}
