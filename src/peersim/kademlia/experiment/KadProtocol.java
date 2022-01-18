package peersim.kademlia.experiment;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.kademlia.*;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadProtocol implements Cloneable, EDProtocol {

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
        KademliaProtocol dolly = new KademliaProtocol(KadProtocol.prefix);
        return dolly;
    }

    /**
     * Used only by the initializer when creating the prototype. Every other instance call CLONE to create the new object.
     *
     * @param prefix
     *            String
     */
    public KadProtocol(String prefix) {

        KadProtocol.prefix = prefix;

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
            // remove node from my routing table if its a kadnode
            if(timeout.node instanceof KadNode){
                this.me.getRoutingTable().removeNeighbour((KadNode) timeout.node);
            }
            // remove from closestSet of find operation
            this.findOp.get(timeout.opID).closestSet.remove(timeout.node);

            // try another node
            Message m1 = new Message();
            m1.operationId = timeout.opID;
            m1.src = (KadNode) this.me;
            m1.dest = this.findOp.get(timeout.opID).destNode;
            LookupFactory lookup;
            if(((KadNode) this.me).getDomain() == m1.dest.getDomain()){
                lookup = new IntraDomainLookup(kademliaid, (KadNode) this.me, findOp, m1, tid, sentMsg);
            } else{
                lookup =  new InterDomainLookup(kademliaid, (KadNode) this.me, findOp, m1, tid, sentMsg);
            }
            lookup.handleResponse();
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

        // Parse message content Activate the correct event manager for the particular event
        this.kademliaid = myPid;
        SimpleEvent ev = (SimpleEvent) event;

        //if it's a message
        if(ev instanceof Message){

            Message m = (Message) ev;

            // check what type of message and handle appropriately
            switch (m.getType()) {
                case Message.MSG_FINDNODE:
                    DHTProtocolStore prot = new KademliaProtocolStore();
                    this.currentLookup = prot.orderLookup(this.type, this.me, m.dest);
                    currentLookup.performFindOp();
                    break;
                case Message.MSG_ROUTE:
                    this.currentLookup.performRespondOp();
                    break;
                case Message.MSG_RESPONSE:
                    sentMsg.remove(m.ackId);
                    this.currentLookup.performHandleResponseOp();
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

    /**
     * Get the current kadNode if it is kadNode.
     * @return nodeId
     */
    public KadNode getKadNode(){
        return (KadNode) this.me;
    }

    /**
     * Get the current bridge node if it is a bridgeNode.
     * @return
     */
    public BridgeNode getBridgeNode(){
        return (BridgeNode) this.me;
    }

    /**
     * Get the current transport ID
     * @return tid
     */
    public int getTransportID(){
        return this.tid;
    }



}
