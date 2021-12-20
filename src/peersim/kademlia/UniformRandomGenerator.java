package peersim.kademlia;

import java.math.BigInteger;
import java.util.Random;

//_________________________________________________________________________________________________
/**
 * This initializator assign to the Nodes a nodeId (stored in the protocol MSPastryProtocol) by using this 128-bit (32 byte)
 * random generator.
 * 
 * <b>Warning:</b> this implementation is not serialized and is not thread-safe
 * <p>
 * Title: MSPASTRY
 * </p>
 * 
 * <p>
 * Description: MsPastry implementation for PeerSim
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: The Pastry Group
 * </p>
 * 
 * @author Elisa Bisoffi, Manuel Cortella
 * @version 1.0
 */
public final class UniformRandomGenerator {

	private final Random rnd;
	private final int bits;

	/**
	 * Create the random ID.
	 * @return
	 */
	private final BigInteger nextRand() {
		return new BigInteger(bits, rnd);
	}

	/**
	 * initialized this random generator with the specified random seeder and the number of desider bits to generate
	 * 
	 * @param aBits
	 *            int
	 * @param r
	 *            Random
	 */
	public UniformRandomGenerator(int aBits, Random r) {
		bits = aBits;
		rnd = r;
	}

	/**
	 * instanciate the random generator with the given seed
	 * 
	 * @param aSeed
	 *            long
	 * @param aBits
	 *            number of bits of the number-to-be-generated
	 */
	public UniformRandomGenerator(int aBits, long aSeed) {
		this(aBits, new Random(aSeed));
	}

	/**
	 * Returns a unique x-bit random number. The number is also put into an internal store to check it will be never returned
	 * again.
	 * 
	 * @return BigInteger
	 */
	public final BigInteger generate() {
		return nextRand();
	}

}