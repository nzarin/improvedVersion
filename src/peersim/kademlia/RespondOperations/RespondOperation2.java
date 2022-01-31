package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

public abstract class RespondOperation2 {
    int kademliaid;
    Message lookupMessage;
    MessageSender messageSender;

    public abstract void respond();
}
