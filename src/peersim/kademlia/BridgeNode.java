package peersim.kademlia;

public class BridgeNode {

    private Lookup lookup;

    public BridgeNode(){
        lookup = new InterDomainLookup();
    }


}
