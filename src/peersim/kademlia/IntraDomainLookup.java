package peersim.kademlia;

import peersim.core.Node;

import java.util.LinkedHashMap;

public class IntraDomainLookup implements Lookup{

    private int kademliaid;
    private KadNode currentNode;
    private LinkedHashMap<Long, FindOperation> findOpMap;
    private Message m;

    public IntraDomainLookup(int kademlia_id, KadNode current, LinkedHashMap<Long, FindOperation> findOps, Message lookupMessage ) {
        this.kademliaid = kademlia_id;
        this.currentNode =  current;
        this.findOpMap = findOps;
        this.m = lookupMessage;
    }

    @Override
    public void lookup() {
        find();
    }


    /**
     * Start a find node operation.
     * Find the ALPHA closest node and send find request to them.
     *
     */
    public FindOperation find() {

        // if I am the searched node or searched node is down -> skip (should not happen in kademlia)
        Node dst = Util.nodeIdtoNode(m.dest.getNodeId(), kademliaid);
        if((m.dest.getNodeId() == this.currentNode.getNodeId()) || (!dst.isUp()))
            return null;

        // increase the number of find operations
        KademliaObserver.find_op.add(1);

        // create find operation and add to operations array for bookkeeping
        FindOperation fop = new FindOperation(m.dest, m.timestamp);
        fop.body = m.body;
        findOpMap.put(fop.operationId, fop);

//		System.err.println("The operationID is : " + fop.operationId);
//		System.err.println("Size of the findOP of node : " + this.nodeId +" is " + findOp.size());

        // get the K closest node to search key
        KadNode[] neighbours = this.currentNode.getRoutingTable().getKClosestNeighbours(m.dest, this.currentNode);

        // update the list of closest nodes and re-initialize available requests
        fop.updateClosestSet(neighbours);
        fop.available_requests = KademliaCommonConfig.ALPHA;

        // set message operation id
        m.operationId = fop.operationId;
        m.type = Message.MSG_ROUTE;
        m.src = this.currentNode;

        if(fop == null){
            System.err.println("fop in IntraDomainClass is null " + fop);
        }

        //return the find operaration
        return fop;

    }


}
