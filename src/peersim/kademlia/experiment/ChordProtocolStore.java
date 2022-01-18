package peersim.kademlia.experiment;

public class ChordProtocolStore extends DHTProtocolStore {

    @Override
    Lookup createLookup(String type) {
        System.err.println("Chord lookup not implemented yet");
        return null;
    }
}
