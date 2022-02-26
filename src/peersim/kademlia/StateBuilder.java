package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Objects;

/**
 * Initialization class that performs the bootstrap filling the k-buckets of all initial nodes.
 * In particular every node is added to the routing table of every other node in the network. In the end however the various nodes
 * doesn't have the same k-buckets because when a k-bucket is full a random node in it is deleted.
 */
public class StateBuilder implements peersim.core.Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_FRACTION_ADVERSARIAL = "fraction_kappa";

    private final int kademliaid;
    private final double fraction_kappa;

    public StateBuilder(String prefix) {
        this.kademliaid = Configuration.getPid(prefix + "." + PAR_PROT);
        this.fraction_kappa = Configuration.getDouble(prefix + "." + PAR_FRACTION_ADVERSARIAL);
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

        //for every node i in the complete network
        for (int i = 0; i < sz; i++) {

            Node networkNode = Network.get(i);
            KademliaProtocol iKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode iNode = iKad.getCurrentNode();

            //if i is a kadNode
            if (iNode instanceof KadNode) {

                // fill the routing table with random nodes
                addRandomKadNodesToKadNode((KadNode) iNode);

                //fill the routing table with close nodes
                int start = i;
                if (i > sz - 50) {
                    start = sz - 25;
                }
                addCloseKadNodesToKadNode((KadNode) iNode, start);

                // add all the relevant bridge nodes to this kad node
                addBridgeAndColludeNodesToKadNode((KadNode) iNode);

                // i is a bridge node
            } else {

                //fill the list
                addNodesToBridgeNode((BridgeNode) iNode);
            }

        }


    }


    /**
     * Add hundred of random nodes to the routing table of iNode
     *
     * @param iNode
     */
    private void addRandomKadNodesToKadNode(KadNode iNode) {
        //take 100 random nodes add to routing table or arraylist according to the rules
        for (int j = 0; j < 100; j++) {
            Node randomNetworkNode = Network.get(CommonState.r.nextInt(Network.size()));
            KademliaProtocol jKad = (KademliaProtocol) (randomNetworkNode.getProtocol(kademliaid));
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

                // if this random node is a BridgeNode -> retry
            } else {
                j--;
            }
        }
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
            } else {

                if (iNode.getNodeId() != jNode.getNodeId()) {
                    iNode.getBridgeNodes().add((BridgeNode) jNode);
                }

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
