package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

public class BridgeToKadHandleResponseOperation extends HandleResponseOperation2 {

    public BridgeToKadHandleResponseOperation(int kid, Message lookupMsg, int tid){
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

            lookupMessage.receiver.getFindOperationsMap().remove(fop.operationId);
//            System.err.println("I have received the result of the lookup and I am going to update the statistics!");

            Statistician.updateLookupStatistics((KadNode) lookupMessage.receiver, fop, kademliaid);
        }
    }
}
