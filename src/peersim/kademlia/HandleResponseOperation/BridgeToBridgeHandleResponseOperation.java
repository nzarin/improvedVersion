package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KadNode;
import peersim.kademlia.Message;
import peersim.kademlia.MessageSender;

public class BridgeToBridgeHandleResponseOperation extends HandleResponseOperation2 {

    public BridgeToBridgeHandleResponseOperation(int kid, Message lookupMsg, int tid){
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
    public void handleResponse() {

        //update statistics also from the other direction
        FindOperation fop = (FindOperation) lookupMessage.body;
        fop.nrMessages = fop.nrMessages + 2;
        fop.shortestNrHops++;

        //find the correct kad node and forward it the results;
        Message response = new Message(Message.MSG_RESPONSE);
        response.body = fop;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.sender = lookupMessage.receiver;
        response.receiver = lookupMessage.src;
        response.ackId = lookupMessage.msgId;
//        System.err.println("I am sending a RESPONSE message to (" + response.receiver.getNodeId() + "," + response.receiver.getDomain() + ") of type " + response.receiver.getType() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}
