package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KademliaNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class DHTProtocolStore {

    private Lookup lookup;

    public Lookup orderLookup(String type, KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid){

        //create either the correct protocol (naive or improved) and the corresponding factory
        this.lookup = createLookup(type, sender, destination);

        //initialize all the operations and prepare to perform one
        lookup.prepare(sender, destination, kademliaid, lookupMessage, findOpsMap, sentMsg, tid);

        return lookup;
    }

    //note that the factory method is now abstract in DHTProtocolStore
    abstract Lookup createLookup(String type, KademliaNode sender, KademliaNode receiver);

    public void performFindOperation(){
        this.lookup.performFindOp();
    }

}
