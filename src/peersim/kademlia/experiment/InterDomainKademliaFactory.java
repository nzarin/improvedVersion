package peersim.kademlia.experiment;

import peersim.kademlia.*;

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
