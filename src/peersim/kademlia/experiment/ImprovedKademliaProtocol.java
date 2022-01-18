package peersim.kademlia.experiment;

public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory2 lookupFactory2;

    public ImprovedKademliaProtocol(LookupIngredientFactory2 lookupFactory2) {
        this.lookupFactory2 = lookupFactory2;
    }

    @Override
    void prepare() {
        findOp = lookupFactory2.createFindOperation();
        resOp = lookupFactory2.createRespondOperation();
        handleResOp = lookupFactory2.createHandleResponseOperation();
    }

    @Override
    void performLookup() {

    }
}
