package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public abstract class Lookup {
    String type;
    FindOperation2 findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;
    KademliaNode sender;
    KademliaNode receiver;

    abstract void prepare(KademliaNode sender, KademliaNode receiver);
    abstract void performFindOp();
    abstract void performRespondOp();
    abstract void performHandleResponseOp();

    void setType(String s){
        this.type = s;
    }


    void setSender(KademliaNode s){
        this.sender = s;
    }

    void setReiver(KademliaNode r){
        this.receiver = r;
    }


}
