package peersim.kademlia;

import java.math.BigInteger;


public class KadNode {
    
    private BigInteger nodeId;
    private int domain;
    private RoutingTable routingTable;
    
    public KadNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable();
    }


    public void setNodeId(BigInteger nodeId) {
        this.nodeId = nodeId;
    }

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public BigInteger getNodeId() {
        return this.nodeId;
    }

    public int getDomain() {
        return this.domain;
    }

    public RoutingTable getRoutingTable(){
        return this.routingTable;
    }

    @Override
    public String toString() {
        return "KadNode{" +
                "nodeId=" + nodeId +
                ", domain=" + domain +
                ", routingTable=" + routingTable +
                '}';
    }
}
