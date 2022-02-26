package peersim.kademlia.HandleResponseOperations;

import peersim.kademlia.*;

public abstract class HandleResponseOperation {

    int kademliaid;
    Message lookupMessage;
    int transportid;
    MessageSender messageSender;

    public abstract void handleResponse();

}
