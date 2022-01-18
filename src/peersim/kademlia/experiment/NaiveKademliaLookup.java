package peersim.kademlia.experiment;

import peersim.kademlia.KadNode;
import peersim.kademlia.KademliaNode;

public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif){
        this.lif2 = lif;
    }

    @Override
    void prepare(KademliaNode s, KademliaNode r) {
        System.err.println("we are now in the perform lookup of NaiveKademliaLookup class");
        findOp = lif2.createFindOperation(s, r);
        resOp = lif2.createRespondOperation();
        handleResOp = lif2.createHandleResponseOperation();
    }

    @Override
    void performFindOp() {
        System.err.println("we are now in the perform lookup of NaiveKademliaLookup class");
        findOp.find();
        resOp.respond();
        handleResOp.handleResponse();
    }

    @Override
    void performRespondOp() {

    }

    @Override
    void performHandleResponseOp() {

    }
}
