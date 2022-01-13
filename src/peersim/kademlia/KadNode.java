package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;


public class KadNode implements KademliaNode {

    private BigInteger nodeId;
    private int networkNodeId;
    private int domain;
    private RoutingTable routingTable;
    private KademliaProtocol kademliaProtocol;
    private NodeType type;
    private ArrayList<BridgeNode> bridgeNodes;


    public KadNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
    }

    public KadNode(BigInteger id, int domain, KademliaProtocol kadprotocol){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.kademliaProtocol = kadprotocol;
    }


    public int getDomain() {return this.domain;}

    public NodeType getType(){ return this.type;};

    public RoutingTable getRoutingTable(){ return this.routingTable;}

    //setters
    public void setDomain(int domain) {
        this.domain = domain;
    }

    public void setNodeType(NodeType type){ this.type = type;}


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

    @Override
    public BigInteger getNodeId() {
        return this.nodeId;
    }
}
