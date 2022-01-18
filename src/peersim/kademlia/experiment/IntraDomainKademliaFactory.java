package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode sender, KademliaNode receiver) {

        // step 1: determine what type of findOperation this is -> for intra domain only one type -> Kad2KadFind
        // step 2: create the correct one
        FindOperation2 fop2 =  new KadToKadFindOperation();

        return fop2;
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
