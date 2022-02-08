package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class AdversarialNode implements KademliaNode {

    KademliaProtocol kademliaProtocol;
    private final BigInteger nodeId;
    private final RoutingTable routingTable;
    private final ArrayList<BridgeNode> bridgeNodes;
    private int domain;
    private final LinkedHashMap<Long, FindOperation> findOperationsMap;
    private final TreeMap<Long, Long> sentMsgTracker;

    public AdversarialNode(BigInteger id, int domain){
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    public AdversarialNode(BigInteger id, int domain, KademliaProtocol kademliaProtocol){
        this.kademliaProtocol = kademliaProtocol;
        this.nodeId = id;
        this.domain = domain;
        this.routingTable = new RoutingTable(this);
        this.bridgeNodes = new ArrayList<>();
        this.findOperationsMap = new LinkedHashMap<>();
        this.sentMsgTracker = new TreeMap<>();
    }

    @Override
    public BigInteger getNodeId() {
        return this.nodeId;
    }

    @Override
    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    @Override
    public int getDomain() {
        return this.domain;
    }

    @Override
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
        return "AdversarialNode";
    }
}
