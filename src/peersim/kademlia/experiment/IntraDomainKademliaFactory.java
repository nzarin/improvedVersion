package peersim.kademlia.experiment;

public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation() {
        return new KadToKadFindOperation();
    }

    @Override
    public RespondOperation2 createRespondOperation() {
        return new KadToKadRespondOperation();
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation() {
        return new KadToKadHandleResponseOperation();
    }
}
