package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

public abstract class RespondOperation2 {
    KadNode source;
    KadNode target;
    KademliaNode sender;
    KademliaNode receiver;
    int kademliaid;
    Message lookupMessage;
    MessageSender messageSender;

    public abstract void respond();
}
