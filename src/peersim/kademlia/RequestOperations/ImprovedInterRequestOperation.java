package peersim.kademlia.RequestOperations;

import peersim.kademlia.*;

public class ImprovedInterRequestOperation extends RequestOperation {

    public ImprovedInterRequestOperation(int kid, Message lookupMsg, int tid) {
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void find() {

        //check whether the findOp object already exists (inter-domain) or whether we have to create one (intra-domain)
        FindOperation findOp;
        if(lookupMessage.src.getDomain() == lookupMessage.target.getDomain()){
            findOp = new FindOperation((KadNode) lookupMessage.src, (KadNode) lookupMessage.target, lookupMessage.timestamp);
            findOp.scope = Scope.INTRADOMAIN;
            findOp.body = lookupMessage.body;
        } else{
            findOp = (FindOperation) lookupMessage.body;
        }

        //add the findOp to my map of find operations anyway
        lookupMessage.receiver.getFindOperationsMap().put(findOp.operationId, findOp);
        KadNode[] neighbours =  lookupMessage.receiver.getRoutingTable().getKNeighbours2((KadNode) lookupMessage.target, (KadNode) lookupMessage.receiver, (KadNode) lookupMessage.src);

        KadNode[] result = new KadNode[KademliaCommonConfig.K];
        int count = 0;

        // select nodes in this neighbours list that are truly from the target domain
        for(KadNode n : neighbours){
            if(n.getDomain() == lookupMessage.target.getDomain()){
                result[count] = n;
                count++;
            }
        }

        //if no nodes have been found that are from the target domain ->
        if(count == 0){

            result = neighbours;
        }

        // update the list of closest nodes and re-initialize available requests
        findOp.updateClosestSet(result);
        findOp.available_requests = KademliaCommonConfig.ALPHA;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = findOp.getNeighbour();
            if (nextNode != null) {

                //update statistics of the find operation
                findOp.nrMessages++;

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
//                System.err.println("I am sending a REQUEST message to (" + request.receiver.getNodeId() + "," + request.receiver.getDomain() + ") of type " + request.receiver.getType() + "with msgId is " + request.msgId);
                messageSender.sendMessage(request);
            }
        }
    }
}
