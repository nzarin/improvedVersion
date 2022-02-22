package peersim.kademlia;

import peersim.core.Node;

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
    private Domain domain;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;
    private boolean malicious;
    private Role role;

    public KadNode(BigInteger id){
        this.nodeId = id;
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
     */
    public KadNode(BigInteger id, Domain domain) {
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
    public KadNode(BigInteger id, Domain domain, KademliaProtocol kademliaProtocol, Role role) {
        this.kademliaProtocol = kademliaProtocol;
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.colluders = new ArrayList<>();
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
        this.malicious = false;
        this.role = role;
    }

    //SETTERS

    public void setDomain(Domain domain) { this.domain = domain;}

    public void makeMalicious(){ this.malicious = true;}

    public void setRole(Role role){this.role = role;}

    // GETTERS

    public Role getRole(){ return this.role;}

    public Domain getDomain() {
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
                ", has target in routing table= " + hasNodeInRoutingTable(this) +
                '}';
    }


    public String toString3(){
        return "nodeId=" + nodeId + ", ";
    }

    @Override
    public BigInteger getNodeId() {
        return this.nodeId;
    }

    public boolean isAlive(){
        Node n = Util.nodeIdtoNode(nodeId, kademliaProtocol.getKademliaId());
        return n.isUp();
    }

    public boolean hasNodeInRoutingTable(KadNode node){
        //iterate over routing table
        int index = Util.prefixLen(this.nodeId, node.getNodeId());
        KBucket targetBucket = routingTable.getKBucket(index);
        if(targetBucket.getKadNode(node) != null){
            return true;
        }
        return false;
    }

}
