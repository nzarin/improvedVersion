package peersim.kademlia;

/**
 * This class specifies the naive variant of the Kademlia protocol for cross-DHT lookups.
 */
public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif) {
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
        this.source = lookupMsg.src;
        this.target = lookupMsg.target;
        this.sender = lookupMsg.sender;
        this.receiver = lookupMsg.receiver;
        this.kademliaid = kid;
        this.transportid = tid;

    }

    /**
     * Perform the find operation of the naive kademlia variant.
     */
    @Override
    public void performFindOp() {
        findOp = lif2.createFindOperation(kademliaid, lookupMessage, transportid);
        findOp.find();

    }

    /**
     * Perform the respond operation of the naive kademlia variant.
     */
    @Override
    public void performRespondOp() {
        resOp = lif2.createRespondOperation(kademliaid, lookupMessage, transportid);
        resOp.respond();

    }

    /**
     * Handle the response for the naive kademlia variant.
     */
    @Override
    public void performHandleResponseOp() {
        handleResOp = lif2.createHandleResponseOperation(kademliaid, lookupMessage, transportid);
        handleResOp.handleResponse();

    }
}
