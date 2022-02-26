package peersim.kademlia.HandleResponseOperations;

import peersim.kademlia.*;

public class BridgeToKadHandleResponseOperation extends HandleResponseOperation {

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
        FindOperation findOp = (FindOperation) lookupMessage.body;

        if(findOp != null){
            lookupMessage.receiver.getFindOperationsMap().remove(findOp.operationId);
//            System.err.println("FINAL CLOSEST SET IS");
//            System.err.println(findOp.printClosestSet());
            Statistician.updateLookupStatistics((KadNode) lookupMessage.receiver, findOp, kademliaid);
        } else {
            System.err.println(" something weird is going on: the findOp in the response message in inter-domain lookup is null");
        }
    }
}
