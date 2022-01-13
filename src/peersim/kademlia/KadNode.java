package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;


public class KadNode implements KademliaNode {

    private BigInteger nodeId;
    private int networkNodeId;
    private int domain;
    private RoutingTable routingTable;
    private KademliaProtocol kademliaProtocol;
    private ArrayList<BridgeNode> bridgeNodes;


    public KadNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
    }

    public KadNode(BigInteger id, int domain, KademliaProtocol kadprotocol){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.kademliaProtocol = kadprotocol;
        this.bridgeNodes = new ArrayList<>();

    }

    public void addBridgeNode(BridgeNode node){
        this.bridgeNodes.add(node);
    }

    public int getDomain() {return this.domain;}

    public RoutingTable getRoutingTable(){ return this.routingTable;}

    public ArrayList<BridgeNode> getBridgeNodes(){ return this.bridgeNodes;}

    @Override
    public ArrayList<KadNode> getKadNodes() {
        return null;
    }

    //setters
    public void setDomain(int domain) {
        this.domain = domain;
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

    @Override
    public BigInteger getNodeId() {
        return this.nodeId;
    }
}
