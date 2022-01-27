package peersim.kademlia.FindOperations;

import peersim.core.CommonState;
import peersim.core.Node;
import peersim.kademlia.*;

public class BridgeToKadFindOperation extends FindOperation2 {

    public BridgeToKadFindOperation(int kid, Message lookupMsg, int tid){
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

        //select a random kadnode in this domain that can initiate the findoperation
        KadNode randomKadNodeThisDomain = null;
        do{
            randomKadNodeThisDomain = lookupMessage.receiver.getKadNodes().get(CommonState.r.nextInt(lookupMessage.receiver.getKadNodes().size()));
        } while (randomKadNodeThisDomain == null);

        // update statistics of the findOp object
        FindOperation findOp = lookupMessage.src.getFindOperationsMap().get(lookupMessage.operationId);
        findOp.nrHops++;

        // create FINDNODE message to send it to this bridge node
        System.err.println(" I am forwarding the FIND message to " + randomKadNodeThisDomain.getNodeId());
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomKadNodeThisDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
        messageSender.sendMessage(forward);

    }
}
