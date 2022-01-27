package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

/**
 * This control generates random search traffic from nodes to random destination node.
 */
public class TrafficGenerator implements Control {

    /**
     * Protocol to act
     */
    private final static String PAR_PROT = "protocol";

    /**
     * Protocol ID to act
     */
    private final int pid;

    /**
     * Constructor that links the CONTROL and PROTOCOL objects.
     *
     * @param prefix
     */
    public TrafficGenerator(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);

    }

    /**
     * Generates a find node message, by randomly selecting the destination.
     *
     * @return Message
     */
    private Message generateFindNodeMessage(KadNode source, KadNode target) {
        Message m = Message.makeEmptyMessage("Automatically Generated Traffic", Message.MSG_FINDNODE);
        m.timestamp = CommonState.getTime();
        m.src = source;
        m.sender = source;
        m.receiver = source;
        m.target = target;
        m.newLookup = true;
        return m;
    }

    private KadNode selectRandomKadNode() {
        // existing active destination node
        Node n = Network.get(CommonState.r.nextInt(Network.size()));
        while (!n.isUp()) {
            n = Network.get(CommonState.r.nextInt(Network.size()));
        }

        // prevent that a find message is generated for a bridge node
        KademliaProtocol kademliaProtocol = (KademliaProtocol) n.getProtocol(pid);
        if (kademliaProtocol.getCurrentNode() instanceof KadNode) {
            return (KadNode) kademliaProtocol.getCurrentNode();
        }
        return selectRandomKadNode();

    }


    /**
     * Every call of this control generates and send a random find node message
     *
     * @return boolean
     */
    public boolean execute() {
        Node source;
        Node target;
        KademliaNode sourceKadNode;
        KademliaNode targetKadNode;

        do {
            source = Network.get(CommonState.r.nextInt(Network.size()));
            target = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol kadProtocolSource = (KademliaProtocol) source.getProtocol(pid);
            KademliaProtocol kadProtocolTarget = (KademliaProtocol) target.getProtocol(pid);
            sourceKadNode = kadProtocolSource.getCurrentNode();
            targetKadNode = kadProtocolTarget.getCurrentNode();
        } while ((source == null) || (target == null) || (!source.isUp()) || (!target.isUp()) || (sourceKadNode instanceof BridgeNode) || (targetKadNode instanceof BridgeNode || sourceKadNode.getNodeId() == targetKadNode.getNodeId()));

        // send message
        EDSimulator.add(0, generateFindNodeMessage((KadNode) sourceKadNode, (KadNode) targetKadNode), source, pid);

        return false;
    }

}