package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * This control initializes the whole network (that was already created by peersim) assigning a randomly generated
 * unique NodeId to every node.
 */
public class CustomDistribution2 implements peersim.core.Control {

    //initializers
    private static final String PAR_PROT = "protocol";
    private static final String PAR_FRACTION_ADVERSARIAL = "fraction_f";
    private static final String PAR_FRACTION_OCTOPUSNODES = "fraction_octopusnodes";
    private final int protocolID;
    private final UniformRandomGenerator urg;
    private final TreeMap<Long, BigInteger> mapNIDoPID;
    private final int numberOfDomains;
    private final double fractionAdversarial;
    private final double fractionOctopusNodes;
    public ArrayList<Domain> domains;
    private int currentIndexNetworkNode;


    /**
     * Constructor that links the Controller and Protocol IDs and creates a uniform random generator.
     *
     * @param prefix from the config file
     */
    public CustomDistribution2(String prefix) {
        this.protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
        this.fractionAdversarial = Configuration.getDouble(prefix + "." + PAR_FRACTION_ADVERSARIAL);
        this.fractionOctopusNodes = Configuration.getDouble(prefix + "." + PAR_FRACTION_OCTOPUSNODES);
        this.urg = new UniformRandomGenerator(KademliaCommonConfig.BITS, CommonState.r);
        this.mapNIDoPID = new TreeMap<>();
        this.numberOfDomains = KademliaCommonConfig.NUMBER_OF_DOMAINS;
        this.currentIndexNetworkNode = 0;
        this.domains = new ArrayList<>();
    }

    /**
     * Generates Kadnodes in the network.
     */
    private void generateKadNodes() {
        //determine number of KadNodes in the network
//        int amountKadNodes = Network.size() - ((int) Math.round(Network.size() * fractionOctopusNodes));
        int numberOfAdversarialNodes = (int) Math.round(Network.size() * fractionAdversarial);

        //create them
        for (int i = 0; i < Network.size(); ++i) {
            BigInteger tmpID = urg.generateID();
            if (!mapNIDoPID.containsValue(tmpID)) {
                KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(currentIndexNetworkNode).getProtocol(protocolID);
                KademliaNode kadNode = new KadNode(tmpID, selectRandomDomain(), kademliaProtocol, selectRandomRole());
                kademliaProtocol.setKadNode((KadNode) kadNode);
                mapNIDoPID.put(Network.get(currentIndexNetworkNode).getID(), tmpID);

                //determine whether this node has to be an adversarial
                if (currentIndexNetworkNode < numberOfAdversarialNodes) {
                    ((KadNode) kadNode).makeMalicious();
                }

                //determine whether this node has to be an octopus
                double dice = CommonState.r.nextDouble();
                if(dice < fractionOctopusNodes){
                    ((KadNode) kadNode).giveRole(Role.OCTOPUS);
                } else {
                    ((KadNode) kadNode).giveRole(Role.NORMAL);
                }

                //update index
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

        int numberOfBridgeNodes = (int) Math.round(Network.size() * fractionOctopusNodes);
        for (int j = 0; j < numberOfBridgeNodes; j++) {
            BigInteger tmpId = urg.generateID();
            if (!mapNIDoPID.containsValue(tmpId)) {
                KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(currentIndexNetworkNode).getProtocol(protocolID);
                KademliaNode bridgeNode = new BridgeNode(tmpId, selectRandomDomain(), kademliaProtocol);
                kademliaProtocol.setBridgeNode((BridgeNode) bridgeNode);
                mapNIDoPID.put(Network.get(currentIndexNetworkNode).getID(), tmpId);
                currentIndexNetworkNode++;
            } else {
                j--;
            }
        }
    }

    private void createDomains() {
        int domainCounter = 0;
        do {
            Domain dom = new Domain(urg.generateID());
            if(!domains.contains(dom)){
                domains.add(dom);
                domainCounter++;
            }
        } while (domainCounter < numberOfDomains);
    }

    private void acquaintDomains(){
        //for every domain
        for(int i = 0; i < domains.size(); i++){

            //add every other domain
            for(int j = 0; j < domains.size(); j++){

                if (i != j){
                    domains.get(i).addDomain(domains.get(j));
                }
            }
        }
    }

    private Domain selectRandomDomain() {
        return domains.get(CommonState.r.nextInt(domains.size()));
    }

    private Role selectRandomRole() {
        // throw the dice
        double dice = CommonState.r.nextDouble();
        if (dice <= fractionOctopusNodes) return Role.OCTOPUS;
        else return Role.NORMAL;
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

        //create domains
        createDomains();

        //acquaint domains
        acquaintDomains();

        // create normal nodes
        generateKadNodes();

//        //create bridge nodes
//        generateBridgeNodes();

        System.err.println();

        return false;
    }


}
