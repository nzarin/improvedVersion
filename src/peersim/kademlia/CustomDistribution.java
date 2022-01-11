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
    private static final String PAR_NMR_DOMAINS = "nmrdomains";
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
        
        for (int i = 0; i < Network.size(); ++i) {
            //create a KadNode
            BigInteger tmpID = urg.generateID();
            int tmpDomain = urg.selectDomain();
            KadNode node = new KadNode(tmpID, tmpDomain);
            node.getRoutingTable().setOwnerKadNode(node);
            if (!mapNIDoPID.containsValue(tmpID)) {
                System.err.println("Network node " + Network.get(i).getID() + " gets assigned Node ID : " + tmpID + " for domain " + tmpDomain );
                ((KademliaProtocol) (Network.get(i).getProtocol(protocolID))).setNode(node);
                mapNIDoPID.put(Network.get(i).getID(), tmpID);
            } else {
                // set i back with 1 to retry
                i--;
            }
        }

        System.err.println();

        return false;
    }


}
