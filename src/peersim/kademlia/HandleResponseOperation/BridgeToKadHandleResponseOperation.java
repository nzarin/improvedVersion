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

        //update statistics (also the other direction)
        FindOperation fop = (FindOperation) lookupMessage.body;
        fop.nrMessages = fop.nrMessages + 2;
        fop.shortestNrHops++;

        if(fop != null){

            this.receiver.getFindOperationsMap().remove(fop.operationId);
//            System.err.println("I have received the result of the lookup and I am going to update the statistics!");

            Util.updateLookupStatistics((KadNode) lookupMessage.receiver, fop, kademliaid);
        }
    }
}
