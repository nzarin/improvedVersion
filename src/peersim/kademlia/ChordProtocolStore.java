package peersim.kademlia;

public class ChordProtocolStore extends DHTProtocolStore {

    @Override
    Lookup createLookup(String type, KademliaNode s, KademliaNode r) {
        System.err.println("Chord lookup not implemented yet");
        return null;
    }
}
