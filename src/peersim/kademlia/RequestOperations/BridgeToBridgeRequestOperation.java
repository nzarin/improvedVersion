package peersim.kademlia.RequestOperations;

import peersim.kademlia.*;

public class BridgeToBridgeRequestOperation extends RequestOperation {

    public BridgeToBridgeRequestOperation(int kid, Message lookupMsg, int tid){
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }
    @Override
    public void find() {

        //add this node my list of kad nodes
        if(lookupMessage.sender instanceof KadNode && !lookupMessage.receiver.getKadNodes().contains(lookupMessage.sender)){
            lookupMessage.receiver.getKadNodes().add((KadNode) lookupMessage.sender);
        }

        // find the correct bridge node of the domain of the target node
        BridgeNode randomBridgeNodeTargetDomain = null;
        for(BridgeNode b : lookupMessage.receiver.getBridgeNodes()){
            if(lookupMessage.target.getDomain() == b.getDomain() && b != lookupMessage.target){
                randomBridgeNodeTargetDomain = b;
            }
        }

        //update statistics of the find operation
        FindOperation findOp = (FindOperation) lookupMessage.body;
        findOp.nrMessages++;
        findOp.shortestNrHops++;

        //create FINDNODE message to send it to kad node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.body = findOp;
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomBridgeNodeTargetDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
//        System.err.println(" I am forwarding the FIND message to (" + randomBridgeNodeOtherDomain.getNodeId() + "," + randomBridgeNodeOtherDomain.getDomain() + ") of type " + randomBridgeNodeOtherDomain.getType());
        messageSender.sendMessage(forward);

    }
}
