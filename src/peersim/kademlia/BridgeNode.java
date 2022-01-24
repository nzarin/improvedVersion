package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class BridgeNode implements KademliaNode {

    private final BigInteger nodeId;
    private int domain;
    private final RoutingTable routingTable;
    private final KademliaProtocol kademliaProtocol;
    private final ArrayList<KadNode> kadNodes;
    private final ArrayList<BridgeNode> bridgeNodes;
    private final boolean available;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;

    public BridgeNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol) {
        this.nodeId = id;
        this.domain = domain;
        this.kademliaProtocol = kademliaProtocol;
        this.routingTable = (new RoutingTable(this));
        this.available = true;
        this.kadNodes = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    public void addKadNode(KadNode node) {
        this.kadNodes.add(node);
    }

    public void addBridgeNode(BridgeNode node) {
        this.bridgeNodes.add(node);
    }

    //getters
    public int getDomain() {
        return this.domain;
    }

    //setters
    public void setDomain(int domain) {
        this.domain = domain;
    }

    @Override
    public ArrayList<BridgeNode> getBridgeNodes() {
        return this.bridgeNodes;
    }

    @Override
    public ArrayList<KadNode> getKadNodes() {
        return this.kadNodes;
    }

    @Override
    public LinkedHashMap<Long, FindOperation> getFindOperationsMap() {
        return this.findOperationsMap;
    }

    @Override
    public TreeMap<Long, Long> getSentMsgTracker() {
        return this.sentMsgTracker;
    }

    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    public boolean isAvailable() {
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
