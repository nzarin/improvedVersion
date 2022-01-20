package peersim.kademlia;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif){
        this.lif2 = lif;
    }

    @Override
    void prepare(KademliaNode sender, KademliaNode dest, int kid, Message lookupMsg, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        lookupMessage = lookupMsg;
        source = sender;
        destination = dest;
        kademliaid = kid;
        transportid = tid;
        findOperationsMap = findOpsMap;
        sentMsgTracker = sentMsg;

    }

    @Override
    public void performFindOp() {
        findOp = lif2.createFindOperation(source, destination, kademliaid, lookupMessage, findOperationsMap, sentMsgTracker, transportid);
        findOp.find();

    }

    @Override
    public void performRespondOp() {

        //todo: problem here is that we don't use the findOp which contains the latest knowledge on the different sets
        resOp = lif2.createRespondOperation(source, destination, kademliaid, lookupMessage, findOperationsMap, sentMsgTracker, transportid);
        resOp.respond();

    }

    @Override
    public void performHandleResponseOp() {
        handleResOp = lif2.createHandleResponseOperation(source, destination, kademliaid, lookupMessage, findOperationsMap, sentMsgTracker, transportid);
        handleResOp.handleResponse();

    }
}
