package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KadNode;
import peersim.kademlia.KademliaNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif){
        this.lif2 = lif;
    }

    @Override
    void prepare(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        System.err.println("we are now in the perform lookup of NaiveKademliaLookup class");
        findOp = lif2.createFindOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        resOp = lif2.createRespondOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        handleResOp = lif2.createHandleResponseOperation(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }

    @Override
    public void performFindOp() {
        findOp.find();

    }

    @Override
    public void performRespondOp() {
        resOp.respond();

    }

    @Override
    public void performHandleResponseOp() {
        handleResOp.handleResponse();

    }
}
