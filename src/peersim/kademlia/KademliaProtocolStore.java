package peersim.kademlia;

import java.util.ArrayList;

/**
 * This class represents the store that contrains and creates all kademlia Lookups
 */
public class KademliaProtocolStore extends DHTProtocolStore {

    ArrayList<Lookup> lookups;

    public KademliaProtocolStore() {
        this.lookups = new ArrayList<>();
    }

    /**
     * Create a lookup with the corresponding factory.
     *
     * @param version
     * @param s
     * @param r
     * @return
     */
    @Override
    Lookup createLookup(String version, KademliaNode s, KademliaNode r) {
        Lookup lookup = null;

        //determine what type of lookup we have and create the correct factory
        switch (version) {
            case "naive":
                if (s.getDomain() == r.getDomain()) {
                    lookup = new NaiveKademliaLookup(new IntraDomainKademliaFactory());
                } else {
                    lookup = new NaiveKademliaLookup(new InterDomainKademliaFactory());
                }
                lookup.setType("naive kademlia lookup");
                break;
            case "improved":
                if (s.getDomain() == r.getDomain()) {
                    lookup = new ImprovedKademliaProtocol(new IntraDomainKademliaFactory());
                } else {
                    lookup = new ImprovedKademliaProtocol(new InterDomainKademliaFactory());
                }
                lookup.setType("naive kademlia lookup");
                break;
        }

        lookups.add(lookup);
        return lookup;
    }

}
