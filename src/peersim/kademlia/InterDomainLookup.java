package peersim.kademlia;

public class InterDomainLookup implements Lookup {


    public InterDomainLookup() {
        System.err.println("This is a inter-domain request.. Do nothing for now");
    }

    @Override
    public void find() {

        //1: send to bridge node of this domain
        //2: bridge node searches for the correct other bridge node from the receiver domain
        //3: bridge node select a random node to initiate the lookup
        //4: intra-domain lookup
        //5: return closest set to bridge node
        //6: bridge node returns closest set to other bridge node
        //7: other bridge node sends closest set to initiator node

    }

    @Override
    public void respond() {

    }

    @Override
    public void handleResponse() {

    }
}
