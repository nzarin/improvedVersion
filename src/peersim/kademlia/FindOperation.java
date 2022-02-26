package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
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

    public KadNode sourceNode;

    private BridgeNode sourceBridgeNode;

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

    public Scope scope;

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
    public FindOperation(KadNode sourceNode, KadNode destNode, long timestamp) {
        this.sourceNode = sourceNode;
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

                        // replace the node with larger distance with n if n is closer (todo: check of dit wel klopt -> moet het niet maxdist zijn ipv nodemaxdist)
                        if (nodemaxdist.getNodeId().compareTo(n.getNodeId()) != 0) {
                            closestSet.remove(nodemaxdist);
                            closestSet.put(n, false);
                        }
                    }
                }
            }
        }
    }

    public void updateShortList(KadNode[] neighbours){
        // update response number because we can send another route message
        available_requests++;

        ArrayList<KadNode> previous_nodes_in_target_domain = new ArrayList<>();
        ArrayList<KadNode> previous_nodes_in_other_domains = new ArrayList<>();
        ArrayList<KadNode> new_nodes_in_target_domain = new ArrayList<>();
        ArrayList<KadNode> new_nodes_in_other_domain = new ArrayList<>();

        // segment current nodes
        for(KadNode i : closestSet.keySet()){
            if(i.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())){
//                System.err.println(i.toString3() + " is in the target domain");
                previous_nodes_in_target_domain.add(i);
            } else{
                previous_nodes_in_other_domains.add(i);
            }
        }

        //segment new nodes
        for(KadNode i : neighbours) {
            if (i.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())) {
                if (!previous_nodes_in_target_domain.contains(i)) {
                    new_nodes_in_target_domain.add(i);
                }
            } else {
                if (!previous_nodes_in_other_domains.contains(i)) {
                    new_nodes_in_other_domain.add(i);
                }
            }
        }

        // Scenario 1: there are older nodes in the target domain and there are new nodes in the target domain
        if(!previous_nodes_in_target_domain.isEmpty() && !new_nodes_in_target_domain.isEmpty()){
            // if there are older node from other domains -> replace them with nodes from target domain
            if(!previous_nodes_in_other_domains.isEmpty()){
                replaceNodesFromOtherDomainForTargetDomain(previous_nodes_in_other_domains, new_nodes_in_target_domain);
            } else{ //replace farther node for closer node to target node
                replaceNodesFromTargetDomainForTargetDomain(previous_nodes_in_target_domain, new_nodes_in_target_domain);
            }
        }

        //Scenario 2: there are older nodes in the target domain but there are no new nodes in the target domain
        if(!previous_nodes_in_target_domain.isEmpty() && new_nodes_in_target_domain.isEmpty()){
            // if there are older nodes from other domains -> replace them with new nodes from other domain that are closer to target domainId
            if(!previous_nodes_in_other_domains.isEmpty()){
                replaceNodesFromOtherDomainForOtherDomain(previous_nodes_in_other_domains, new_nodes_in_other_domain);
            }
        }
        //Scenario 3: there are no older nodes in the target domain and there are new nodes in the target domain
        if(previous_nodes_in_target_domain.isEmpty() && !new_nodes_in_target_domain.isEmpty()){
            // replace nodes from other domains for nodes from target domain
            replaceNodesFromOtherDomainForTargetDomain(previous_nodes_in_other_domains, new_nodes_in_target_domain);
        }
        //Scenario 4: there are no older nodes in the target domain and there are also no new nodes in the target domain
        if(previous_nodes_in_target_domain.isEmpty() && new_nodes_in_target_domain.isEmpty()){
            // update nodes such that they are closest to target domainID
            if(!new_nodes_in_other_domain.isEmpty()){
                replaceNodesFromOtherDomainForOtherDomain(previous_nodes_in_other_domains, new_nodes_in_other_domain);
            }
        }
    }

    private void replaceNodesFromOtherDomainForOtherDomain(ArrayList<KadNode> oldNodes, ArrayList<KadNode> newNodes) {

        for(KadNode n : newNodes){
            if(n != null){
                if(!closestSet.containsKey(n)){
                    if(closestSet.size() < KademliaCommonConfig.K){
                        closestSet.put(n, false);
                    } else {

                        //find the node with the farthest distance to the target domainId
                        BigInteger maxdist = Util.distance(n.getNodeId(), destNode.getDomain().getDomainId());
                        KadNode nodeMaxDist = n;
                        for (KadNode i : oldNodes) {
                            BigInteger dist = Util.distance(i.getNodeId(), destNode.getDomain().getDomainId());

                            if (dist.compareTo(maxdist) < 0) {
                                maxdist = dist;
                                nodeMaxDist = i;
                            }
                        }

                        //replace the node with larger distance with n if n is closer
                        if (maxdist.compareTo(Util.distance(n.getNodeId(), destNode.getDomain().getDomainId())) != 0) {
                            closestSet.remove(nodeMaxDist);
                            closestSet.put(n, false);
                        }
                    }
                }
            }
        }
    }

    private void replaceNodesFromTargetDomainForTargetDomain(ArrayList<KadNode> oldNodes, ArrayList<KadNode> newNodes) {

        for(KadNode n : newNodes){
            if(n != null){
                if(!closestSet.containsKey(n)){
                    if(closestSet.size() < KademliaCommonConfig.K){
                        closestSet.put(n, false);
                    } else {

                        //find the node with the farthest distance to the target nodeId
                        BigInteger maxdist = Util.distance(n.getNodeId(), destNode.getNodeId());
                        KadNode nodeMaxDist = n;
                        for(KadNode i : oldNodes){
                            BigInteger dist = Util.distance(i.getNodeId(), destNode.getNodeId());

                            if(dist.compareTo(maxdist) < 0){
                                maxdist = dist;
                                nodeMaxDist = i;
                            }
                        }

                        //replace the node with larger distance with n if n is closer
                        if(maxdist.compareTo(Util.distance(n.getNodeId(), destNode.getNodeId())) != 0){
                            closestSet.remove(nodeMaxDist);
                            closestSet.put(n, false);
                        }
                    }
                }
            }
        }
    }

    private void replaceNodesFromOtherDomainForTargetDomain(ArrayList<KadNode> oldNodes, ArrayList<KadNode> newNodes) {

        // replace pairwise
        for(int i =0; i < Math.min(oldNodes.size(), newNodes.size()); i++){
            if(newNodes.get(i) != null){
                if(!closestSet.containsKey(newNodes.get(i))){
                    if(closestSet.size() < KademliaCommonConfig.K){
                        closestSet.put(newNodes.get(i), false);
                    } else {
                        closestSet.remove(oldNodes.get(i));
                        closestSet.put(newNodes.get(i), false);
                    }
                }
            }
        }
    }


    /**
     * update closestSet with the new information received
     *
     * @param neighbours
     */
    public void updateClosestSet2(KadNode[] neighbours) {


        // add to closestSet
        for (KadNode n : neighbours) {

            if (n != null) {

                if (!closestSet.containsKey(n)) {

                    if (closestSet.size() < KademliaCommonConfig.K) {

                        // add directly if there is space left
                        closestSet.put(n, false);

                    } else {

                        boolean alreadyAddedNodeN = false;
                        ArrayList<KadNode> replacementCandidatesTargetDomain = new ArrayList<>();
                        ArrayList<KadNode> replacementCandidatesOtherDomain = new ArrayList<>();

                        // find candidates for replacement
                        for (KadNode i : closestSet.keySet()) {

                            boolean nIsInTargetDomain = n.getDomain().getDomainId().equals(destNode.getDomain().getDomainId());
                            boolean iIsInTargetDomain = i.getDomain().getDomainId().equals(destNode.getDomain().getDomainId());

                            //if n is in target domain and i is not (and if we have not already added node n) -> replace i for n
                            if(nIsInTargetDomain && !iIsInTargetDomain && !alreadyAddedNodeN ){
                                closestSet.remove(i);
                                closestSet.put(n, false);
                                alreadyAddedNodeN = true;

                                //if n is in target domain and i is too -> potentially replace
                            } else if(nIsInTargetDomain && iIsInTargetDomain){
                                replacementCandidatesTargetDomain.add(i);

                                //if n is not in target domain and i is also not in target domain -> potentially replace
                            } else if (!nIsInTargetDomain && !iIsInTargetDomain){
                                replacementCandidatesOtherDomain.add(i);

                                //if n is not in target domain and i is in target domain -> do nothing
                            } else if(!nIsInTargetDomain && iIsInTargetDomain){

                            }
                        }

                        //replace the correct candidates (todo: check of dit if else moet zijn)
                        if(!replacementCandidatesTargetDomain.isEmpty()){
                            replaceFarthestNode(n, replacementCandidatesTargetDomain);
                        } else {
                            replaceFarthestNode(n, replacementCandidatesOtherDomain);
                        }
                    }
                }
            }
        }
    }

    private void replaceFarthestNode(KadNode n, ArrayList<KadNode> candidates){

        // find the node with max distance
        BigInteger maxdist = Util.distance(n.getNodeId(), destNode.getNodeId());
        KadNode nodemaxdist = n;

        // if there are some candidates from other domain
        for(KadNode i : candidates){
            BigInteger dist = Util.distance(i.getNodeId(), destNode.getNodeId());

            if (dist.compareTo(maxdist) > 0) {
                maxdist = dist;
                nodemaxdist = i;
            }
        }

        // replace the node with larger distance with n if n is closer
        if (nodemaxdist.getNodeId().compareTo(n.getNodeId()) != 0) {
            closestSet.remove(nodemaxdist);
            closestSet.put(n, false);
        }
    }

    public KadNode getNextHop(KadNode me){

        KadNode nextHop = null;
        // if it is an inter-domain lookup
        if(scope.equals(Scope.INTERDOMAIN)){
            //if I am in the target domain -> select closest unvisited node to target nodeId
            if(me.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())){
                getNextHopTargetDomain();
            } else {
                // if I know someone from targetDomain (I am octopus) -> select this node from target domain
                if(me.getRoutingTable().containsNodeFromTargetDomain(destNode.getDomain().getDomainId())){
                    nextHop = getNextHopTargetDomain();
                } else {
                    //select closest unvisited node to target domainId
                    nextHop =  getNextHopCloseToTargetDomain();
                }
            }
        } else {

            //if it is an intra-domain lookup -> we are already in target domain and select the closest unvisited node to target nodeId
            nextHop = getNextHopTargetDomain();
        }

        return nextHop;

    }

    private KadNode getNextHopCloseToTargetDomain() {

        KadNode res = null;
        for (KadNode n : closestSet.keySet()) {
            if (n != null && !closestSet.get(n)) {
                if (res == null) {
                    res = n;
                    // if this node is in the target domain, and it's the closest we have
                } else if ((Util.distance(n.getNodeId(), destNode.getDomain().getDomainId()).compareTo(Util.distance(res.getNodeId(), destNode.getDomain().getDomainId())) < 0)) {
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

    private KadNode getNextHopTargetDomain(){

        // find closest neighbour that is not already queried from the target domain
        ArrayList<KadNode> unvisited_nodes_from_target_domain = new ArrayList<>();
        for(KadNode i : closestSet.keySet()){
            if(i.getDomain().getDomainId().equals(destNode.getDomain().getDomainId()) && !closestSet.get(i)){
                unvisited_nodes_from_target_domain.add(i);
            }
        }

        KadNode res = null;
        for (KadNode n : unvisited_nodes_from_target_domain) {
            if (res == null) {
                res = n;
                // if this node is in the target domain and its the closest we have
            } else if ((Util.distance(n.getNodeId(), destNode.getNodeId()).compareTo(Util.distance(res.getNodeId(), destNode.getNodeId())) < 0)) {
                res = n;
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
     * Get the first neighbour in closest set which has not been already queried.
     *
     * @return the Id of the node or null if there aren't available node.
     */
    public KadNode getNeighbourThisDomain() {

        // find closest neighbour (the first not already queried) from this domain
        KadNode res = null;
        for (KadNode n : closestSet.keySet()) {
            if (n != null && !closestSet.get(n)) {
                if (res == null) {
                    res = n;
                    // if this node is in the target domain and its the closest we have
                } else if ((Util.distance(n.getNodeId(), destNode.getNodeId()).compareTo(Util.distance(res.getNodeId(), destNode.getNodeId())) < 0) && n.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())) {
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


    public KadNode getNeighbourOtherDomain(){
        // find closest neighbour (the first not already queried) to the ID of the target domain
        KadNode res = null;
        for (KadNode n : closestSet.keySet()) {
            if (n != null && !closestSet.get(n)) {
                if (res == null) {
                    res = n;
                    // if this node is in the target domain and its the closest we have
                } else if ((Util.distance(n.getNodeId(), destNode.getNodeId()).compareTo(Util.distance(res.getNodeId(), destNode.getNodeId())) < 0) && n.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())) {
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


    public KadNode getNeighbourClosestToOtherDomain(){
        // find closest neighbour (the first not already queried) FROM TARGET DOMAIN
        KadNode res = null;
        for (KadNode n : closestSet.keySet()) {
            if (n != null && !closestSet.get(n)) {
                if (res == null) {
                    res = n;
                    // if this node is in the target domain and its the closest we have
                } else if ((Util.distance(n.getNodeId(), destNode.getNodeId()).compareTo(Util.distance(res.getNodeId(), destNode.getNodeId())) < 0) && n.getDomain().getDomainId().equals(destNode.getDomain().getDomainId())) {
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

    /**
     * Get the bridge node of the source domain
     * @return
     */
    public BridgeNode getSourceBridgeNode() {return sourceBridgeNode;}

    /**
     * Set the bridge node of the source for the findOp object
     * @param sourceBridgeNode
     */
    public void setSourceBridgeNode(BridgeNode sourceBridgeNode) {this.sourceBridgeNode = sourceBridgeNode;}

    /**
     * Set the operation id of this find operation
     * @param id
     */
    public void setOperationId(long id){
        this.operationId = id;
    }


    public String beautifyClosestSet(){
        StringBuilder str = new StringBuilder();
        str.append("[ \n");
        for(KadNode node : closestSet.keySet()){
            str.append(node.toString3() + " \n");
        }
        str.append(" ]");
        return str.toString();
    }
}
