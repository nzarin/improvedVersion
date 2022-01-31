package peersim.kademlia.FindOperations;

import peersim.core.CommonState;
import peersim.core.Node;
import peersim.kademlia.*;

public class KadToBridgeFindOperation extends FindOperation2 {

    public KadToBridgeFindOperation(int kid, Message lookupMsg, int tid){
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
        //If searched node is down, do nothing
        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if (!target.isUp())
            return;

        // find a random bridge of our domain and other domain and send it the route message
        BridgeNode randomBridgeNodeThisDomain;
        do{
            randomBridgeNodeThisDomain = lookupMessage.receiver.getBridgeNodes().get(CommonState.r.nextInt(lookupMessage.receiver.getBridgeNodes().size()));
        } while (randomBridgeNodeThisDomain == null);


        // create FINDNODE message to send it to this bridge node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.body = lookupMessage.body;
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
