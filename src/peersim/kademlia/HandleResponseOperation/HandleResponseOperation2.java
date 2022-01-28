package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

public abstract class HandleResponseOperation2 {

    KadNode source;
    KadNode target;
    KademliaNode sender;
    KademliaNode receiver;
    int kademliaid;
    Message lookupMessage;
    int transportid;
    MessageSender messageSender;

    public abstract void handleResponse();

}
