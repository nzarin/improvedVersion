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

    private final double p_intra;

    /**
     * Constructor that links the CONTROL and PROTOCOL objects.
     *
     * @param prefix
     */
    public TrafficGenerator(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
        p_intra = KademliaCommonConfig.PERCENTAGE_INTRA;
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


    /**
     * Create a intra domain lookup if variable intra is true. Otherwise, create inter-domain lookup.
     *
     * @param intra
     */
    private void createLookup(boolean intra) {
        Node source;
        Node target;
        KademliaNode sourceKadNode;
        KademliaNode targetKadNode;

        if (intra) {
            //create intra domain lookup
            do {
                source = Network.get(CommonState.r.nextInt(Network.size()));
                target = Network.get(CommonState.r.nextInt(Network.size()));
                KademliaProtocol kadProtocolSource = (KademliaProtocol) source.getProtocol(pid);
                KademliaProtocol kadProtocolTarget = (KademliaProtocol) target.getProtocol(pid);
                sourceKadNode = kadProtocolSource.getCurrentNode();
                targetKadNode = kadProtocolTarget.getCurrentNode();
            } while ((source == null) || (target == null) || (!source.isUp()) || (!target.isUp()) || (sourceKadNode instanceof BridgeNode) || (targetKadNode instanceof BridgeNode) || (sourceKadNode.getNodeId() == targetKadNode.getNodeId()) || sourceKadNode.getDomain() != targetKadNode.getDomain());
        } else {

            //create inter-domain lookup
            do {
                source = Network.get(CommonState.r.nextInt(Network.size()));
                target = Network.get(CommonState.r.nextInt(Network.size()));
                KademliaProtocol kadProtocolSource = (KademliaProtocol) source.getProtocol(pid);
                KademliaProtocol kadProtocolTarget = (KademliaProtocol) target.getProtocol(pid);
                sourceKadNode = kadProtocolSource.getCurrentNode();
                targetKadNode = kadProtocolTarget.getCurrentNode();
            } while ((source == null) || (target == null) || (!source.isUp()) || (!target.isUp()) || (sourceKadNode instanceof BridgeNode) || (targetKadNode instanceof BridgeNode) || (sourceKadNode.getNodeId() == targetKadNode.getNodeId()) || sourceKadNode.getDomain() == targetKadNode.getDomain());
        }


        // send message
        EDSimulator.add(0, generateFindNodeMessage((KadNode) sourceKadNode, (KadNode) targetKadNode), source, pid);

    }

    /**
     * Every call of this control generates and send a random find node message
     *
     * @return boolean
     */
    public boolean execute() {

        // throw the dice
        double dice = CommonState.r.nextDouble();

        // perform the correct operation based on the probability
        createLookup(dice < p_intra);

        return false;
    }

}