package peersim.kademlia;

import java.util.ArrayList;

public class KademliaProtocolStore extends DHTProtocolStore{

    ArrayList<Lookup> lookups;

    public KademliaProtocolStore() {
        this.lookups = new ArrayList<>();
    }

    @Override
    Lookup createLookup(String type, KademliaNode s, KademliaNode r) {

        Lookup lookup = null;

        //determine what type of lookup we have and create the correct factory
        LookupIngredientFactory2 interDomainLookupFactory = new InterDomainKademliaFactory();
        LookupIngredientFactory2 intraDomainLookupFactory = new IntraDomainKademliaFactory();

        switch(type){
            case "naive":
                if(s.getDomain() == r.getDomain()){
                    lookup = new NaiveKademliaLookup(intraDomainLookupFactory);
                } else {
                    lookup = new NaiveKademliaLookup(interDomainLookupFactory);
                }
                lookup.setType("naive kademlia lookup");
                break;
            case "improved":
                if(s.getDomain() == r.getDomain()){
                    lookup = new ImprovedKademliaProtocol(intraDomainLookupFactory);
                } else {
                    lookup = new ImprovedKademliaProtocol(interDomainLookupFactory);
                }
                lookup.setType("naive kademlia lookup");
                break;
        }

        lookups.add(lookup);
        return lookup;
    }

}
