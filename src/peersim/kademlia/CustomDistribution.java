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
    private int protocolID;
    private UniformRandomGenerator urg;
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

        System.err.println("");
        System.err.println("Assigning kademlia node identifiers to nodes in the network:");

        BigInteger tmp;
        for (int i = 0; i < Network.size(); ++i) {
            tmp = urg.generate();
            System.err.println("Network node " + Network.get(i).getID() + " gets assigned Node ID : " + tmp + " for (kademlia) protocol with ID " + protocolID);
            mapNIDoPID.put(Network.get(i).getID(), tmp);
            ((KademliaProtocol) (Network.get(i).getProtocol(protocolID))).setNodeId(tmp);
        }

        System.err.println("");

        //HARD CODED IDS FOR EASY EXAMPLE
//        ((KademliaProtocol) (Network.get(0).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(1));
//        ((KademliaProtocol) (Network.get(1).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(4));
//        ((KademliaProtocol) (Network.get(2).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(5));
//        ((KademliaProtocol) (Network.get(3).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(6));
//        ((KademliaProtocol) (Network.get(4).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(12));
//        ((KademliaProtocol) (Network.get(5).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(13));
//        ((KademliaProtocol) (Network.get(6).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(15));
//        ((KademliaProtocol) (Network.get(7).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(16));
//        ((KademliaProtocol) (Network.get(8).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(18));
//        ((KademliaProtocol) (Network.get(9).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(19));
//        ((KademliaProtocol) (Network.get(10).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(34));
//        ((KademliaProtocol) (Network.get(11).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(43));
//        ((KademliaProtocol) (Network.get(12).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(49));
//        ((KademliaProtocol) (Network.get(13).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(60));
//        ((KademliaProtocol) (Network.get(14).getProtocol(protocolID))).setNodeId(BigInteger.valueOf(62));

        return false;
    }

    /**
     * Get the map from network Node ID to protocol Node ID.
     * @return
     */
    public TreeMap<Long, BigInteger> getIDMap(){
        return this.mapNIDoPID;
    }

    /**
     * For a given network node ID, give the corresponding protocol node ID.
     * @param network_nodeID
     * @return
     */
    public BigInteger getCorrespondingProtocol_NodeID(Long network_nodeID){
        return mapNIDoPID.get(network_nodeID);
    }




}
