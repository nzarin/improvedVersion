package peersim.kademlia;

import peersim.core.CommonState;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * This class implements a kademlia k-bucket. Function for the management of the neighbours update are also implemented
 */
public class KBucket implements Cloneable {

    /**
     * The k-bucket array.
     */
    protected TreeMap<BigInteger, Long> neighbours = null;

    /**
     * Empty constructor.
     */
    public KBucket() {
        neighbours = new TreeMap<BigInteger, Long>();
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
            neighbours.put(node.getNodeId(), time);
        }
    }


    /**
     * Remove a neighbour from this k-bucket.
     *
     * @param node The to-be removed neighbour.
     */
    public void removeNeighbour(BigInteger node) {
        neighbours.remove(node);
    }


    /**
     * Cloning of the KBucket.
     *
     * @return
     */
    public Object clone() {
        KBucket dolly = new KBucket();
        for (BigInteger node : neighbours.keySet()) {
            dolly.neighbours.put(new BigInteger(node.toByteArray()), 0l);
        }
        return dolly;
    }

    /**
     * Get the KBucket.
     *
     * @return Current KBucket.
     */
    public TreeMap<BigInteger, Long> getKBucket() {
        return this.neighbours;
    }

    /**
     * Print the present KBucket.
     *
     * @return
     */
    public String toString() {
        String res = "{\n";

        for (BigInteger node : neighbours.keySet()) {
            res += node + "\n";
        }

        return res + "}";
    }
}
