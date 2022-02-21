package peersim.kademlia.FindOperations;

import peersim.kademlia.*;

/**
 * Abstracts class that generalizes the find operation.
 */
public abstract class RequestOperation {
    int kademliaid;
    Message lookupMessage;
    int transportid;
    MessageSender messageSender;

    public abstract void find();

}
