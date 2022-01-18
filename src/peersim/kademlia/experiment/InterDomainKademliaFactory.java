package peersim.kademlia.experiment;

import peersim.kademlia.BridgeNode;
import peersim.kademlia.KadNode;
import peersim.kademlia.KademliaNode;

public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode s, KademliaNode r) {

        FindOperation2 fop = null;

        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        if(s instanceof KadNode && r instanceof BridgeNode){
            fop = new KadToBridgeFindOperation();
        } else if(s instanceof BridgeNode && r instanceof KadNode){
            fop = new BridgeToKadFindOperation();
        } else if(s instanceof BridgeNode && r instanceof BridgeNode){
            fop = new BridgeToBridgeFindOperation();
        }

        return fop;
    }

    @Override
    public RespondOperation2 createRespondOperation() {
        return new KadToBridgeRespondOperation();
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation() {
        return new KadToKadHandleResponseOperation();
    }
}
