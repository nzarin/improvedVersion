package peersim.kademlia;

/**
 * This class represents the improved kademlia version
 */
public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory lif2;

    public ImprovedKademliaProtocol(LookupIngredientFactory lookupFactory2) {
        this.lif2 = lookupFactory2;
    }

    @Override
    void prepare(int kid, Message lookupMsg, int tid) {
        this.lookupMessage = lookupMsg;
        this.kademliaid = kid;
        this.transportid = tid;
    }

    /**
     * Perform the correct version of teh find operation for the improved kademlia version.
     */
    @Override
    public void performRequestOp() {
        this.findOp = lif2.createRequestOperation(this);
        findOp.find();

    }

    /**
     * Perform the correct version of the respond operation for the improved kademlia version.
     */
    @Override
    public void performRespondOp() {
        this.resOp = lif2.createRespondOperation(this);
        resOp.respond();
    }

    /**
     * Perform the correct version of the handle response operation for the improved kademlia version.
     */
    @Override
    public void performHandleResponseOp() {
        this.handleResOp = lif2.createHandleResponseOperation(this);
        handleResOp.handleResponse();
    }


}
