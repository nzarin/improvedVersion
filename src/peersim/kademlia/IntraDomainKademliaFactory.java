package peersim.kademlia;

import peersim.kademlia.RequestOperations.RequestOperation;
import peersim.kademlia.RequestOperations.KadToKadRequestOperation;
import peersim.kademlia.HandleResponseOperations.HandleResponseOperation;
import peersim.kademlia.HandleResponseOperations.KadToKadHandleResponseOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation;

/**
 * This class represents a factory that creates the correct operations for an intra-domain lookup.
 */
public class IntraDomainKademliaFactory implements LookupIngredientFactory {

    /**
     * Create the find operation for an intra-domain lookup.
     */
    @Override
    public RequestOperation createRequestOperation(Lookup lookup) {
        return new KadToKadRequestOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
    }

    /**
     * Create the respond operation for an intra-domain lookup.
     */
    @Override
    public RespondOperation createRespondOperation(Lookup lookup) {
        return new KadToKadRespondOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
    }

    /**
     * Create a handle response operation for an intra-domain lookup.
     */
    @Override
    public HandleResponseOperation createHandleResponseOperation(Lookup lookup) {
        return new KadToKadHandleResponseOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
    }


}
