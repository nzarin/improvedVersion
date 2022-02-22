package peersim.kademlia;

import peersim.core.CommonState;

import java.math.BigInteger;
import java.util.LinkedHashMap;

/**
 * This class implements a kademlia k-bucket. Function for the management of the neighbours update are also implemented
 */
public class KBucket implements Cloneable {

    /**
     * The k-bucket array.
     */
    protected LinkedHashMap<KadNode, Long> neighbours;

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
    public void fillKBucket(KadNode node) {
        long time = CommonState.getTime();

        // if the k-bucket isn't full, add neighbour to tail of the list.
        if (neighbours.size() < KademliaCommonConfig.K) { // k-bucket isn't full
            neighbours.put(node, time);
        }

    }


    public void addKadNode(KadNode node){
        fillKBucket(node);

//        long time = CommonState.getTime();
//
//        if(neighbours.size() < KademliaCommonConfig.K){
//            neighbours.put(node, time);
//        } else {
//            //determine whether there is a node that is offline
//            for(KadNode neighbour : neighbours.keySet()){
//                if(!neighbour.isAlive()){
//                    neighbours.remove(neighbour);
//                    neighbours.put(node,time);
//                }
//            }
//        }
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
        String res = "{\n";

        for (KadNode node : neighbours.keySet()) {
            res += node.toString3() + "\n";
        }

        return res + "}";
    }


    public KadNode getKadNode(KadNode kadNode){
        for(KadNode neighbour : neighbours.keySet()){
            if (kadNode.getNodeId() ==  neighbour.getNodeId()){
                return neighbour;
            }
        }
        return null;
    }
}
