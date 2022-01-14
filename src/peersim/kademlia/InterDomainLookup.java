package peersim.kademlia;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.UnreliableTransport;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class InterDomainLookup implements LookupFactory {


    private int kademliaid;
    private KadNode currentNode;
    private LinkedHashMap<Long, FindOperation> findOpMap;
    private Message m;
    private int transportID;
    private UnreliableTransport transport;
    private TreeMap<Long, Long> sentMsg;

    public InterDomainLookup(int kademliaId, KadNode current, LinkedHashMap<Long, FindOperation> findOps, Message lookupMessage, int transportID, TreeMap<Long,Long> sentMsg) {
        this.kademliaid = kademliaId;
        this.currentNode =  current;
        this.findOpMap = findOps;
        this.m = lookupMessage;
        this.transportID = transportID;
        this.sentMsg = sentMsg;
    }

    @Override
    public void find() {

        //1: send to bridge node of this domain
        //2: bridge node searches for the correct other bridge node from the receiver domain
        //3: bridge node select a random node to initiate the lookup
        //4: intra-domain lookup
        //5: return closest set to bridge node
        //6: bridge node returns closest set to other bridge node
        //7: other bridge node sends closest set to initiator node

    }

    @Override
    public void respond() {

    }

    @Override
    public void handleResponse() {

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
