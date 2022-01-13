package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public class BridgeNode implements  KademliaNode{

    private BigInteger nodeId;
    private int domain;
    private RoutingTable routingTable;
    private KademliaProtocol kademliaProtocol;
    private ArrayList<KadNode> kadNodes;
    private ArrayList<BridgeNode> bridgeNodes;
    private boolean available;

    public BridgeNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol){
        this.nodeId = id;
        this.domain = domain;
        this.kademliaProtocol = kademliaProtocol;
        this.routingTable = (new RoutingTable(this));
        this.available = true;
        this.kadNodes = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
    }

    public void addKadNode(KadNode node){
        this.kadNodes.add(node);
    }

    public void addBridgeNode(BridgeNode node){
        this.bridgeNodes.add(node);
    }

    //getters
    public int getDomain() {return this.domain;}

    @Override
    public ArrayList<BridgeNode> getBridgeNodes() {
        return this.bridgeNodes;
    }

    @Override
    public ArrayList<KadNode> getKadNodes() {
        return this.kadNodes;
    }

    public RoutingTable getRoutingTable(){ return this.routingTable;}

    //setters
    public void setDomain(int domain) {
        this.domain = domain;
    }

    public void makeBridgeNodeUnavailable(){
        this.available = false;
    }

    public boolean isAvailable(){
        return this.available;
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
