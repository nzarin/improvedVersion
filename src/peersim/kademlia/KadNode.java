package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;


public class KadNode implements KademliaNode {

    private BigInteger nodeId;
    private int domain;
    private RoutingTable routingTable;
    private KademliaProtocol kademliaProtocol;
    private ArrayList<BridgeNode> bridgeNodes;
    private LinkedHashMap<Long, FindOperation> findOperationsMap;
    private TreeMap<Long, Long> sentMsgTracker;


    public KadNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    public KadNode(BigInteger id, int domain, KademliaProtocol kadprotocol){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.kademliaProtocol = kadprotocol;
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
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

    @Override
    public LinkedHashMap<Long, FindOperation> getFindOperationsMap() {
        return this.findOperationsMap;
    }

    @Override
    public TreeMap<Long, Long> getSentMsgTracker() {
        return this.sentMsgTracker;
    }

    @Override
    public void setFindOperationsMap(LinkedHashMap<Long, FindOperation> findOperationsMap) {
        this.findOperationsMap = findOperationsMap;
    }

    @Override
    public void setSentMsgTracker(TreeMap<Long, Long> msgTracker) {
        this.sentMsgTracker = msgTracker;
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
