package peersim.kademlia.FindOperations;

import peersim.kademlia.*;

/**
 * Abstracts class that generalizes the find operation.
 */
public abstract class FindOperation2 {
    int kademliaid;
    Message lookupMessage;
    int transportid;
    MessageSender messageSender;

    public abstract void find();

}
