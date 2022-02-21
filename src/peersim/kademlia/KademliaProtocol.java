package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

/**
 * The actual protocol.
 */
public class KademliaProtocol implements Cloneable, EDProtocol {

    private static final String PAR_TRANSPORT = "transport";
    private static String prefix = null;
    /**
     * allow to call the service initializer only once
     */
    private static boolean _ALREADY_INSTALLED = false;
    // VARIABLE PARAMETERS
    final String PAR_K = "K";
    final String PAR_ALPHA = "ALPHA";
    final String PAR_BETA = "BETA";
    final String PAR_BITS = "BITS";
    private final int tid;
    private int kademliaid;
    private KademliaNode me;
    private final String typeOfLookup;
    private Lookup currentLookup = null;
    private final DHTProtocolStore prot;


    /**
     * Used only by the initializer when creating the prototype. Every other instance call CLONE to create the new object.
     *
     * @param prefix String
     */
    public KademliaProtocol(String prefix) {

        this.me = null;

        KademliaProtocol.prefix = prefix;

        _init();

        tid = Configuration.getPid(prefix + "." + PAR_TRANSPORT);

        //determine whether the protocol is naive
        if (KademliaCommonConfig.NAIVE_KADEMLIA_PROTOCOL == 1) {
            this.typeOfLookup = "naive";
        } else {
            this.typeOfLookup = "improved";
        }

        //since every kademliaprotocol has a store, we create one for this one
        this.prot = new KademliaProtocolStore();

    }

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
     * @param myNode       Node
     * @param myProtocolID int
     * @param event        Object
     */
    public void processEvent(Node myNode, int myProtocolID, Object event) {

//        System.err.println(" ---------------------------------------- ");
        // Parse message content Activate the correct event manager for the particular event
        this.kademliaid = myProtocolID;
        SimpleEvent ev = (SimpleEvent) event;

        //if it's a message (in the future there might be different types of events)
        if (ev instanceof Message) {

            Message m = (Message) ev;

            // if I do not have a lookup object yet OR THIS IS A NEW LOOKUP ->  create new one.
            if (this.currentLookup == null || m.newLookup) {
                //create the correct lookup object
                this.currentLookup = prot.orderLookup(this.typeOfLookup, m);
            }

            // Check what type of message and handle appropriately
            switch (m.getType()) {
                case Message.MSG_FINDNODE:
//                    System.err.println("node (" + m.receiver.getNodeId() + "," + m.receiver.getDomain() + ") of type " + m.receiver.getType()  + " gets a FIND message from (" + m.sender.getNodeId() + ","  + m.sender.getDomain() + ") to find node ("+ m.target.getNodeId() + "," + m.target.getDomain() + ")");
                    this.currentLookup.prepare(kademliaid, m, tid);
                    currentLookup.performRequestOp();
                    break;
                case Message.MSG_REQUEST:
//                    System.err.println("node (" + m.receiver.getNodeId() + "," + m.receiver.getDomain() + ") of type " + m.receiver.getType()  + " gets a REQUEST message from (" + m.sender.getNodeId() + ","  + m.sender.getDomain() + ") of type " + m.sender.getType());
                    this.currentLookup.prepare(kademliaid, m, tid);
                    currentLookup.performRespondOp();
                    break;
                case Message.MSG_RESPONSE:
//                    System.err.println("node (" + m.receiver.getNodeId() + "," + m.receiver.getDomain() + ") of type " + m.receiver.getType()  + " gets a RESPONSE message from (" + m.sender.getNodeId() + ","  + m.sender.getDomain() + ") of type " + m.sender.getType());
                    this.currentLookup.prepare(kademliaid, m, tid);
                    currentLookup.performHandleResponseOp();
                    break;
                case Message.TIMEOUT: ;
                    // the response msg is not arrived
                    if (m.receiver.getSentMsgTracker().containsKey(m.msgId)) {

                        //remove from the sentMsg
                        m.receiver.getSentMsgTracker().remove(m.msgId);

                        //if it is a KadNode that is not responding
                        if (m.src instanceof KadNode) {

                            //remove this node from routing table and from the closest set of findOperation
                            m.receiver.getRoutingTable().removeNeighbour((KadNode) m.sender);
                            m.receiver.getFindOperationsMap().get(m.operationId).closestSet.remove((KadNode) m.sender);
                            //try another node
                            Message m2 = new Message();
                            m2.operationId = m.operationId;
                            m2.src = m.src;
                            m2.target = m.target;
                            m2.sender = m.receiver;
                            m2.receiver = m.receiver.getFindOperationsMap().get(m.operationId).destNode;
                            currentLookup.prepare(kademliaid, m2, tid);
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
     *
     * @param kadnode
     */
    public void setKadNode(KadNode kadnode) {
        this.me = kadnode;
    }

    /**
     * Make it clear that this kademlia protocol has an owner that is a bridgeNode
     *
     * @param bridgenode
     */
    public void setBridgeNode(BridgeNode bridgenode) {
        this.me = bridgenode;
    }


    public KademliaNode getCurrentNode() {
        return this.me;
    }

    public int getKademliaId(){return this.kademliaid;}

}
