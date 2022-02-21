package peersim.kademlia;

import peersim.core.Node;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class BridgeNode implements KademliaNode {

    private KademliaProtocol kademliaProtocol;
    private final BigInteger nodeId;
    private final RoutingTable routingTable;
    private final ArrayList<KadNode> kadNodes;
    private final ArrayList<BridgeNode> bridgeNodes;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;
    private int domain;

    public BridgeNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol) {
        this.kademliaProtocol = kademliaProtocol;
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = (new RoutingTable(this));
        this.kadNodes = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
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

    @Override
    public String getType() {
        return "BridgeNode";
    }

    @Override
    public boolean isMalicious() { return false;}

    @Override
    public boolean isAlive() {
        Node n = Util.nodeIdtoNode(nodeId, kademliaProtocol.getKademliaId());
        return n.isUp();
    }

    public RoutingTable getRoutingTable() {
        return this.routingTable;
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
