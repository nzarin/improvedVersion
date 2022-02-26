package peersim.kademlia;

import peersim.kademlia.HandleResponseOperations.*;
import peersim.kademlia.RequestOperations.*;
import peersim.kademlia.RespondOperations.ImprovedRespondOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation;


public class InterDomainKademliaFactory implements LookupIngredientFactory {


    @Override
    public RequestOperation createRequestOperation(Lookup lookup) {
        int kademliaid = lookup.kademliaid;
        Message lookupMessage = lookup.lookupMessage;
        int tid = lookup.transportid;


        RequestOperation requestOperation = null;

        if(lookup.type.equals("naive")){
            //for the first message its always the case that the receiver is the source -> so we have a kad to bridge find operation
            if (lookupMessage.receiver == lookupMessage.src) {
                requestOperation = new KadToBridgeRequestOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
                requestOperation = new BridgeToBridgeRequestOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
                requestOperation = new BridgeToKadRequestOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
                requestOperation = new KadToKadRequestOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
                requestOperation = new KadToBridgeRequestOperation(kademliaid, lookupMessage, tid);
            }
        } else {
            requestOperation = new ImprovedInterRequestOperation(kademliaid, lookupMessage, tid);
        }

        return requestOperation;

    }

    @Override
    public RespondOperation createRespondOperation(Lookup lookup) {
        if(lookup.type.equals("naive")){
            return new KadToKadRespondOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
        } else {
            return new ImprovedRespondOperation(lookup.kademliaid, lookup.lookupMessage, lookup.transportid);
        }
    }

    @Override
    public HandleResponseOperation createHandleResponseOperation(Lookup lookup) {
        int kademliaid = lookup.kademliaid;
        Message lookupMessage = lookup.lookupMessage;
        int tid = lookup.transportid;

        HandleResponseOperation handleResponseOperation = null;
        if(lookup.type.equals("Naive")){
            if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof KadNode) {
                handleResponseOperation = new KadToBridgeHandleRespondOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof BridgeNode && lookupMessage.sender instanceof BridgeNode) {
                handleResponseOperation = new BridgeToBridgeHandleResponseOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof BridgeNode) {
                handleResponseOperation = new BridgeToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
            } else if (lookupMessage.receiver instanceof KadNode && lookupMessage.sender instanceof KadNode) {
                handleResponseOperation = new KadToKadHandleResponseOperation(kademliaid, lookupMessage, tid);
            }
        } else{
            handleResponseOperation = new ImprovedHandleResponseOperation(kademliaid, lookupMessage, tid);
        }

        return handleResponseOperation;
    }


}
