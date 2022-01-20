package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToKadFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToKadHandleResponseOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        //this is the problem, we create a new operation every time. So there is no side effects (not modifying the correct sentMsg set for example)
        FindOperation2 fop2 =  new KadToKadFindOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
        return fop2;
    }

    @Override
    public RespondOperation2 createRespondOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        return new KadToKadRespondOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        return new KadToKadHandleResponseOperation((KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);
    }

}
