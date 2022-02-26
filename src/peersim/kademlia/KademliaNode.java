package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface KademliaNode {
    Role getRole();
    BigInteger getNodeId();
    RoutingTable getRoutingTable();
    Domain getDomain();
    ArrayList<BridgeNode> getBridgeNodes();
    ArrayList<KadNode> getKadNodes();
    LinkedHashMap<Long, FindOperation> getFindOperationsMap();
    TreeMap<Long, Long> getSentMsgTracker();
    String getType();
    boolean isMalicious();
    boolean isAlive();

}
