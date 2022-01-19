package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KadNode;
import peersim.kademlia.KademliaNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        FindOperation2 fop2 =  new KadToKadFindOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        System.err.println("we just have created a KadToKadFindOperation");
        return fop2;
    }

    @Override
    public RespondOperation2 createRespondOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        return new KadToKadRespondOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        return new KadToKadHandleResponseOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }
}
