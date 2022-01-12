package peersim.kademlia;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.UnreliableTransport;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class IntraDomainLookup implements Lookup{

    private int kademliaid;
    private KadNode currentNode;
    private LinkedHashMap<Long, FindOperation> findOpMap;
    private Message m;
    private int transportID;
    private UnreliableTransport transport;
    private TreeMap<Long, Long> sentMsg;


    public IntraDomainLookup(int kademliaId, KadNode current, LinkedHashMap<Long, FindOperation> findOps, Message lookupMessage, int transportID, TreeMap<Long,Long> sentMsg) {
        this.kademliaid = kademliaId;
        this.currentNode =  current;
        this.findOpMap = findOps;
        this.m = lookupMessage;
        this.transportID = transportID;
        this.sentMsg = sentMsg;
    }

    /**
     * Start a find node operation.
     * Find the ALPHA closest node and send find request to them.
     *
     */
    @Override
    public void find() {

        // if I am the searched node or searched node is down -> skip (should not happen in kademlia)
        Node dst = Util.nodeIdtoNode(m.dest.getNodeId(), kademliaid);
        if((m.dest.getNodeId() == this.currentNode.getNodeId()) || (!dst.isUp()))
            return;

        // increase the number of find operations
        KademliaObserver.find_op.add(1);

        // create find operation and add to operations array for bookkeeping
        FindOperation fop = new FindOperation(m.dest, m.timestamp);
        fop.body = m.body;
        findOpMap.put(fop.operationId, fop);

//		System.err.println("The operationID is : " + fop.operationId);
//		System.err.println("Size of the findOP of node : " + this.nodeId +" is " + findOp.size());

        // get the K closest node to search key
        KadNode[] neighbours = this.currentNode.getRoutingTable().getKClosestNeighbours(m.dest, this.currentNode);

        // update the list of closest nodes and re-initialize available requests
        fop.updateClosestSet(neighbours);
        fop.available_requests = KademliaCommonConfig.ALPHA;

        // set message operation id
        m.operationId = fop.operationId;
        m.type = Message.MSG_ROUTE;
        m.src = this.currentNode;

        // send ALPHA messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = fop.getNeighbour();
            if (nextNode != null) {
//				System.err.println("A ROUTE message with id: " + m.id + " is sent next to node " + nextNode);
//				System.err.println("the distance between the target node " + m.dest + " and the next node " + nextNode + " is : " + Util.distance(nextNode.getNodeId(), m.dest.getNodeId()));
                KademliaObserver.next_node_distance.add(Util.distance(nextNode.getNodeId(), m.dest.getNodeId()).doubleValue());
                fop.nrHops++;
                sendMessage(m.copy(), nextNode, this.kademliaid);

            }
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
        this.currentNode.getRoutingTable().addNeighbour(destId);

        Node src = Util.nodeIdtoNode(this.currentNode.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(destId.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportID);
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


}
