package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

/**
 * The factory class that creates all the ingredients of a lookup: FindOperation, RespondOperation and HandleRespondOperation
 */
public interface LookupIngredientFactory2 {
    FindOperation2 createFindOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid);

    HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid);

}
