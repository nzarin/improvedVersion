package peersim.kademlia.experiment;

public class InterDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation() {
        return new KadToKadFindOperation();
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
