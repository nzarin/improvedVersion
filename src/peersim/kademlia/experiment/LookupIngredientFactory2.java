package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KademliaNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface LookupIngredientFactory2 {
     FindOperation2 createFindOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
     RespondOperation2 createRespondOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
     HandleResponseOperation2 createHandleResponseOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
}
