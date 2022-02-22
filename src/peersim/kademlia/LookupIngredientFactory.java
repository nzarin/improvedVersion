package peersim.kademlia;

import peersim.kademlia.RequestOperations.RequestOperation;
import peersim.kademlia.HandleResponseOperations.HandleResponseOperation;
import peersim.kademlia.RespondOperations.RespondOperation;

/**
 * The factory class that creates all the ingredients of a lookup: FindOperation, RespondOperation and HandleRespondOperation
 */
public interface LookupIngredientFactory {

    RequestOperation createRequestOperation(Lookup lookup);

    RespondOperation createRespondOperation(Lookup lookup);

    HandleResponseOperation createHandleResponseOperation(Lookup lookup);


}
