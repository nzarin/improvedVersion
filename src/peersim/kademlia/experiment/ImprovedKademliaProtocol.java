package peersim.kademlia.experiment;

public class ImprovedKademliaProtocol extends Lookup {

    LookupFactory2 lookupFactory2;

    public ImprovedKademliaProtocol(LookupFactory2 lookupFactory2) {
        this.lookupFactory2 = lookupFactory2;
    }

    @Override
    void create() {
        findOp = lookupFactory2.createFindOperation();
        resOp = lookupFactory2.createRespondOperation();
        handleResOp = lookupFactory2.createHandleResponseOperation();
    }
}
