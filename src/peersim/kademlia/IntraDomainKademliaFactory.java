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
     * @param kademliaid
     * @param lookupMessage
     * @param tid
     * @return
     */
    @Override
    public RequestOperation createNaiveRequestOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadRequestOperation(kademliaid, lookupMessage, tid);
    }

    /**
     * Create the respond operation for an intra-domain lookup.
     * @param kademliaid
     * @param lookupMessage
     * @param tid
     * @return
     */
    @Override
    public RespondOperation createNaiveRespondOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadRespondOperation(kademliaid, lookupMessage, tid);
    }

    /**
     * Create a handle response operation for an intra-domain lookup.
     * @param kademliaid
     * @param lookupMessage
     * @param tid
     * @return
     */
    @Override
    public HandleResponseOperation createNaiveHandleResponseOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
    }

}
