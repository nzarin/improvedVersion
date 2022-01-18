package peersim.kademlia;

import java.math.BigInteger;
import java.util.ArrayList;

public abstract class KademliaNode {
    abstract BigInteger getNodeId();
    abstract RoutingTable getRoutingTable();
    abstract int getDomain();
    abstract ArrayList<BridgeNode> getBridgeNodes();
    abstract ArrayList<KadNode> getKadNodes();
}
