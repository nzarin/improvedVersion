package peersim.kademlia;

/**
 * This class represents the general store for all sorts of DHT lookup protocols
 */
public abstract class DHTProtocolStore {

    public Lookup lookup = null;

    public Lookup orderLookup(String type, Message lookupMessage) {

        //create either the correct protocol (naive or improved) and the corresponding intra or inter-domain factory
        this.lookup = createLookup(type, lookupMessage.src, lookupMessage.target);

        return this.lookup;
    }

    //note that the factory method is now abstract in DHTProtocolStore
    abstract Lookup createLookup(String type, KademliaNode source, KademliaNode target);

    public Lookup getLookup() {
        return lookup;
    }
}
