package peersim.kademlia;

import peersim.kademlia.FindOperations.*;
import peersim.kademlia.HandleResponseOperation.*;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;


public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(int kademliaid, Message lookupMessage, int tid) {

        FindOperation2 fop = null;

        //for the first message its always the case that the receiver is the source -> so we have a kad to bridge find operation
        if (lookupMessage.receiver == lookupMessage.src) {
//            System.err.println("We have a new lookup request ");
            fop = new KadToBridgeFindOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
//            System.err.println("it is a bridge to bridge find operation ");
            fop = new BridgeToBridgeFindOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
//            System.err.println("it is a bridge to kad find operation ");
            fop = new BridgeToKadFindOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
//            System.err.println("it is a kad to kad find operation ");
            fop = new KadToKadFindOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
//            System.err.println("it is a kad to bridge find operation ");
            fop = new KadToBridgeFindOperation(kademliaid, lookupMessage, tid);
        }

        return fop;
    }

    @Override
    public RespondOperation2 createRespondOperation(int kademliaid, Message lookupMessage, int tid) {
        //because the lookup operation will eventually always be
//        System.err.println("it is a kad to kad respond operation ");
        return new KadToKadRespondOperation(kademliaid, lookupMessage, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(int kademliaid, Message lookupMessage, int tid) {
        HandleResponseOperation2 handleResponseOperation2 = null;

        if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
//            System.err.println("it is a kad to bridge handleresponse operation ");
            handleResponseOperation2 = new KadToBridgeHandleRespondOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
//            System.err.println("it is a bridge to bridge handleresponse operation ");
            handleResponseOperation2 = new BridgeToBridgeHandleResponseOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
//            System.err.println("it is a bridge to kad handleresponse operation ");
            handleResponseOperation2 = new BridgeToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
//            System.err.println("it is a kad to kad handleresponse operation ");
            handleResponseOperation2 = new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
        }
        return handleResponseOperation2;
    }

}
