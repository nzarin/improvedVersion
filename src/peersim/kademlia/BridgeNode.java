package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public class BridgeNode implements  KademliaNode{

    private BigInteger nodeId;
    private int domain;
    private RoutingTable routingTable;
    private KademliaProtocol kademliaProtocol;
    private ArrayList<KadNode> kadNodes;

    public BridgeNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol){
        this.nodeId = id;
        this.domain = domain;
        this.kademliaProtocol = kademliaProtocol;
        this.routingTable = (new RoutingTable(this));
    }


    public int getDomain() {return this.domain;}


    public RoutingTable getRoutingTable(){ return this.routingTable;}

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
