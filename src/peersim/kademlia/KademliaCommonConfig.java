package peersim.kademlia;

/**
 * Fixed Parameters of a kademlia network. They have a default value and can be configured at startup of the network, once only.
 */
public class KademliaCommonConfig {

	/**
	 * Length of identifiers (default is 160).
	 */
	public static int BITS = 6;

	/**
	 * Number of simultaneous lookup, so degree of parallelism (default is 3).
	 */
	public static int ALPHA = 3;

	/**
	 *  Number of returned contacts (default is 2).
	 */
	public static int BETA = 2;

	/**
	 * Dimension of k-buckets (default is 20)
	 */
	public static int K = 4; //

	/**
	 * short information about current kademlia configuration
	 * 
	 * @return String
	 */
	public static String info() {
		return String.format("[K=%d][ALPHA=%d][BETA=%d][BITS=%d]", K, ALPHA, BETA, BITS);
	}

}
