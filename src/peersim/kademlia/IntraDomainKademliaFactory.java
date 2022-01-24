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

    @Override
    public FindOperation2 createFindOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadFindOperation(kademliaid, lookupMessage, tid);
    }

    @Override
    public RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadRespondOperation(kademliaid, lookupMessage, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
    }

}
