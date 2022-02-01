package peersim.kademlia;

import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;
import peersim.transport.UnreliableTransport;

/**
 * This class allows a Kademlia node to send messages.
 */
public class MessageSender {

    private final int kademliaid;
    private final int transportid;
    private Transport transport;

    /**
     * Constructs the message sender.
     *
     * @param kademliaid
     * @param transportid
     */
    public MessageSender(int kademliaid, int transportid) {
        this.kademliaid = kademliaid;
        this.transportid = transportid;
    }


    /**
     * Determines what type of send operation it will be and then sends it.
     *
     * @param m
     */
    public void sendMessage(Message m) {

        //determine what type of message and then send it
        if (m.sender instanceof KadNode) {
            if (m.receiver instanceof KadNode) {
                sendMessageKadToKad((KadNode) m.sender, (KadNode) m.receiver, m);
            } else {
                sendMessageKadToBridge((KadNode) m.sender, (BridgeNode) m.receiver, m);
            }
        } else {
            if (m.receiver instanceof KadNode) {
                sendMessageBridgeToKad((BridgeNode) m.sender, (KadNode) m.receiver, m);
            } else {
                sendMessageBridgeToBridge((BridgeNode) m.sender, (BridgeNode) m.receiver, m);
            }
        }

    }


    /**
     * Send a message from a KadNode to a KadNode
     *
     * @param sender
     * @param receiver
     * @param m
     */
    private void sendMessageKadToKad(KadNode sender, KadNode receiver, Message m) {

        //retrieve the relevant network nodes
        Node s = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node r = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        //Send over transport layer
        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(s, r, m, kademliaid);

        //if it is a ROUTE message, we also set a timeout
        if (m.getType() == Message.MSG_REQUEST) {

            Message timeout = new Message(Message.TIMEOUT, m.msgId, receiver, sender);

            // set delay at 2*RTT
            long latency = transport.getLatency(s, r);
            long delay = 4 * latency;

            // add to sent msg
            sender.getSentMsgTracker().put(m.msgId, m.timestamp);
            EDSimulator.add(delay, timeout, s, this.kademliaid);
        }
    }

    /**
     * Send a message from a KadNode to a BridgeNode
     *
     * @param sender
     * @param receiver
     * @param m
     */
    private void sendMessageKadToBridge(KadNode sender, BridgeNode receiver, Message m) {

        Node src = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

    }

    /**
     * Send a message from a BridgeNode to a KadNode
     *
     * @param sender
     * @param receiver
     * @param m
     */
    private void sendMessageBridgeToKad(BridgeNode sender, KadNode receiver, Message m) {

        Node src = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

    }

    /**
     * Send a message from a BridgeNode to a BridgeNode
     *
     * @param sender
     * @param receiver
     * @param m
     */
    private void sendMessageBridgeToBridge(BridgeNode sender, BridgeNode receiver, Message m) {

        Node src = Util.nodeIdtoNode(sender.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(receiver.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportid);
        transport.send(src, dest, m, kademliaid);

    }

}
