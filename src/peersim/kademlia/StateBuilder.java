package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.transport.Transport;

import java.math.BigInteger;
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

//                KademliaProtocol p1 = (KademliaProtocol) (n1.getProtocol(kademliaid));
//                KademliaProtocol p2 = (KademliaProtocol) (n2.getProtocol(kademliaid));
//                return Util.put0(p1.nodeId).compareTo(Util.put0(p2.nodeId));

                KademliaProtocol pr1 = (KademliaProtocol) (n1.getProtocol(kademliaid));
                KademliaProtocol pr2 = (KademliaProtocol) (n2.getProtocol(kademliaid));
                return Util.put0(pr1.getKadNode().getNodeId()).compareTo(Util.put0(pr2.getKadNode().getNodeId()));
            }

        });

        int sz = Network.size();

        //for every node in every domain, take 100 random nodes in the same domain and add to k-bucket of it
        for(int i = 0; i < sz; i++){
            Node networkNode = Network.get(i);
            KademliaProtocol iKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KadNode iNode = iKad.getKadNode();

            for(int j = 0; j < 100; j++){
                Node randomNetworkNode = Network.get(CommonState.r.nextInt(sz));
                KademliaProtocol jKad = (KademliaProtocol) (randomNetworkNode.getProtocol(kademliaid));
                KadNode jNode = jKad.getKadNode();

                //add if it's in the same domain
                if(iNode.getDomain() == jNode.getDomain()){
                    iNode.getRoutingTable().addNeighbour(jNode);
                } else {
                    j--;
                }

            }
        }



        // add other 50 near nodes
        for(int i =0; i < sz; i++){
            Node networkNode = Network.get(i);
            KademliaProtocol iKad = (KademliaProtocol) (networkNode.getProtocol(kademliaid));
            KadNode iNode = iKad.getKadNode();

            int start = i;
            if (i > sz - 50){
                start = sz - 25;
            }
            for(int j =0; j < 50; j++){
                start = start++;

                if(start > 0 && start < sz){
                    Node neighbour = Network.get(start++);
                    KademliaProtocol jKad = (KademliaProtocol) neighbour.getProtocol(kademliaid);
                    KadNode jNode = jKad.getKadNode();

                    if(iNode.getDomain() == jNode.getDomain()){
                        iNode.getRoutingTable().addNeighbour(jNode);
                    } else {
                        j--;
                    }
                }
            }
        }

        System.err.println("Routing tables are filled to their domains");

        return false;

    }

}
