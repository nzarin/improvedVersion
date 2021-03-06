package peersim.kademlia;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * Some utility and mathematical function to work with BigInteger numbers and strings.
 */
public class Util {

    /**
     * Given two numbers, returns the length of the common prefix, i.e. how many digits (in base 2) have in common from the
     * leftmost side of the number
     *
     * @param b1 BigInteger
     * @param b2 BigInteger
     * @return int
     */
    public static int prefixLen(BigInteger b1, BigInteger b2) {

        String s1 = Util.put0(b1);
        String s2 = Util.put0(b2);

        int i;
        for (i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i))
                return i;
        }

        return i;
    }

    /**
     * Computes the XOR distance between two number wich is defined as (a XOR b)
     *
     * @param a BigInteger
     * @param b BigInteger
     * @return BigInteger
     */
    public static BigInteger distance(BigInteger a, BigInteger b) {
        return a.xor(b);
    }

    /**
     * Convert a BigInteger into a String (base 2) and lead all needed non-significative zeroes in order to reach the canonical
     * length of a nodeid
     *
     * @param b BigInteger
     * @return String
     */
    public static String put0(BigInteger b) {
        if (b == null)
            return null;
        String s = b.toString(2); // base 2
        while (s.length() < KademliaCommonConfig.BITS) {
            s = "0" + s;
        }
        return s;
    }

    /**
     * Search through the network the Node having a specific node Id, by performing binary search (we concern about the ordering
     * of the network).
     *
     * @param searchNodeId BigInteger
     * @return Node
     */
    public static Node nodeIdtoNode(BigInteger searchNodeId, int kademliaid) {
        if (searchNodeId == null)
            return null;

        int inf = 0;
        int sup = Network.size() - 1;
        int m;

        while (inf <= sup) {
            m = (inf + sup) / 2;

            KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(m).getProtocol(kademliaid);
            BigInteger mId = kademliaProtocol.getCurrentNode().getNodeId();

            if (mId.equals(searchNodeId))
                return Network.get(m);

            if (mId.compareTo(searchNodeId) < 0)
                inf = m + 1;
            else
                sup = m - 1;
        }

        // perform a traditional search for more reliability (maybe the network is not ordered)
        BigInteger mId;
        for (int i = Network.size() - 1; i >= 0; i--) {
            KademliaProtocol kademliaProtocol = (KademliaProtocol) Network.get(i).getProtocol(kademliaid);
            mId = kademliaProtocol.getCurrentNode().getNodeId();
            if (mId.equals(searchNodeId))
                return Network.get(i);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static void sortNet(int kademliaid) {
        Network.sort(new Comparator() {
            // ______________________________________________________________________________________
            public int compare(Object o1, Object o2) {
                Node n1 = (Node) o1;
                Node n2 = (Node) o2;
                KademliaProtocol p1 = (KademliaProtocol) (n1.getProtocol(kademliaid));
                KademliaProtocol p2 = (KademliaProtocol) (n2.getProtocol(kademliaid));
                return Util.put0(p1.getCurrentNode().getNodeId()).compareTo(Util.put0(p2.getCurrentNode().getNodeId()));
            }

            public boolean equals(Object obj) {
                return compare(this, obj) == 0;
            }
        });
    }


}
