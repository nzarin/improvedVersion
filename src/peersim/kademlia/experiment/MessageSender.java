package peersim.kademlia.experiment;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.kademlia.*;
import peersim.transport.Transport;
import peersim.transport.UnreliableTransport;

import java.util.TreeMap;

public class MessageSender {

    private Transport transport;
    private int kademliaid;
    private int transportid;
    private int pid;

    public MessageSender(Transport tr, int pid, int kademliaid, int transportid) {
        this.transport = tr;
        this.kademliaid = kademliaid;
        this.transportid = transportid;
        this.pid = pid;
    }


    public void sendMessage(Message m, KademliaNode s, KademliaNode r, TreeMap<Long, Long> sentMsg) {

        //determine what type of message and then send it
        if(s instanceof KadNode){
            if(r instanceof KadNode) {
               sendMessageKadToKad((KadNode) s, (KadNode) r, m, sentMsg);
            } else{
                sendMessageKadToBridge((KadNode) s, (BridgeNode) r, m, sentMsg);
            }
        } else{
            if(r instanceof KadNode){
                sendMessageBridgeToKad((BridgeNode) s, (KadNode) r, m, sentMsg);
            } else{
                sendMessageBridgeToBridge((BridgeNode) s, (BridgeNode) r, m, sentMsg);
            }
        }

    }

    private void sendMessageKadToKad(KadNode s, KadNode r, Message m, TreeMap sentMsg){
        //todo: fix dat "current node" (dit kan een tussennode zijn, zijn routing table updates (check oude code)
        s.getRoutingTable().addNeighbour(r);

        Node src = Util.nodeIdtoNode(s.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(r.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

        if (m.getType() == Message.MSG_ROUTE) { // is a request
            Timeout t = new Timeout(r, m.id, m.operationId);

            // set delay at 2*RTT
            long latency = transport.getLatency(src, dest);
            long delay = 4*latency;

            // add to sent msg
            sentMsg.put(m.id, m.timestamp);
            EDSimulator.add(delay, t, src, pid);
        }
    }

    private void sendMessageKadToBridge(KadNode s, BridgeNode r, Message m, TreeMap sentMsg){
    }

    private void sendMessageBridgeToKad(BridgeNode s, KadNode r, Message m, TreeMap sentMsg){
    }

    private void sendMessageBridgeToBridge(BridgeNode s, BridgeNode r, Message m, TreeMap sentMsg){

    }

}
