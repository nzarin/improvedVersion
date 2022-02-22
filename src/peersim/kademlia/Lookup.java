package peersim.kademlia;

import peersim.kademlia.RequestOperation.RequestOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation;

/**
 * The lookup object that needs to be prepared and then can be used to perform the main lookup operations.
 */
public abstract class Lookup {
    String type;
    RequestOperation findOp;
    RespondOperation resOp;
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
