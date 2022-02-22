package peersim.kademlia;

import peersim.kademlia.RequestOperations.RequestOperation;
import peersim.kademlia.HandleResponseOperations.HandleResponseOperation;
import peersim.kademlia.RespondOperations.RespondOperation;

/**
 * The factory class that creates all the ingredients of a lookup: FindOperation, RespondOperation and HandleRespondOperation
 */
public interface LookupIngredientFactory {
    RequestOperation createNaiveRequestOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation createNaiveRespondOperation(int kademliaid, Message lookupMessage, int tid);

    HandleResponseOperation createNaiveHandleResponseOperation(int kademliaid, Message lookupMessage, int tid);

    RequestOperation createImprovedRequestOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation createImprovedRespondOperation(int kademliaid, Message lookupMessage, int tid);

    RespondOperation createImprovedHandleResponseOperation(int kademliaid, Message lookupMessage, int tid);

}
