package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.util.*;

/**
 * Initialization class that performs the bootstrap filling the k-buckets of all initial nodes.
 * In particular every node is added to the routing table of every other node in the network. In the end however the various nodes
 * doesn't have the same k-buckets because when a k-bucket is full a random node in it is deleted.
 */
public class StateBuilder2 implements peersim.core.Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_FRACTION_KAPPA = "fraction_kappa";

    private final int kademliaid;
    private final double fraction_kappa;

    public StateBuilder2(String prefix) {
        this.kademliaid = Configuration.getPid(prefix + "." + PAR_PROT);
        this.fraction_kappa = Configuration.getDouble(prefix + "." + PAR_FRACTION_KAPPA);
    }

    /**
     * Print object for comparison.
     *
     * @param o
     */
    public static void o(Object o) {
        System.out.println(o);
    }

    /**
     * Return the kademlia protocol from node with NETWORK ID i.
     *
     * @param i
     * @return
     */
    public final KademliaProtocol get(int i) {
        return ((KademliaProtocol) (Network.get(i)).getProtocol(kademliaid));
    }


    /**
     * Add random nodes to the routing table
     */
    private void createState() {
        int sz = Network.size();

        //for every node i in the network
        for (int i = 0; i < sz; i++) {

            Node networkNode = Network.get(i);
            KademliaProtocol iKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode iNode = iKad.getCurrentNode();

            //if i is a kadNode
            if (iNode instanceof KadNode) {


                // fill the routing table with random nodes
                addSameDomainRandomKadNodesToKadNode((KadNode) iNode);

                //fill the routing table with close nodes
                int start = i;
                if (i > sz - 50) {
                    start = sz - 25;
                }
                addCloseKadNodesToKadNode((KadNode) iNode, start);

                // add all the relevant bridge nodes to this kad node
                addBridgeAndColludeNodesToKadNode((KadNode) iNode);

                //if it is an octopus -> add other-domain nodes to routing table
                if((iNode).getRole() == Role.OCTOPUS){
                    addRandomKadNodesToOctopusKadNode((KadNode) iNode);
                }

                // i is a bridge node
            } else {

                //fill the list
                addNodesToBridgeNode((BridgeNode) iNode);
            }

        }


    }

    /**
     * Add 50 of random nodes to the routing table of iNode
     *
     * @param iNode
     */
    private void addRandomKadNodesToOctopusKadNode(KadNode iNode) {
        //select random node in order to retrieve domain info
        KademliaNode randomNode;
        do{
            Node randomNetworkNode = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol randomKad = (KademliaProtocol) (randomNetworkNode.getProtocol(kademliaid));
            randomNode = randomKad.getCurrentNode();
        } while(randomNode instanceof BridgeNode);

        // sort the domain array in ascending distance order
        ArrayList<Domain> otherDomains = randomNode.getDomain().getOtherDomains();
        otherDomains.sort(Comparator.comparing(o -> Util.put0(Util.distance(o.getDomainId(), iNode.getNodeId()))));


        //sample 100 random kad nodes from other domains
        HashMap<Integer, ArrayList<KadNode>> nodes_other_domain = new HashMap<>();

        //initialize the nodes_other_domain list such that every ith key displays the ith domain id from the sorted list
        for(int i = 0; i < otherDomains.size(); i++){
            nodes_other_domain.put(i, new ArrayList<>());
        }

        //hashmap looks like this:
        // ----------------------------
        // 0 | nodes from closest domain
        // 1 | nodes from second closest domain
        // ..|
        // n | nodes from farthest domain

        int nmrAddedNodes = 0;
        do{
            Node randNode = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol jKad = (KademliaProtocol) (randNode.getProtocol(kademliaid));
            KademliaNode jNode = jKad.getCurrentNode();

            //if this random node is a KadNode and its from another domain
            if ((jNode instanceof KadNode) && otherDomains.contains(jNode.getDomain())) {

                // determine what domain it is and which index it should get
                int index = otherDomains.indexOf(jNode.getDomain());

                // add it to this index in the hashmap
                nodes_other_domain.get(index).add((KadNode) jNode);

                //increment counter
                nmrAddedNodes++;
            }

        } while (nmrAddedNodes < 200);      //should be large enough to prevent null pointers


        // now add 50 nodes from other domains to routing table of iNode with a bias towards nodes from closer domains
        for (int i = 0; i < 50; i++){
            System.err.println("otherDomain.size() : " + otherDomains.size());
            int domainIndex = chooseBiasedIndex(0, otherDomains.size()-1);
            System.err.println("biased domainIndex: " + domainIndex);

            ArrayList<KadNode> nodes = nodes_other_domain.get(domainIndex);
            System.err.println("nodes.size(): " + nodes.size());
            //select random node from this arraylist
            KadNode node = nodes.get(chooseRandomIndex(0, nodes.size()-1));

            //add to iNode routing table
            iNode.getRoutingTable().addNeighbourOfOctopus(node);
        }
    }


    private int chooseBiasedIndex(int min, int max){
        return (int) Math.floor(Math.abs(Math.random() - Math.random()) * (1 + max - min) + min);
    }

    private int chooseRandomIndex(int min, int max){
        return (int)(Math.random() * ((max - min) + 1)) + min;
    }


    /**
     * Add hundred of random nodes to the routing table of iNode
     *
     * @param iNode
     */
    private void addSameDomainRandomKadNodesToKadNode(KadNode iNode) {
        int nmrAddedNodes = 0;

        do {
            Node randomNetworkNode = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol jKad = (KademliaProtocol) (randomNetworkNode.getProtocol(kademliaid));
            KademliaNode jNode = jKad.getCurrentNode();

            if(jNode instanceof KadNode && (iNode.getDomain().getDomainId() == jNode.getDomain().getDomainId()) ){
                iNode.getRoutingTable().addNeighbour((KadNode) jNode);
                nmrAddedNodes++;
            }

        } while (nmrAddedNodes < 100);

    }

    /**
     * Add bridge nodes to Kad Node's list B.
     * @param iNode
     */
    public void addBridgeAndColludeNodesToKadNode(KadNode iNode) {
        //iterate over entire network
        for (int j = 0; j < Network.size(); j++) {
            Node networkNode = Network.get(j);
            KademliaProtocol jProt = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode jNode = jProt.getCurrentNode();

            // if this node is a bridge node, and we have space left, then add it to list B
            if ((jNode instanceof BridgeNode) && (iNode.getDomain() == jNode.getDomain())) {
                iNode.getBridgeNodes().add((BridgeNode) jNode);
                //if both nodes are malicious, then add it to the list of colluders
            } else if(iNode.isMalicious() && jNode.isMalicious()){
                iNode.getColluders().add((KadNode) jNode);
            }
        }
    }


    /**
     * Add hundred of random nodes to the routing table of iNode
     *
     * @param iNode
     */
    private void addCloseKadNodesToKadNode(KadNode iNode, int start) {

        //take 50 close nodes add to routing table or arraylist according to the rules
        for (int j = 0; j < 50; j++) {
            start++;

            if (start > 0 && start < Network.size()) {
                Node neighbourNetworkNode = Network.get(start++);
                KademliaProtocol jKad = (KademliaProtocol) (neighbourNetworkNode.getProtocol(kademliaid));
                KademliaNode jNode;

                //if this random node is a KadNode
                if (jKad.getCurrentNode() instanceof KadNode) {
                    jNode = jKad.getCurrentNode();

                    //if it is in the same domain -> add it to iNode its routing table
                    if (iNode.getDomain() == jNode.getDomain()) {
                        iNode.getRoutingTable().addNeighbour((KadNode) jNode);
                    } else {
                        //retry
                        j--;
                    }

                    // this must be a bridge node then
                } else {
                    j--;
                }
            }
        }

    }


    /**
     * Add nodes to bridge node i.
     *
     * @param iNode
     */
    private void addNodesToBridgeNode(BridgeNode iNode) {
        //determine size of KadNodes in this domain
        int nmrOfKadNodesTotal = Network.size() - (KademliaCommonConfig.NUMBER_OF_DOMAINS * KademliaCommonConfig.NUMBER_OF_BRIDGES_PER_DOMAIN);
        int nmrOfKadNodesThisDomain = nmrOfKadNodesTotal / KademliaCommonConfig.NUMBER_OF_DOMAINS;
        int nmrOfKnownKadNodesToBridgeNode =  (int) Math.round( nmrOfKadNodesThisDomain * fraction_kappa);

        //iterate over all the network nodes and add all bridge nodes to the routing table and all domain kadnode to list
        for (int j = 0; j < Network.size(); j++) {

            Node networkNode = Network.get(j);
            KademliaProtocol jKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode jNode = jKad.getCurrentNode();

            //if this j node is a kadNode
            if (jNode instanceof KadNode) {

                //if this kadNode is in the same domain && if there is room in list K -> add to the lists. Otherwise, ignore it.
                if (jNode.getDomain() == iNode.getDomain() && (iNode.getKadNodes().size() < nmrOfKnownKadNodesToBridgeNode)) {
                    iNode.getKadNodes().add((KadNode) jNode);
                }

                // This j node must be a bridge node -> add to the list of bridge nodes
            } else if (!(iNode.getNodeId() == jNode.getNodeId())) {
                iNode.getBridgeNodes().add((BridgeNode) jNode);
            }

        }
    }

    /**
     * Every call of this control performs bootstrapping procedure.
     *
     * @return
     */
    public boolean execute() {

        // Sort the network by nodeId (Ascending)
        Network.sort((o1, o2) -> {

            KademliaProtocol pr1 = (KademliaProtocol) (o1.getProtocol(kademliaid));
            KademliaProtocol pr2 = (KademliaProtocol) (o2.getProtocol(kademliaid));
            return Util.put0(pr1.getCurrentNode().getNodeId()).compareTo(Util.put0(pr2.getCurrentNode().getNodeId()));
        });

        createState();

        System.err.println("Routing tables are filled to their domains");

        return false;

    }

}
