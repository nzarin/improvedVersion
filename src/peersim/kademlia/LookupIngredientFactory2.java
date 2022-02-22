package peersim.kademlia;

import peersim.kademlia.RequestOperation.RequestOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation;

/**
 * The factory class that creates all the ingredients of a lookup: FindOperation, RespondOperation and HandleRespondOperation
 */
public interface LookupIngredientFactory2 {
    RequestOperation createNaiveRequestOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation createNaiveRespondOperation(int kademliaid, Message lookupMessage, int tid);

    HandleResponseOperation2 createNaiveHandleResponseOperation(int kademliaid, Message lookupMessage, int tid);

}
