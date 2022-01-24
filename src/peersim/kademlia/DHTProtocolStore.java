package peersim.kademlia;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class DHTProtocolStore {

    public Lookup lookup = null;

    public Lookup orderLookup(String type, int kademliaid, Message lookupMessage, int tid){

        System.err.println("~DHTProtocolStore~ orderLookup()");

        //create either the correct protocol (naive or improved) and the corresponding intra or inter-domain factory
        this.lookup = createLookup(type, lookupMessage.src, lookupMessage.target);

        //initialize all the operations and prepare to perform one
        this.lookup.prepare(lookupMessage.src, lookupMessage.target, lookupMessage.sender, lookupMessage.receiver, kademliaid, lookupMessage, tid);

        return this.lookup;
    }

    //note that the factory method is now abstract in DHTProtocolStore
    abstract Lookup createLookup(String type, KademliaNode source, KademliaNode target);

    public Lookup getLookup() {
        return lookup;
    }
}
