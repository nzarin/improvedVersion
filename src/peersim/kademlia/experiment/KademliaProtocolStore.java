package peersim.kademlia.experiment;

import peersim.kademlia.KademliaNode;

public class KademliaProtocolStore extends DHTProtocolStore{


    @Override
    Lookup createLookup(String type, KademliaNode s, KademliaNode r) {

        Lookup lookup = null;

        //determine what type of lookup we have and create the correct factory
        LookupIngredientFactory2 interDomainLookupFactory = new InterDomainKademliaFactory();
        LookupIngredientFactory2 intraDomainLookupFactory = new IntraDomainKademliaFactory();

        switch(type){
            case "naive":
                System.err.println("sender: " + s.getNodeId());
                System.err.println("receiver:" + r.getNodeId());
                if(s.getDomain() == r.getDomain()){
                    lookup = new NaiveKademliaLookup(intraDomainLookupFactory);
                    System.err.println("it is a naive lookup we just created for intra-domain lookup in kademlia store");
                } else {
                    lookup = new NaiveKademliaLookup(interDomainLookupFactory);
                    System.err.println("it is a naive lookup we just created for inter-domain lookup in kademlia store");
                }
                lookup.setType("naive kademlia lookup");
                break;
            case "improved":
                if(s.getDomain() == r.getDomain()){
                    lookup = new ImprovedKademliaProtocol(intraDomainLookupFactory);
                    System.err.println("it is the improved lookup we just created for intra-domain lookup in kademlia store");
                } else {
                    lookup = new ImprovedKademliaProtocol(interDomainLookupFactory);
                    System.err.println("it is the improved lookup we just created for inter-domain lookup in kademlia store");
                }
                lookup.setType("naive kademlia lookup");
                break;
        }

        return lookup;
    }
}
