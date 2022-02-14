package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Gives an implementation for the routing table component of a kademlia node.
 */
public class RoutingTable implements Cloneable {

    /**
     * ID of the node that has this routing table.
     */
    public KademliaNode owner = null;

    /**
     * K-Buckets of this node.
     */
    public TreeMap<Integer, KBucket> k_buckets;

    /**
     * Instantiates a new empty routing table with the specified size
     */
    public RoutingTable(KademliaNode owner) {
        this.owner = owner;
        k_buckets = new TreeMap<>();
        // initialize k-bukets
        for (int i = 0; i <= KademliaCommonConfig.BITS; i++) {
            k_buckets.put(i, new KBucket());
        }
    }


    /**
     * Add a neighbour to the correct k-bucket.
     *
     * @param neighbour The node to be added.
     */
    public void addNeighbour(KadNode neighbour) {

        // get the length of the longest common prefix (correspond to the correct k-bucket)
        int prefix_len = Util.prefixLen(owner.getNodeId(), neighbour.getNodeId());

        // add the node to the corresponding k-bucket
        k_buckets.get(prefix_len).addNeighbour(neighbour);
    }


    /**
     * Remove a neighbour from the correct k-bucket.
     *
     * @param node The node to be removed.
     */
    public void removeNeighbour(KadNode node) {

        // get the length of the longest common prefix (correspond to the correct k-bucket)
        int prefix_len = Util.prefixLen(this.owner.getNodeId(), node.getNodeId());

        // add the node to the k-bucket
        k_buckets.get(prefix_len).removeNeighbour(node);
    }


    /**
     * Return the closest neighbour to a key from the correct k-bucket.
     *
     * @param target The ID we are looking up
     * @param source The original requester of this lookup
     * @return The k closest neighbors to the search key
     */
    public KadNode[] getKNeighbours(final KadNode target, final KadNode receiver, final KadNode source) {

        KadNode[] result = new KadNode[KademliaCommonConfig.K];

        // create a map (distance, node)
        TreeMap<BigInteger, KadNode> distance_map = new TreeMap<>();
        TreeMap<BigInteger, KadNode> distance_map_colluders = new TreeMap<>();

        for(int i=0; i < KademliaCommonConfig.BITS; i++){
            for(KadNode node : k_buckets.get(i).neighbours.keySet()){
                //add the collaborator malicious node if there is place in the array list
                if(!source.isMalicious() && receiver.isMalicious() && node.isMalicious()){
                    distance_map_colluders.put(Util.distance((node.getNodeId()), target.getNodeId()), node);
                }
                distance_map.put(Util.distance((node.getNodeId()), target.getNodeId()), node);
            }
        }

        // this means that we should add malicious nodes to the head of the array
        if(distance_map_colluders.size() > 0){
            addMaliciousNeighbours(distance_map_colluders, result);
        }

        // fill the rest with the table with honest nodes (if there is space left)
        if(distance_map_colluders.size() < KademliaCommonConfig.K){
            addHonestNeighbours(distance_map, result, distance_map_colluders.size());
        }

        return result;
    }


    /**
     * Add honest neighbours to the list of closest neighbours to a target node
     * @param distance_map
     * @param result
     * @param start_index
     */
    private  void addHonestNeighbours(TreeMap<BigInteger, KadNode> distance_map, KadNode[] result, int start_index){
        for(int i = start_index; i < Math.min(KademliaCommonConfig.K, distance_map.size()); i++){
            result[i] = distance_map.pollFirstEntry().getValue();
        }
    }

    /**
     * Add malicious colluders to the list of closest neighbours to a target node
     * @param distance_map_colluders
     * @param result
     */
    private  void addMaliciousNeighbours(TreeMap<BigInteger, KadNode> distance_map_colluders, KadNode[] result){
        for(int i = 0; i < Math.min(KademliaCommonConfig.K, distance_map_colluders.size()); i++){
            result[i] = distance_map_colluders.pollFirstEntry().getValue();
        }
    }



    public Object clone() {
        RoutingTable dolly = new RoutingTable(this.owner);
        for (int i = 0; i < KademliaCommonConfig.BITS; i++) {
            dolly.k_buckets.put(i, new KBucket());
        }
        return dolly;
    }

    /**
     * print a string representation of the table
     *
     * @return String
     */
    public String toString() {
        String s = "";
        for (Map.Entry<Integer, KBucket> entry : k_buckets.entrySet()) {
            KBucket kBucket = entry.getValue();
            String ith_k_bucket = kBucket.toString();
            s = s + "Nodes with common prefix " + entry.getKey() + " are : " + ith_k_bucket + "\n";
        }
        return s;
    }



    /**
     * Bind the owner id (legacy code permits)
     * @param node
     */
    public void setOwnerKadNode(KadNode node) {
        this.owner = node;
    }
}