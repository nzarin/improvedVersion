package peersim.kademlia;

/**
 * This class represents the improved kademlia version
 */
public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory2 lif2;

    public ImprovedKademliaProtocol(LookupIngredientFactory2 lookupFactory2) {
        this.lif2 = lookupFactory2;
    }

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
     * Perform the find operation for the improved kademlia version.
     */
    @Override
    public void performFindOp() {
        this.findOp = lif2.createFindOperation(kademliaid, lookupMessage, transportid);
        findOp.find();

    }

    @Override
    public void performRespondOp() {
        this.resOp = lif2.createRespondOperation(kademliaid, lookupMessage, transportid);
        resOp.respond();
    }

    @Override
    public void performHandleResponseOp() {
        this.handleResOp = lif2.createHandleResponseOperation(kademliaid, lookupMessage, transportid);
        handleResOp.handleResponse();
    }


}
