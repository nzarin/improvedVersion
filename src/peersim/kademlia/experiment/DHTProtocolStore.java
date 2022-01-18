package peersim.kademlia.experiment;

public abstract class DHTProtocolStore {

    public Lookup orderLookup(String type){
        Lookup lookup;

        //now createLookup is back to being a call to a method in the DHTProtocolStore rather than on a factory object.
        lookup = createLookup(type);

        //todo: check if this should be here
        lookup.prepare();

        //todo: check if this should be here
        lookup.performLookup();

        return lookup;
    }

    //note that the factory method is now abstract in DHTProtocolStore
    abstract Lookup createLookup(String type);
}
