package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public class Domain {

    private final ArrayList<KadNode> kadNodes;
    private final ArrayList<BridgeNode> bridgeNodes;
    private BigInteger domainId;

    public Domain(BigInteger id){
        this.bridgeNodes = new ArrayList<>();
        this.kadNodes = new ArrayList<>();
        this.domainId = id;
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
