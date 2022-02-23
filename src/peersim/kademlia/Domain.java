package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public class Domain {

    private final ArrayList<KadNode> kadNodes;
    private final ArrayList<BridgeNode> bridgeNodes;
    private BigInteger domainId;
    private ArrayList<Domain> otherDomains;

    public Domain(BigInteger id){
        this.bridgeNodes = new ArrayList<>();
        this.kadNodes = new ArrayList<>();
        this.otherDomains = new ArrayList<>();
        this.domainId = id;
    }

    public void addDomain(Domain domain){
        this.otherDomains.add(domain);
    }

    public ArrayList<Domain> getOtherDomains(){
        return this.otherDomains;
    }

    public ArrayList<BridgeNode> getBridgeNodes() {
        return bridgeNodes;
    }

    public ArrayList<KadNode> getKadNodes() {
        return kadNodes;
    }

    public BigInteger getDomainId() {
        return domainId;
    }
}
