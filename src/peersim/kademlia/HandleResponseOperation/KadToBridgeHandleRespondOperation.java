package peersim.kademlia.HandleResponseOperation;

import peersim.core.CommonState;
import peersim.kademlia.BridgeNode;
import peersim.kademlia.KadNode;
import peersim.kademlia.Message;
import peersim.kademlia.MessageSender;

/**
 * Kad to Bridge indicates that the intra domain lookup has finished, and we have to forward this result back to the source
 */
public class KadToBridgeHandleRespondOperation extends HandleResponseOperation2 {

    public KadToBridgeHandleRespondOperation(int kid, Message lookupMsg, int tid){
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
        // forward this message to the right domain bridge node

        // find a random bridge of our domain and other domain and send it the route message
        BridgeNode randomBridgeNodeOtherDomain = null;
        for(BridgeNode b : lookupMessage.receiver.getBridgeNodes()){
            if(lookupMessage.src.getDomain() == b.getDomain()){
                randomBridgeNodeOtherDomain = b;
            }
        }

        // create FINDNODE message to send it to this bridge node
        Message forward = new Message(Message.MSG_RESPONSE);
        forward.body = lookupMessage.body;
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomBridgeNodeOtherDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
        messageSender.sendMessage(forward);

    }
}
