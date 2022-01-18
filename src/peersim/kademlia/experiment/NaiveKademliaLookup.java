package peersim.kademlia.experiment;

public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif){
        this.lif2 = lif;
    }

    @Override
    void prepare() {
        findOp = lif2.createFindOperation();
        resOp = lif2.createRespondOperation();
        handleResOp = lif2.createHandleResponseOperation();
    }

    @Override
    void performLookup() {
        findOp.find();

    }
}
