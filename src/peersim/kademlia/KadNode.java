package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * This class represents a normal (traditional) KadNode
 */
public class KadNode implements KademliaNode {
    private KademliaProtocol kademliaProtocol;
    private final BigInteger nodeId;
    private final RoutingTable routingTable;
    private final ArrayList<BridgeNode> bridgeNodes;
    private final ArrayList<KadNode> colluders;
    private int domain;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;
    private boolean malicious;

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
        this.colluders = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
        this.malicious = false;
    }

    /**
     * Constructs the KadNode
     *
     * @param id
     * @param domain
     * @param kademliaProtocol
     */
    public KadNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol) {
        this.kademliaProtocol = kademliaProtocol;
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.colluders = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
        this.malicious = false;
    }

    //SETTERS

    public void setDomain(int domain) {
        this.domain = domain;
    }

    public void makeMalicious(){ this.malicious = true;}

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
    public ArrayList<KadNode> getKadNodes() {return null;}

    public ArrayList<KadNode> getColluders() {return this.colluders;}

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

    @Override
    public boolean isMalicious() {
        return this.malicious;
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
