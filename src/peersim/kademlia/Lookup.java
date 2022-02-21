package peersim.kademlia;

import peersim.kademlia.FindOperations.RequestOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

/**
 * The lookup object that needs to be prepared and then can be used to perform the main lookup operations.
 */
public abstract class Lookup {
    String type;
    RequestOperation findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;
    Message lookupMessage;
    int kademliaid;
    int transportid;

    abstract void prepare(int kademliaid, Message lookupMessage, int tid);

    abstract void performRequestOp();

    abstract void performRespondOp();

    abstract void performHandleResponseOp();

    void setType(String s) {
        this.type = s;
    }


}
