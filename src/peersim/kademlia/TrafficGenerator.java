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
    private Message generateFindNodeMessage(KadNode source) {
        Message m = Message.makeEmptyMessage("Automatically Generated Traffic", Message.MSG_FINDNODE);
        m.timestamp = CommonState.getTime();
        KadNode randomTargetNode = selectRandomKadNode();
        m.src = source;
        m.sender = source;
        m.receiver = source;
        m.target = randomTargetNode;
        m.newLookup = true;
        System.err.println("we created a new findnode message in traffic  generator");
        System.err.println("the operationId of this message is: " + m.operationId);
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
        Node start;
        KademliaNode kNode;
        do {
            start = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol kademliaProtocol = (KademliaProtocol) start.getProtocol(pid);
            kNode = kademliaProtocol.getCurrentNode();
        } while ((start == null) || (!start.isUp()) || kNode instanceof BridgeNode);

        // send message
        EDSimulator.add(0, generateFindNodeMessage((KadNode) kNode), start, pid);

        return false;
    }

}