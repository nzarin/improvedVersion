package peersim.kademlia;

import java.math.BigInteger;


public class KadNode {

    private Lookup lookup;
    private BigInteger nodeId;
    private int networkNodeId;
    private int domain;
    private RoutingTable routingTable;
    private NodeType type;


    public KadNode(){ lookup = new IntraDomainLookup(); }

    public KadNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
    }

    // getters
    public BigInteger getNodeId() {
        return this.nodeId;
    }

    public int getDomain() {return this.domain;}

    public NodeType getType(){ return this.type;};

    public RoutingTable getRoutingTable(){ return this.routingTable;}

    //setters
    public void setDomain(int domain) {
        this.domain = domain;
    }

    public void setNodeType(NodeType type){ this.type = type;}

    public KadNode getKadNodeByNetworkNode(int networkNodeID){
        return this;
    }

    //printers
    @Override
    public String toString() {
        return "KadNode{" +
                "nodeId=" + nodeId +
                ", domain=" + domain +
                ", routingTable=" + routingTable.toString() +
                '}';
    }


    public String toString2() {
        return "KadNode{" +
                "nodeId=" + nodeId +
                ", domain=" + domain +
                '}';
    }
}
