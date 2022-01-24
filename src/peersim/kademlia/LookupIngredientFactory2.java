package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface LookupIngredientFactory2 {
      FindOperation2 createFindOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid);
      RespondOperation2 createRespondOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid);
      HandleResponseOperation2 createHandleResponseOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid);

}
