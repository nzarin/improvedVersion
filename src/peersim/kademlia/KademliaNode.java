package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public interface KademliaNode {
    BigInteger getNodeId();
    RoutingTable getRoutingTable();
    int getDomain();
    ArrayList<BridgeNode> getBridgeNodes();
    ArrayList<KadNode> getKadNodes();
}
