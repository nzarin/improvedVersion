package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface KademliaNode {
    BigInteger getNodeId();
    RoutingTable getRoutingTable();
    int getDomain();
    ArrayList<BridgeNode> getBridgeNodes();
    ArrayList<KadNode> getKadNodes();
    LinkedHashMap<Long, FindOperation> getFindOperationsMap();
    TreeMap<Long, Long> getSentMsgTracker();


}
