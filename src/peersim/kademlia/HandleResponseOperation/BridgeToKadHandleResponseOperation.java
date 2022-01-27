package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

public class BridgeToKadHandleResponseOperation extends HandleResponseOperation2 {

    public BridgeToKadHandleResponseOperation(int kid, Message lookupMsg, int tid){
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
        lookupMessage.receiver.getSentMsgTracker().remove(lookupMessage.ackId);

        FindOperation fop = (FindOperation) lookupMessage.body;
        FindOperation fopOriginal = lookupMessage.receiver.getFindOperationsMap().get(lookupMessage.operationId);

        if(fop != null){

            //Step 1: update the closest set by saving the received neighbour
            try{
                fop.updateClosestSet((KadNode[]) lookupMessage.body);
            } catch (Exception e){
                fop.available_requests++;
            }

            this.receiver.getFindOperationsMap().remove(fop.operationId);

            Util.updateLookupStatistics((KadNode) lookupMessage.receiver, fop, kademliaid);
        }
    }
}
