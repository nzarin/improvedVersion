package peersim.kademlia.HandleResponseOperations;

import peersim.kademlia.*;

/**
 * Kad to Bridge indicates that the intra domain lookup has finished, and we have to forward this result back to the source
 */
public class KadToBridgeHandleRespondOperation extends HandleResponseOperation {

    public KadToBridgeHandleRespondOperation(int kid, Message lookupMsg, int tid){
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void handleResponse() {

        //update statistics (keep in mind also from the other direction)
        FindOperation fop = (FindOperation) lookupMessage.body;
        fop.nrMessages++;

        // create RESPONSE message to send it to this bridge node
        Message response = new Message(Message.MSG_RESPONSE);
        response.body = fop;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.sender = lookupMessage.receiver;
        response.receiver = fop.getSourceBridgeNode();
        response.operationId = lookupMessage.operationId;
        response.newLookup = lookupMessage.newLookup;
//        System.err.println("I am sending a RESPONSE message to (" + response.receiver.getNodeId() + "," + response.receiver.getDomain() + ") of type " + response.receiver.getType() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}