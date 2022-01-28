package peersim.kademlia.HandleResponseOperation;

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

        //find the correct kad node and forward it the results;
        Message response = new Message(Message.MSG_RESPONSE);
        response.body = lookupMessage.body;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.sender = lookupMessage.receiver;
        response.receiver = lookupMessage.src;
        response.ackId = lookupMessage.msgId;       //todo check of dit nog klopt
        System.err.println("I am sending a RESPONSE message to " + response.receiver.getNodeId() + " of type " + response.receiver.getType() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}
