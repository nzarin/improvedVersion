package peersim.kademlia;

import java.math.BigInteger;
import java.util.HashMap;

/**
 * This class represents a find operation and offers the methods needed to maintain and update the closest set.
 * It also maintains the number of parallel requests that can be maximum ALPHA
 */
public class FindOperation {

    /**
     * Unique sequence number generator of the operation.
     */
    private static long OPERATION_ID_GENERATOR = 0;

    /**
     * Represent univocally the find operation.
     */
    public long operationId;

    /**
     * Id of the node to find.
     */
    public KadNode destNode;

    /**
     * Body of the original find message.
     */
    public Object body;

    /**
     * Number of available find request message to send (it must be always less than ALPHA).
     */
    public int available_requests;

    /**
     * Start timestamp of the search operation
     */
    public long timestamp;

    /**
     * Number of hops the message did
     */
    public int nrHops = 0;

    public int shortestNrHops = 1;

    public int nrMessages = 0;

    public int nrResponse = 0;

    public boolean alreadyFoundTarget;

    /**
     * This map contains the K closest nodes and corresponding boolean value that indicates if the nodes has been already queried
     * or not
     */
    public HashMap<KadNode, Boolean> closestSet;

    /**
     * default constructor
     *
     * @param destNode Id of the node to find
     */
    public FindOperation(KadNode destNode, long timestamp) {
        this.destNode = destNode;

        this.timestamp = timestamp;

        // set a new find id
        operationId = OPERATION_ID_GENERATOR++;

        // set available request to ALPHA
        available_requests = KademliaCommonConfig.ALPHA;

        // initialize closestSet
        closestSet = new HashMap<>();
    }

    /**
     * update closestSet with the new information received
     *
     * @param neighbours
     */
    public void updateClosestSet(KadNode[] neighbours) {
        // update response number because we can send another route message
        available_requests++;

        // add to closestSet
        for (KadNode n : neighbours) {

            if (n != null) {

                if (!closestSet.containsKey(n)) {

                    if (closestSet.size() < KademliaCommonConfig.K) {

                        // add directly if there is space left
                        closestSet.put(n, false);

                    } else {

                        // find the node with max distance
                        BigInteger maxdist = Util.distance(n.getNodeId(), destNode.getNodeId());
                        KadNode nodemaxdist = n;
                        for (KadNode i : closestSet.keySet()) {
                            BigInteger dist = Util.distance(i.getNodeId(), destNode.getNodeId());

                            if (dist.compareTo(maxdist) > 0) {
                                maxdist = dist;
                                nodemaxdist = i;
                            }
                        }

                        // replace the node with larger distance with n
                        if (nodemaxdist.getNodeId().compareTo(n.getNodeId()) != 0) {
                            closestSet.remove(nodemaxdist);
                            closestSet.put(n, false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Get the first neighbour in closest set which has not been already queried.
     *
     * @return the Id of the node or null if there aren't available node.
     */
    public KadNode getNeighbour() {

        // find closest neighbour (the first not already queried)
        KadNode res = null;
        for (KadNode n : closestSet.keySet()) {
            if (n != null && !closestSet.get(n)) {
                if (res == null) {
                    res = n;
                } else if (Util.distance(n.getNodeId(), destNode.getNodeId()).compareTo(Util.distance(res.getNodeId(), destNode.getNodeId())) < 0) {
                    res = n;
                }
            }
        }

        // found a valid neighbour
        if (res != null) {
            closestSet.remove(res);
            closestSet.put(res, true);
            available_requests--;
        }

        return res;
    }

    /**
     * Get closest set.
     *
     * @return
     */
    public HashMap<KadNode, Boolean> getClosestSet() {
        return closestSet;
    }


    public void setOperationId(long id){
        this.operationId = id;
    }
}
