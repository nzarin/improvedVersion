package peersim.kademlia;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class NaiveKademliaLookup extends Lookup {
    LookupIngredientFactory2 lif2;

    public NaiveKademliaLookup(LookupIngredientFactory2 lif){
        this.lif2 = lif;
    }

    @Override
    void prepare(KademliaNode source, KademliaNode target, KademliaNode sender, KademliaNode receiver, int kid, Message lookupMsg, int tid) {

        System.err.println(" ~ NaiveKademliaLookup.java~ prepare() ");
        this.lookupMessage = lookupMsg;
        this.source = source;
        this.target = target;
        this.sender = sender;
        this.receiver = receiver;
        this.kademliaid = kid;
        this.transportid = tid;
//        this.findOp = lif2.createFindOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
//        this.resOp = lif2.createRespondOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
//        this.handleResOp = lif2.createHandleResponseOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);

    }

    @Override
    public void performFindOp() {
        System.err.println(" ~ NaiveKademliaLookup.java~ performFindOp() ");
        findOp = lif2.createFindOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
        findOp.find();

    }

    @Override
    public void performRespondOp() {
        System.err.println(" ~ NaiveKademliaLookup.java~ performRespondOp() ");
        //todo: problem here is that we don't use the findOp which contains the latest knowledge on the different sets
        resOp = lif2.createRespondOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
        resOp.respond();

    }

    @Override
    public void performHandleResponseOp() {
        System.err.println(" ~ NaiveKademliaLookup.java~ performHandleResponseOp() ");
        handleResOp = lif2.createHandleResponseOperation(source, target, sender, receiver, kademliaid, lookupMessage, transportid);
        handleResOp.handleResponse();

    }
}
