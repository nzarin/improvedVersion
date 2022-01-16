package peersim.kademlia.experiment;

public class NaiveKademliaLookup extends Lookup {
    LookupFactory2 lookupFactory2;

    public NaiveKademliaLookup(LookupFactory2 fact){
        this.lookupFactory2 = fact;
    }


    @Override
    void create() {
        findOp = lookupFactory2.createFindOperation();
        resOp = lookupFactory2.createRespondOperation();
        handleResOp = lookupFactory2.createHandleResponseOperation();

    }
}
