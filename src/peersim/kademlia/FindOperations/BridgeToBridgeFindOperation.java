package peersim.kademlia.FindOperations;

import peersim.core.Node;
import peersim.kademlia.*;

public class BridgeToBridgeFindOperation extends FindOperation2 {

    public BridgeToBridgeFindOperation(int kid, Message lookupMsg, int tid){
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

        // find the correct bridge node of the domain of the target node
        BridgeNode randomBridgeNodeOtherDomain = null;
        for(BridgeNode b : lookupMessage.receiver.getBridgeNodes()){
            if(lookupMessage.target.getDomain() == b.getDomain() && b != lookupMessage.target){
                randomBridgeNodeOtherDomain = b;
            }
        }


        //create FINDNODE message to send it to kad node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.body = lookupMessage.body;
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomBridgeNodeOtherDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
//        System.err.println(" I am forwarding the FIND message to (" + randomBridgeNodeOtherDomain.getNodeId() + "," + randomBridgeNodeOtherDomain.getDomain() + ") of type " + randomBridgeNodeOtherDomain.getType());
        messageSender.sendMessage(forward);

    }
}
