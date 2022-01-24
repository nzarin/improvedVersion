package peersim.kademlia;

/**
 * Fixed Parameters of a kademlia network. They have a default value and can be configured at startup of the network, once only.
 */
public class KademliaCommonConfig {

	/**
	 * Length of identifiers (default is 160).
	 */
	public static int BITS = 5;

    /**
     * Number of simultaneous lookup, so degree of parallelism (default is 3).
     */
    public static int ALPHA = 3;

    /**
     * Number of returned contacts (default is 2).
     */
    public static int BETA = 2;

    /**
     * Dimension of k-buckets (default is 20)
     */
    public static int K = 20;

    /**
     * Number of domains (overlays)
     */
    public static int NUMBER_OF_DOMAINS = 1;

    /**
     * Number of bridge nodes per domain.
     */
    public static int NUMBER_OF_BRIDGES_PER_DOMAIN = 2;

    /**
     * The version of the protocol: either naive or improved
     */
    public static int NAIVE_KADEMLIA_PROTOCOL = 1;

    /**
     * short information about current kademlia configuration
     *
     * @return String
     */
    public static String info() {
        return String.format("[K=%d][ALPHA=%d][BETA=%d][BITS=%d]", K, ALPHA, BETA, BITS);
    }

}
