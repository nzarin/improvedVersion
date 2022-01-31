package peersim.kademlia.HandleResponseOperation;

import peersim.core.CommonState;
import peersim.kademlia.*;

/**
 * Kad to Bridge indicates that the intra domain lookup has finished, and we have to forward this result back to the source
 */
public class KadToBridgeHandleRespondOperation extends HandleResponseOperation2 {

    public KadToBridgeHandleRespondOperation(int kid, Message lookupMsg, int tid){
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void handleResponse() {

        // find a random bridge of other domain and send it the route message
        BridgeNode randomBridgeNodeOtherDomain = null;
        for(BridgeNode b : lookupMessage.receiver.getBridgeNodes()){
            if(lookupMessage.src.getDomain() == b.getDomain()){
                randomBridgeNodeOtherDomain = b;
                break;
            }
        }

        //update statistics (keep in mind also from the other direction)
        FindOperation fop = (FindOperation) lookupMessage.body;
        fop.nrMessages= fop.nrMessages+2;
        fop.shortestNrHops++;

        // create RESPONSE message to send it to this bridge node
        Message response = new Message(Message.MSG_RESPONSE);
        response.body = fop;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.sender = lookupMessage.receiver;
        response.receiver = randomBridgeNodeOtherDomain;
        response.operationId = lookupMessage.operationId;
        response.newLookup = lookupMessage.newLookup;
//        System.err.println("I am sending a RESPONSE message to (" + response.receiver.getNodeId() + "," + response.receiver.getDomain() + ") of type " + response.receiver.getType() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}