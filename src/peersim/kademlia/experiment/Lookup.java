package peersim.kademlia.experiment;

public abstract class Lookup {
    String type;
    FindOperation2 findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;

    abstract void prepare();
    abstract void performLookup();

    void setType(String s){
        this.type = s;
    }



}
