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
	public BigInteger destNode;

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
	public long timestamp = 0;

	/**
	 * Number of hops the message did
	 */
	public int nrHops = 0;


	/**
	 * This map contains the K closest nodes and corresponding boolean value that indicates if the nodes has been already queried
	 * or not
	 */
	public HashMap<BigInteger, Boolean> closestSet;

	/**
	 * default constructor
	 * 
	 * @param destNode
	 *            Id of the node to find
	 */
	public FindOperation(BigInteger destNode, long timestamp) {
		this.destNode = destNode;
		this.timestamp = timestamp;

		// set a new find id
		operationId = OPERATION_ID_GENERATOR++;

		// set available request to ALPHA
		available_requests = KademliaCommonConfig.ALPHA;

		// initialize closestSet
		closestSet = new HashMap<BigInteger, Boolean>();
	}

	/**
	 * update closestSet with the new information received
	 * 
	 * @param neighbours
	 */
	public void updateClosestSet(BigInteger[] neighbours) {

//		System.err.println("Destination Node: " + this.destNode);
//		System.err.println("Current available_requests (elaborateRes.) : " + available_requests);
//		System.err.println("Operation id (univocally) of find operation:  " + this.operationId );
//		System.err.println("Unique sequence number generator of the operation :  " + this.OPERATION_ID_GENERATOR );

		// update response number because we can send another route message
		available_requests++;

		// add to closestSet
		for (BigInteger n : neighbours) {

			if (n != null) {

				if (!closestSet.containsKey(n)) {

					if (closestSet.size() < KademliaCommonConfig.K) {

						// add directly if there is space left
						closestSet.put(n, false);

					} else {

						// find in the closest set if there are nodes with less distance
						BigInteger newdist = Util.distance(n, destNode);

						// find the node with max distance
						BigInteger maxdist = newdist;
						BigInteger nodemaxdist = n;
						for (BigInteger i : closestSet.keySet()) {
							BigInteger dist = Util.distance(i, destNode);

							if (dist.compareTo(maxdist) > 0) {
								maxdist = dist;
								nodemaxdist = i;
							}
						}

						// replace the node with larger distance with n
						if (nodemaxdist.compareTo(n) != 0) {
							closestSet.remove(nodemaxdist);
							closestSet.put(n, false);
						}
					}
				}
			}
		}


		/*String s = "closestSet to " + destNode + "\n";
		for (BigInteger clos : closestSet.keySet()) {
			 s+= clos + "-";
		}
		System.out.println(s);*/

	}

	/**
	 * Get the first neighbour in closest set which has not been already queried.
	 * 
	 * @return the Id of the node or null if there aren't available node.
	 */
	public BigInteger getNeighbour() {

		// find closest neighbour (the first not already queried)
		BigInteger res = null;
		for (BigInteger n : closestSet.keySet()) {
			if (n != null && closestSet.get(n) == false) {
				if (res == null) {
					res = n;
				} else if (Util.distance(n, destNode).compareTo(Util.distance(res, destNode)) < 0) {
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
	 * @return
	 */
	public HashMap<BigInteger, Boolean> getClosestSet() {
		return closestSet;
	}




}
