package peersim.kademlia.FindOperations;

import peersim.core.Node;
import peersim.kademlia.*;

/**
 * This class represents the find operation when the source and target are both KadNodes
 */
public class KadToKadFindOperation extends FindOperation2 {


    /**
     * Constructs the FindOperation
     * @param kid
     * @param lookupMsg
     * @param tid
     */
    public KadToKadFindOperation(int kid, Message lookupMsg, int tid) {
        this.source = (KadNode) lookupMsg.src;
        this.target = (KadNode) lookupMsg.target;
        this.sender = lookupMsg.sender;
        this.receiver = lookupMsg.receiver;
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }



    @Override
    public void find() {

        //If I am the searcher and searched node or the searched node is down, do thing
        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if ((lookupMessage.target.getNodeId() == this.source.getNodeId()) || (!target.isUp()))
            return;

        KademliaObserver.find_op.add(1);

        // create find operation and add to operations array for bookkeeping
        FindOperation findOp = new FindOperation(this.target, lookupMessage.timestamp);
        findOp.body = lookupMessage.body;
        this.source.getFindOperationsMap().put(findOp.operationId, findOp);

        // get the K closest node to search key
        KadNode[] neighbours = this.source.getRoutingTable().getKClosestNeighbours((KadNode) lookupMessage.target, source);

        // update the list of closest nodes and re-initialize available requests
        findOp.updateClosestSet(neighbours);
        findOp.available_requests = KademliaCommonConfig.ALPHA;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = findOp.getNeighbour();
            if (nextNode != null) {
                findOp.nrHops++;
                //create a request message
                Message request = new Message(Message.MSG_ROUTE);
                request.src = lookupMessage.src;
                request.target = lookupMessage.target;
                request.sender = lookupMessage.src;
                request.operationId = findOp.operationId;
                request.newLookup = lookupMessage.newLookup;
                request.receiver = nextNode;
                System.err.println("I am sending a ROUTE message to " + request.receiver.getNodeId() + " with msgId is " + request.msgId);
                messageSender.sendMessage(request);
            }
        }
        System.err.println(" after sending these route messages, findOp.nrHops: " + findOp.nrHops);

    }
}
