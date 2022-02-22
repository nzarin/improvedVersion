package peersim.kademlia;

/**
 * This class specifies the naive variant of the Kademlia protocol for cross-DHT lookups.
 */
public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory lif2;

    public NaiveKademliaLookup(LookupIngredientFactory lif) {
        this.lif2 = lif;
    }

    /**
     * Prepare the parameters (functions as the constructor)
     *
     * @param kid
     * @param lookupMsg
     * @param tid
     */
    @Override
    void prepare(int kid, Message lookupMsg, int tid) {
        this.lookupMessage = lookupMsg;
        this.kademliaid = kid;
        this.transportid = tid;

    }

    /**
     * Perform the find operation of the naive kademlia variant.
     */
    @Override
    public void performRequestOp() {
        findOp = lif2.createRequestOperation(this);
        findOp.find();
    }

    /**
     * Perform the respond operation of the naive kademlia variant.
     */
    @Override
    public void performRespondOp() {
        resOp = lif2.createRespondOperation(this);
        resOp.respond();

    }

    /**
     * Handle the response for the naive kademlia variant.
     */
    @Override
    public void performHandleResponseOp() {
        handleResOp = lif2.createHandleResponseOperation(this);
        handleResOp.handleResponse();

    }
}
