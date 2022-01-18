package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public interface LookupIngredientFactory2 {
     FindOperation2 createFindOperation(KademliaNode s, KademliaNode r);
     RespondOperation2 createRespondOperation();
     HandleResponseOperation2 createHandleResponseOperation();
}
