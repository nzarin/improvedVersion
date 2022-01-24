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
    private final TreeMap<Long, BigInteger> mapNIDoPID;
    private final int numberOfDomains;
    private final int numberOfBridgeNodesPerDomain;
    private int currentIndexNetworkNode;

    /**
     * Constructor that links the Controller and Protocol IDs and creates a uniform random generator.
     *
     * @param prefix from the config file
     */
    public CustomDistribution(String prefix) {
        protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
        urg = new UniformRandomGenerator(KademliaCommonConfig.BITS, CommonState.r);
        mapNIDoPID = new TreeMap<>();
        this.numberOfDomains = KademliaCommonConfig.NUMBER_OF_DOMAINS;
        this.numberOfBridgeNodesPerDomain = KademliaCommonConfig.NUMBER_OF_BRIDGES_PER_DOMAIN;
        this.currentIndexNetworkNode = 0;
    }

    /**
     * Generates Kadnodes in the network.
     */
    private void generateKadNodes() {
        //determine number of KadNodes in the network
        int amountKadNodes = Network.size() - (numberOfDomains * numberOfBridgeNodesPerDomain);

        //create them
        for (int i = 0; i < amountKadNodes; ++i) {
            BigInteger tmpID = urg.generateID();
            int tmpDomain = urg.selectDomain();
            if (!mapNIDoPID.containsValue(tmpID)) {
                KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(currentIndexNetworkNode).getProtocol(protocolID);
                KademliaNode kadNode = new KadNode(tmpID, tmpDomain, kademliaProtocol);
                kademliaProtocol.setKadNode((KadNode) kadNode);
                mapNIDoPID.put(Network.get(currentIndexNetworkNode).getID(), tmpID);
                System.err.println("Network node " + Network.get(currentIndexNetworkNode).getID() + " is a KADNODE and gets assigned Node ID : " + tmpID + " for domain " + tmpDomain);
                currentIndexNetworkNode++;
            } else {
                // set i back with 1 to retry
                i--;
            }
        }
    }

    /**
     * Generates bridge nodes in the network.
     */
    private void generateBridgeNodes() {

        //for every domain
        for (int i = 0; i < numberOfDomains; ++i) {
            //generate the necessary amount of bridge nodes
            for (int j = 0; j < numberOfBridgeNodesPerDomain; j++) {
                BigInteger tmpId = urg.generateID();
                if (!mapNIDoPID.containsValue(tmpId)) {
                    KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(currentIndexNetworkNode).getProtocol(protocolID);
                    KademliaNode bridgeNode = new BridgeNode(tmpId, i, kademliaProtocol);
                    kademliaProtocol.setBridgeNode((BridgeNode) bridgeNode);
                    mapNIDoPID.put(Network.get(currentIndexNetworkNode).getID(), tmpId);
                    System.err.println("Network node " + Network.get(currentIndexNetworkNode).getID() + " is a BRIDGENODE and gets assigned Node ID : " + tmpId + " for domain " + i);
                    currentIndexNetworkNode++;
                } else {
                    j--;
                }
            }
        }
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

        // create normal nodes
        generateKadNodes();

        //create bridge nodes
        generateBridgeNodes();

        System.err.println();

        return false;
    }


}
