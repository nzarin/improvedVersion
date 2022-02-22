package peersim.kademlia.RequestOperations;

import peersim.kademlia.Message;
import peersim.kademlia.MessageSender;

public class KadToOctopusRequestOperation extends RequestOperation {

    public KadToOctopusRequestOperation(int kid, Message lookupMsg, int tid) {
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void find() {

    }

}
