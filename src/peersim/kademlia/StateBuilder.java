package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.transport.Transport;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Initialization class that performs the bootstrap filling the k-buckets of all initial nodes.
 * In particular every node is added to the routing table of every other node in the network. In the end however the various nodes
 * doesn't have the same k-buckets because when a k-bucket is full a random node in it is deleted.
 */
public class StateBuilder implements peersim.core.Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_TRANSPORT = "transport";

    private final String prefix;
    private final int kademliaid;
    private final int transportid;
    private final int amountDomains;

    public StateBuilder(String prefix) {
        this.prefix = prefix;
        kademliaid = Configuration.getPid(this.prefix + "." + PAR_PROT);
        transportid = Configuration.getPid(this.prefix + "." + PAR_TRANSPORT);
        this.amountDomains = KademliaCommonConfig.NUMBER_OF_DOMAINS;
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
     * Return the transport protocol from node with NETWORK ID i.
     *
     * @param i
     * @return
     */
    public final Transport getTr(int i) {
        return ((Transport) (Network.get(i)).getProtocol(transportid));
    }

    /**
     * Add random nodes to the routing table
     */
    private void createState(){
        int sz = Network.size();

        //for every node i in the complete network -> add 100 random nodes.
        for(int i = 0; i < sz; i++){

            Node networkNode = Network.get(i);
            KademliaProtocol iKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode iNode;

            //if i am a kadNode
            if (iKad.getCurrentNode() instanceof KadNode){
                iNode =  iKad.getCurrentNode();

                // fill the routing table with random nodes
                addRandomNodesToKadNode((KadNode) iNode, sz);

                //fill the routing table with close nodes
                int start = i;
                if(i > sz - 50){
                    start = sz - 25;
                }
                addCloseNodesToKadNode((KadNode) iNode, sz, start);
                // i am  bridge node
            } else {
                iNode = iKad.getCurrentNode();

                //fill the list
                addNodesToBridgeNode((BridgeNode) iNode, sz);
            }

        }


    }


    /**
     * Add hundred of random nodes to the routing table of iNode
     * @param iNode
     * @param networkSize
     */
    private void addRandomNodesToKadNode(KadNode iNode, int networkSize){
        ArrayList<BigInteger> alreadyAddedBridges = new ArrayList<>();

        //take 100 random nodes add to routing table or arraylist according to the rules
        for(int j = 0; j < 100; j++){
            Node randomNetworkNode = Network.get(CommonState.r.nextInt(networkSize));
            KademliaProtocol jKad = (KademliaProtocol) (randomNetworkNode.getProtocol(kademliaid));
            KademliaNode jNode;

            //if this random node is a KadNode
            if(jKad.getCurrentNode() instanceof KadNode){
                jNode = jKad.getCurrentNode();

                //if it is in the same domain -> add it to iNode its routing table
                if(iNode.getDomain() == jNode.getDomain()){
                    iNode.getRoutingTable().addNeighbour((KadNode) jNode);
                } else {
                    //retry
                    j--;
                }

                // if this random node is a BridgeNode
            } else{
                jNode = jKad.getCurrentNode();

                //if it is in the same domain AND if there is still room for bridge nodes && its available -> add it to iNodes list of bridge nodes
                if((iNode.getDomain() == jNode.getDomain()) && iNode.getBridgeNodes().size() < KademliaCommonConfig.NUMBER_OF_BRIDGES_PER_DOMAIN && !alreadyAddedBridges.contains(jNode.getNodeId())){
                    iNode.getBridgeNodes().add((BridgeNode) jNode);
                    alreadyAddedBridges.add(jNode.getNodeId());
                }

                // retry always because we want 100 random kad nodes
                j--;
            }
        }
    }

    /**
     * Add hundred of random nodes to the routing table of iNode
     * @param iNode
     * @param networkSize
     */
    private void addCloseNodesToKadNode(KadNode iNode, int networkSize, int start){
        ArrayList<BigInteger> alreadyAddedBridges = new ArrayList<>();

        //take 50 close nodes add to routing table or arraylist according to the rules
        for(int j = 0; j < 50; j++){
            start++;

            if(start > 0 && start < networkSize){
                Node neighbourNetworkNode = Network.get(start++);
                KademliaProtocol jKad = (KademliaProtocol) (neighbourNetworkNode.getProtocol(kademliaid));
                KademliaNode jNode;

                //if this random node is a KadNode
                if(jKad.getCurrentNode() instanceof KadNode){
                    jNode = jKad.getCurrentNode();

                    //if it is in the same domain -> add it to iNode its routing table
                    if(iNode.getDomain() == jNode.getDomain()){
                        iNode.getRoutingTable().addNeighbour((KadNode) jNode);
                    } else {
                        //retry
                        j--;
                    }

                    // this must be a bridge node then
                } else{
                    jNode = jKad.getCurrentNode();

                    //if it is in the same domain AND if there is still room for bridge nodes && its available -> add it to iNodes list of bridge nodes
                    if(iNode.getDomain() == jNode.getDomain() && iNode.getBridgeNodes().size() < KademliaCommonConfig.NUMBER_OF_BRIDGES_PER_DOMAIN && ((BridgeNode) jNode).isAvailable()){
                        iNode.getBridgeNodes().add((BridgeNode) jNode);
                        alreadyAddedBridges.add(jNode.getNodeId());
                    }
                    // retry always because we want 50 close kad nodes
                    j--;
                }
            }
            }

    }


    /**
     * Add nodes to bridge node i.
     * @param iNode
     * @param networkSize
     */
    private void addNodesToBridgeNode(BridgeNode iNode, int networkSize){

        //iterate over all the network nodes and add all bridge nodes to the routing table and all domain kadnode to list
        for(int j=0; j < networkSize; j++){

            Node networkNode = Network.get(j);
            KademliaProtocol jKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KademliaNode jNode;

            //if this j node is a kadNode
            if (jKad.getCurrentNode() instanceof KadNode){
                jNode = jKad.getCurrentNode();

                //if this kadNode is in the same domain -> add to list of kad nodes. Otherwise, ignore it.
                if(jNode.getDomain() == iNode.getDomain()) {
                    iNode.getKadNodes().add((KadNode) jNode);
                }

                // This j node must be a bridge node -> add to the list of bridge nodes
            } else {
                jNode = jKad.getCurrentNode();
                if(iNode.getNodeId() != jNode.getNodeId()){
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
        Network.sort(new Comparator<Node>() {

            public int compare(Node o1, Node o2) {
                Node n1 = o1;
                Node n2 = o2;

                KademliaProtocol pr1 = (KademliaProtocol) (n1.getProtocol(kademliaid));
                KademliaProtocol pr2 = (KademliaProtocol) (n2.getProtocol(kademliaid));
                return Util.put0(pr1.getCurrentNode().getNodeId()).compareTo(Util.put0(pr2.getCurrentNode().getNodeId()));
            }

        });

        createState();

        System.err.println("Routing tables are filled to their domains");

        return false;

    }

}
