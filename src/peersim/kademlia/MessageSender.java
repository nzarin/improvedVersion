package peersim.kademlia;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import peersim.transport.UnreliableTransport;

public class MessageSender {

    private Transport transport;
    private int kademliaid;
    private int transportid;


    public MessageSender(int kademliaid, int transportid) {
        this.kademliaid = kademliaid;
        this.transportid = transportid;
    }


    public void sendMessage(Message m) {

        //determine what type of message and then send it
        if(m.sender instanceof KadNode){
            if(m.receiver instanceof KadNode) {
               sendMessageKadToKad((KadNode) m.sender, (KadNode) m.receiver, m);
            } else{
                sendMessageKadToBridge((KadNode) m.sender, (BridgeNode) m.receiver, m);
            }
        } else{
            if(m.receiver instanceof KadNode){
                sendMessageBridgeToKad((BridgeNode) m.sender, (KadNode) m.receiver, m);
            } else{
                sendMessageBridgeToBridge((BridgeNode) m.sender, (BridgeNode) m.receiver, m);
            }
        }

    }


    //todo: fix dat "current node" (dit kan een tussennode zijn, zijn routing table updates (check oude code)
    public void sendMessageKadToKad(KadNode sender, KadNode receiver, Message m){

        sender.getRoutingTable().addNeighbour(receiver);

        Node src = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

        //if it is a ROUTE message, we also set a timeout
        if (m.getType() == Message.MSG_ROUTE) {

            Message timeout = new Message(Message.TIMEOUT, m.operationId, receiver, sender);

            // set delay at 2*RTT
            long latency = transport.getLatency(src, dest);
            long delay = 4*latency;

            System.err.println(" we are in the message sender class and we know this message is a ROUTE message");

            // add to sent msg
            sender.getSentMsgTracker().put(m.msgId, m.timestamp);
            EDSimulator.add(delay, timeout, src, this.kademliaid);
        }
    }

    private void sendMessageKadToBridge(KadNode sender, BridgeNode receiver, Message m){
        Node src = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

        if (m.getType() == Message.MSG_ROUTE) { // is a request

            Message timeout = new Message(Message.TIMEOUT, m.operationId, receiver, sender);

            // set delay at 2*RTT
            long latency = transport.getLatency(src, dest);
            long delay = 4*latency;

            // add to sent msg
            sender.getSentMsgTracker().put(m.msgId, m.timestamp);
            EDSimulator.add(delay, timeout, src, this.kademliaid);
        }
    }

    private void sendMessageBridgeToKad(BridgeNode s, KadNode r, Message m){
    }

    private void sendMessageBridgeToBridge(BridgeNode s, BridgeNode r, Message m){

    }

}
