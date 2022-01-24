package peersim.kademlia;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory2 lif2;

    public ImprovedKademliaProtocol(LookupIngredientFactory2 lookupFactory2) {
        this.lif2 = lookupFactory2;
    }

    @Override
    void prepare( KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kid, Message lookupMsg, int tid) {
        this.lookupMessage = lookupMsg;
        this.source = source;
        this.target = target;
        this.sender = sender;
        this.receiver = receiver;
        this.kademliaid = kid;
        this.transportid = tid;
        this.findOp = lif2.createFindOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
        this.resOp = lif2.createRespondOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
        this.handleResOp = lif2.createHandleResponseOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);

    }

    /**
     * Added new shizzle
     */
    @Override
    public void performFindOp() {

    }

    @Override
    public void performRespondOp() {

    }

    @Override
    public void performHandleResponseOp() {

    }


}
