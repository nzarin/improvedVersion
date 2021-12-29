package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * This control initializes the whole network (that was already created by peersim) assigning a randomly generated
 * unique NodeId to every node.
 */
public class CustomDistribution implements peersim.core.Control {

    //initializers
    private static final String PAR_PROT = "protocol";
    private final int protocolID;
    private final UniformRandomGenerator urg;
    protected TreeMap<Long, BigInteger> mapNIDoPID;

    /**
     * Constructor that links the Controller and Protocol IDs and creates a uniform random generator.
     *
     * @param prefix from the config file
     */
    public CustomDistribution(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
        urg = new UniformRandomGenerator(KademliaCommonConfig.BITS, CommonState.r);
        mapNIDoPID = new TreeMap<Long, BigInteger>();
    }

    /**
     * Scan over the nodes in the network and assign a randomly generated NodeId in the space 0..2^BITS, where BITS is a parameter
     * from the kademlia protocol (usually 160)
     *
     * @return boolean always false
     */
    public boolean execute() {

        System.err.println();
        System.err.println("Assigning kademlia node identifiers to nodes in the network:");

        BigInteger tmp;
        for (int i = 0; i < Network.size(); ++i) {
            tmp = urg.generate();
            if (!mapNIDoPID.containsValue(tmp)) {
                System.err.println("Network node " + Network.get(i).getID() + " gets assigned Node ID : " + tmp + " for (kademlia) protocol with ID " + protocolID);
                ((KademliaProtocol) (Network.get(i).getProtocol(protocolID))).setNodeId(tmp);
                mapNIDoPID.put(Network.get(i).getID(), tmp);
            } else {
                // set i back with 1 to retry
                i--;
            }
        }

        System.err.println();

        return false;
    }


}
