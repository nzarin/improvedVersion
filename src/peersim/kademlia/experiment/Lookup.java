package peersim.kademlia.experiment;

public abstract class Lookup {
    FindOperation2 findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;

    abstract void create();

    void performLookup() {

    }

}
