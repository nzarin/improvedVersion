package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory2 lookupFactory2;

    public ImprovedKademliaProtocol(LookupIngredientFactory2 lookupFactory2) {
        this.lookupFactory2 = lookupFactory2;
    }

    @Override
    void prepare(KademliaNode s, KademliaNode r) {
        findOp = lookupFactory2.createFindOperation(s, r);
        resOp = lookupFactory2.createRespondOperation();
        handleResOp = lookupFactory2.createHandleResponseOperation();
    }

    @Override
    void performFindOp() {

    }

    @Override
    void performRespondOp() {

    }

    @Override
    void performHandleResponseOp() {

    }


}
