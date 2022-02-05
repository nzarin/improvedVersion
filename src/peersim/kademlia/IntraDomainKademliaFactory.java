package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToKadFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToKadHandleResponseOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;

/**
 * This class represents a factory that creates the correct operations for an intra-domain lookup.
 */
public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    /**
     * Create the find operation for an intra-domain lookup.
     * @param kademliaid
     * @param lookupMessage
     * @param tid
     * @return
     */
    @Override
    public FindOperation2 createFindOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadFindOperation(kademliaid, lookupMessage, tid);
    }

    /**
     * Create the respond operation for an intra-domain lookup.
     * @param kademliaid
     * @param lookupMessage
     * @param tid
     * @return
     */
    @Override
    public RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid) {
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
    public HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
    }

}
