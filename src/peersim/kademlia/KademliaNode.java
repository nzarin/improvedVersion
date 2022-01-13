package peersim.kademlia;

import java.math.BigInteger;

public interface KademliaNode {
    BigInteger getNodeId();
    RoutingTable getRoutingTable();
    int getDomain();
}
