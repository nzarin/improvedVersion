package peersim.kademlia;

import peersim.kademlia.FindOperations.BridgeToBridgeFindOperation;
import peersim.kademlia.FindOperations.BridgeToKadFindOperation;
import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToBridgeFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToBridgeHandleRespondOperation;
import peersim.kademlia.RespondOperations.KadToBridgeRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {

        FindOperation2 fop = null;

        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        if(sender instanceof KadNode && destination instanceof BridgeNode){
            fop = new KadToBridgeFindOperation();
        } else if(sender instanceof BridgeNode && destination instanceof KadNode){
            fop = new BridgeToKadFindOperation();
        } else if(sender instanceof BridgeNode && destination instanceof BridgeNode){
            fop = new BridgeToBridgeFindOperation();
        }

        return fop;
    }

    @Override
    public RespondOperation2 createRespondOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        return new KadToBridgeRespondOperation();
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        return new KadToBridgeHandleRespondOperation();
    }

}
