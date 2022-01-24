package peersim.kademlia;

import peersim.kademlia.FindOperations.BridgeToBridgeFindOperation;
import peersim.kademlia.FindOperations.BridgeToKadFindOperation;
import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToBridgeFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToBridgeHandleRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;


public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(int kademliaid, Message lookupMessage, int tid) {

        FindOperation2 fop = null;

        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        if (lookupMessage.sender instanceof KadNode && lookupMessage.target instanceof BridgeNode) {
            fop = new KadToBridgeFindOperation();
        } else if (lookupMessage.sender instanceof BridgeNode && lookupMessage.target instanceof KadNode) {
            fop = new BridgeToKadFindOperation();
        } else if (lookupMessage.sender instanceof BridgeNode && lookupMessage.target instanceof BridgeNode) {
            fop = new BridgeToBridgeFindOperation();
        }

        return fop;
    }

    @Override
    public RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid) {
        return null;
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid) {
        return new KadToBridgeHandleRespondOperation();
    }

}
