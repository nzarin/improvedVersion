package peersim.kademlia;

import peersim.kademlia.RequestOperation.RequestOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

/**
 * The factory class that creates all the ingredients of a lookup: FindOperation, RespondOperation and HandleRespondOperation
 */
public interface LookupIngredientFactory2 {
    RequestOperation createRequestOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid);

    HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid);

}
