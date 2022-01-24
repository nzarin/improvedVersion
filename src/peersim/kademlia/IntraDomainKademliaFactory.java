package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.FindOperations.KadToKadFindOperation;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.HandleResponseOperation.KadToKadHandleResponseOperation;
import peersim.kademlia.RespondOperations.KadToKadRespondOperation;
import peersim.kademlia.RespondOperations.RespondOperation2;


public class IntraDomainKademliaFactory implements LookupIngredientFactory2 {

    @Override
    public FindOperation2 createFindOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid) {
        //this is the problem, we create a new operation every time. So there is no side effects (not modifying the correct sentMsg set for example)
//        System.err.println("~IntraDomainKademliaFactory~ createFindOperation()");
        FindOperation2 fop2 =  new KadToKadFindOperation((KadNode) source, (KadNode) target, (KadNode) sender, (KadNode) receiver, kademliaid, lookupMessage, tid);
        return fop2;
    }

    @Override
    public RespondOperation2 createRespondOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage, int tid) {
//        System.err.println("~IntraDomainKademliaFactory~ createRespondOperation()");
        return new KadToKadRespondOperation((KadNode) source, (KadNode) target, (KadNode) sender, (KadNode) receiver, kademliaid, lookupMessage, tid);
    }

    @Override
    public HandleResponseOperation2 createHandleResponseOperation(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kademliaid, Message lookupMessage,  int tid) {
//        System.err.println("~IntraDomainKademliaFactory~ createHandleResponseOperation()");
        return new KadToKadHandleResponseOperation((KadNode) source, (KadNode) target, (KadNode) sender, (KadNode) receiver, kademliaid, lookupMessage, tid);
    }

}
