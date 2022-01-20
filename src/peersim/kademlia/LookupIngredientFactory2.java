package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface LookupIngredientFactory2 {
      FindOperation2 createFindOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long, Long> sentMsg, int tid);
      RespondOperation2 createRespondOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
      HandleResponseOperation2 createHandleResponseOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);

}
