package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * This class represents a normal (traditional) KadNode
 */
public class KadNode implements KademliaNode {

    private final BigInteger nodeId;
    private final RoutingTable routingTable;
    private final ArrayList<BridgeNode> bridgeNodes;
    private int domain;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;

    /**
     * Constructs the KadNode
     *
     * @param id
     * @param domain
     */
    public KadNode(BigInteger id, int domain) {
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    /**
     * Constructs the KadNode
     *
     * @param id
     * @param domain
     * @param kademliaProtocol
     */
    public KadNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol) {
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    //SETTERS

    public void setDomain(int domain) {
        this.domain = domain;
    }

    // GETTERS

    public int getDomain() {
        return this.domain;
    }

    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    public ArrayList<BridgeNode> getBridgeNodes() {
        return this.bridgeNodes;
    }

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
    public String getType() {
        return "KadNode";
    }


    // PRINTERS

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


    public String toString3(){
        return "nodeId=" + nodeId + ", ";
    }

    @Override
    public BigInteger getNodeId() {
        return this.nodeId;
    }
}
