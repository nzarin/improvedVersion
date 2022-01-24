package peersim.kademlia;

import peersim.kademlia.FindOperations.BridgeToBridgeFindOperation;
import peersim.kademlia.FindOperations.BridgeToKadFindOperation;
import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToBridgeFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToBridgeHandleRespondOperation;
import peersim.kademlia.RespondOperations.KadToBridgeRespondOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid) {

        FindOperation2 fop = null;

        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        if(source instanceof KadNode && target  instanceof BridgeNode){
            fop = new KadToBridgeFindOperation();
        } else if(sender instanceof BridgeNode && target instanceof KadNode){
            fop = new BridgeToKadFindOperation();
        } else if(sender instanceof BridgeNode && target instanceof BridgeNode){
            fop = new BridgeToBridgeFindOperation();
        }

        return fop;
    }

    @Override
    public RespondOperation2 createRespondOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, int tid) {
        return new KadToKadRespondOperation((KadNode) source, (KadNode) target, (KadNode) sender, (KadNode) destination, kademliaid, lookupMessage, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid) {
        return new KadToBridgeHandleRespondOperation();
    }

}
