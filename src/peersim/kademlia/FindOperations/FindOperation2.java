package peersim.kademlia.FindOperations;

import peersim.kademlia.*;


public abstract class FindOperation2 {

    KadNode source;
    KadNode target;
    KademliaNode sender;
    KademliaNode receiver;
    int kademliaid;
    Message lookupMessage;
    int transportid;
    MessageSender messageSender;

    public abstract void find();

}
