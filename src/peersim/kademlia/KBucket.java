package peersim.kademlia;

import peersim.core.CommonState;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * This class implements a kademlia k-bucket. Function for the management of the neighbours update are also implemented
 */
public class KBucket implements Cloneable {

    /**
     * The k-bucket array.
     */
    protected LinkedHashMap<KadNode, Long> neighbours = null;

    /**
     * Empty constructor.
     */
    public KBucket() {
        neighbours = new LinkedHashMap<>();
    }



    /**
     * Add a neighbour to this k-bucket.
     *
     * @param node The to-be added neighbour.
     */
    public void addNeighbour(KadNode node) {
        long time = CommonState.getTime();

        // if the k-bucket isn't full, add neighbour to tail of the list.
        if (neighbours.size() < KademliaCommonConfig.K) { // k-bucket isn't full
            neighbours.put(node, time);
        }
    }


    /**
     * Remove a neighbour from this k-bucket.
     *
     * @param node The to-be removed neighbour.
     */
    public void removeNeighbour(KadNode node) {
        neighbours.remove(node);
    }


    /**
     * Cloning of the KBucket.
     *
     * @return
     */
    public Object clone() {
        KBucket dolly = new KBucket();
        for (KadNode node : neighbours.keySet()) {
            KadNode dupl = new KadNode(new BigInteger(node.getNodeId().toByteArray()), node.getDomain());
//            dupl.setNodeId(new BigInteger(node.getNodeId().toByteArray()), 0l);
            dolly.neighbours.put(dupl, 0l);
        }
        return dolly;
    }

    /**
     * Get the KBucket.
     *
     * @return Current KBucket.
     */
    public LinkedHashMap<KadNode, Long> getKBucket() {
        return this.neighbours;
    }

    /**
     * Print the present KBucket.
     *
     * @return
     */
    public String toString() {
        System.err.println("we reach the toString of KBucket class");
        String res = "{\n";

        for (KadNode node : neighbours.keySet()) {
            res += node.toString2() + "\n";
        }

        return res + "}";
    }
}
