package peersim.kademlia.FindOperations;

import peersim.core.CommonState;
import peersim.core.Node;
import peersim.kademlia.*;

public class BridgeToKadFindOperation extends FindOperation2 {

    public BridgeToKadFindOperation(int kid, Message lookupMsg, int tid){
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void find() {
        //If the searched node is down, do nothing
        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if (!target.isUp())
            return;

        //select a random kadnode in this domain that can initiate the findoperation
        KadNode randomKadNodeThisDomain;
        do{
            randomKadNodeThisDomain = lookupMessage.receiver.getKadNodes().get(CommonState.r.nextInt(lookupMessage.receiver.getKadNodes().size()));
        } while (randomKadNodeThisDomain == null || randomKadNodeThisDomain == lookupMessage.target);


        // create FINDNODE message to send it to this bridge node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.body = lookupMessage.body;
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomKadNodeThisDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
//        System.err.println(" I am forwarding the FIND message to (" + randomKadNodeThisDomain.getNodeId() + "," + randomKadNodeThisDomain.getDomain() + ") of type " + randomKadNodeThisDomain.getType());
        messageSender.sendMessage(forward);

    }
}
