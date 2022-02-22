package peersim.kademlia.RequestOperations;

import peersim.core.CommonState;
import peersim.kademlia.*;

public class KadToBridgeRequestOperation extends RequestOperation {

    public KadToBridgeRequestOperation(int kid, Message lookupMsg, int tid){
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void find() {

        //create a findOp object
        FindOperation findOp = new FindOperation((KadNode) lookupMessage.target, lookupMessage.timestamp);
        findOp.body = lookupMessage.body;
        lookupMessage.receiver.getFindOperationsMap().put(findOp.operationId, findOp);

        // find a random bridge of our domain and send it the route message
        BridgeNode randomBridgeNodeThisDomain;
        do{
            randomBridgeNodeThisDomain = lookupMessage.receiver.getBridgeNodes().get(CommonState.r.nextInt(lookupMessage.receiver.getBridgeNodes().size()));
        } while (randomBridgeNodeThisDomain == null);
        findOp.setSourceBridgeNode(randomBridgeNodeThisDomain);

        //update statistics of the find operation
        findOp.nrMessages++;
        findOp.shortestNrHops++;

        // create FINDNODE message to send it to this bridge node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.body = findOp;
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomBridgeNodeThisDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
//        System.err.println("I am forwarding the FIND message to (" + randomBridgeNodeThisDomain.getNodeId() + "," + randomBridgeNodeThisDomain.getDomain() + ") of type " + randomBridgeNodeThisDomain.getType());
        messageSender.sendMessage(forward);
    }
}
