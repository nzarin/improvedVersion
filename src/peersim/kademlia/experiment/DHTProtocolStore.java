package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public abstract class DHTProtocolStore {

    private Lookup lookup;

    public Lookup orderLookup(String type, KademliaNode s, KademliaNode r){

        //create either the correct protocol (naive or improved) and the corresponding factory
        this.lookup = createLookup(type, s, r);

        //initialize all the operations and prepare to perform one
        lookup.prepare(s, r);

        return lookup;
    }

    //note that the factory method is now abstract in DHTProtocolStore
    abstract Lookup createLookup(String type, KademliaNode sender, KademliaNode receiver);

    public void performFindOperation(){
        this.lookup.performFindOp();
    }

}
