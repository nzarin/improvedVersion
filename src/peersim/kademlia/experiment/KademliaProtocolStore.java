package peersim.kademlia.experiment;

public class KademliaProtocolStore extends DHTProtocolStore{


    @Override
    Lookup createLookup(String type) {
        Lookup lookup = null;

        // the factory determines what type of operations we want
//        LookupIngredientFactory2 lookupIngredientFactory2 = new KademliaIngredientFactory();
        LookupIngredientFactory2 interdomainFactory = new InterDomainKademliaFactory();
        LookupIngredientFactory2 intraDomainLookupFactory = new IntraDomainKademliaFactory();

        switch(type){
            case "naive":
                lookup = new NaiveKademliaLookup(intraDomainLookupFactory);
                lookup.setType("naive kademlia lookup");
                System.err.println("it is a naive lookup we just created for kademlia protocol in kademlia store");
                break;
            case "improved":
                lookup = new ImprovedKademliaProtocol(intraDomainLookupFactory);
                lookup.setType("improved kademlia lookup");
                System.err.println("it is a improved lookup we just created for kademlia protocol in kademlia store");
                break;
        }

        return lookup;
    }
}
