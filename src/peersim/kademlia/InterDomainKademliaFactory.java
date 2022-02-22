package peersim.kademlia;

import peersim.kademlia.HandleResponseOperations.*;
import peersim.kademlia.RequestOperations.*;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation;


public class InterDomainKademliaFactory implements LookupIngredientFactory {


    @Override
    public RequestOperation createRequestOperation(Lookup lookup) {
        int kademliaid = lookup.kademliaid;
        Message lookupMessage = lookup.lookupMessage;
        int tid = lookup.transportid;

        RequestOperation fop = null;

        //for the first message its always the case that the receiver is the source -> so we have a kad to bridge find operation
        if (lookupMessage.receiver == lookupMessage.src) {
            fop = new KadToBridgeRequestOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
            fop = new BridgeToBridgeRequestOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
            fop = new BridgeToKadRequestOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
            fop = new KadToKadRequestOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
            fop = new KadToBridgeRequestOperation(kademliaid, lookupMessage, tid);
        }
        return fop;
    }

    @Override
    public RespondOperation createRespondOperation(Lookup lookup) {
        //because the lookup operation will eventually always be
        return new KadToKadRespondOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
    }

    @Override
    public HandleResponseOperation createHandleResponseOperation(Lookup lookup) {
        int kademliaid = lookup.kademliaid;
        Message lookupMessage = lookup.lookupMessage;
        int tid = lookup.transportid;

        HandleResponseOperation handleResponseOperation = null;

        if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
            handleResponseOperation = new KadToBridgeHandleRespondOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
            handleResponseOperation = new BridgeToBridgeHandleResponseOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
            handleResponseOperation = new BridgeToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
        } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
            handleResponseOperation = new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
        }
        return handleResponseOperation;
    }


}
