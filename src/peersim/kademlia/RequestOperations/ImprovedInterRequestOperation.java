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
        FindOperation findOp = new FindOperation((KadNode) lookupMessage.src, (KadNode) lookupMessage.target, lookupMessage.timestamp);
        findOp.body = lookupMessage.body;
        if(lookupMessage.src.getDomain().getDomainId().equals(lookupMessage.target.getDomain().getDomainId())){
            findOp.scope = Scope.INTRADOMAIN;
        } else {
            findOp.scope = Scope.INTERDOMAIN;
        }

        lookupMessage.receiver.getFindOperationsMap().put(findOp.operationId, findOp);

        KadNode[] neighbours =  lookupMessage.receiver.getRoutingTable().getNextHopCandidates((KadNode) lookupMessage.target, (KadNode) lookupMessage.receiver, (KadNode) lookupMessage.src);
        System.err.println("next hop candidates are : " + neighbours.length);
        for(int i = 0; i < neighbours.length; i++){
            System.err.println(neighbours[i].toString3());
        }
        // update the list of closest nodes and re-initialize available requests
        System.err.println("my old short list was: ");
        System.err.println(findOp.beautifyClosestSet());
        findOp.updateShortList(neighbours);
        System.err.println("my new short list is: ");
        System.err.println(findOp.beautifyClosestSet());

        findOp.available_requests = KademliaCommonConfig.ALPHA;
        System.err.println("Do I have node from the target domain in my routing table? " + lookupMessage.receiver.getRoutingTable().containsNodeFromTargetDomain(lookupMessage.target.getDomain().getDomainId()));
        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = findOp.getNextHop((KadNode) lookupMessage.receiver);
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
                System.err.println("I am sending a REQUEST message to (" + request.receiver.getNodeId() + "," + request.receiver.getDomain().getDomainId() + ") of role " + request.receiver.getRole() + " with msgId is " + request.msgId);
                messageSender.sendMessage(request);
            }
        }
    }
}
