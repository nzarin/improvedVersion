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
        //If I searched node is down, do nothing
        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if (!target.isUp())
            return;

        // create a new find operation and add to operations array for bookkeeping
        FindOperation findOp = new FindOperation((KadNode) lookupMessage.target, lookupMessage.timestamp);
        KademliaObserver.find_op.add(1);
        findOp.body = lookupMessage.body;
        lookupMessage.receiver.getFindOperationsMap().put(findOp.operationId, findOp);


        // get the K closest node to search key
        KadNode[] neighbours = lookupMessage.receiver.getRoutingTable().getKClosestNeighbours((KadNode) lookupMessage.target, (KadNode) lookupMessage.receiver);
        // update the list of closest nodes and re-initialize available requests
        findOp.updateClosestSet(neighbours);
        findOp.available_requests = KademliaCommonConfig.ALPHA;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = findOp.getNeighbour();
            if (nextNode != null) {
                findOp.nrHops++;
                //create a request message
                Message request = new Message(Message.MSG_REQUEST);
                request.body = lookupMessage.body;
                request.src = lookupMessage.src;
                request.target = lookupMessage.target;
                request.sender = lookupMessage.receiver;
                request.operationId = findOp.operationId;
                request.newLookup = lookupMessage.newLookup;
                request.receiver = nextNode;
                request.sender.getRoutingTable().addNeighbour(nextNode);
                System.err.println("I am sending a REQUEST message to (" + request.receiver.getNodeId() + "," + request.receiver.getDomain() + ") with msgId is " + request.msgId);
                messageSender.sendMessage(request);
            }
        }
    }
}
