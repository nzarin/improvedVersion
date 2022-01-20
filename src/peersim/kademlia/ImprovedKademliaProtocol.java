package peersim.kademlia;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class ImprovedKademliaProtocol extends Lookup {

    LookupIngredientFactory2 lookupFactory2;

    public ImprovedKademliaProtocol(LookupIngredientFactory2 lookupFactory2) {
        this.lookupFactory2 = lookupFactory2;
    }

    @Override
    void prepare( KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        findOp = lookupFactory2.createFindOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        resOp = lookupFactory2.createRespondOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        handleResOp = lookupFactory2.createHandleResponseOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }

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
