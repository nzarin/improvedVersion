package peersim.kademlia;

import java.math.BigInteger;
import java.util.Random;

//_________________________________________________________________________________________________

/**
 * This initializator assign to the Nodes a nodeId (stored in the protocol MSPastryProtocol) by using this 128-bit (32 byte)
 * random generator.
 *
 */
public final class UniformRandomGenerator {

    private final Random rnd;
    private final int bits;
    private final int nmr_domains;

    /**
     * initialized this random generator with the specified random seeder and the number of desider bits to generate
     *
     * @param aBits int
     * @param r     Random
     */
    public UniformRandomGenerator(int aBits, Random r) {
        bits = aBits;
        rnd = r;
        nmr_domains = KademliaCommonConfig.NUMBER_OF_DOMAINS;
    }

    /**
     * instanciate the random generator with the given seed
     *
     * @param aSeed long
     * @param aBits number of bits of the number-to-be-generated
     */
    public UniformRandomGenerator(int aBits, long aSeed) {
        this(aBits, new Random(aSeed));
    }

    /**
     * Create the random ID.
     *
     * @return
     */
    private final BigInteger nextRand() {
        return new BigInteger(bits, rnd);
    }

    /**
     * Select a random domain to assign an ID to
     * @return
     */
    public int selectDomain() {
        return rnd.nextInt(nmr_domains);
    }

    /**
     * Returns a unique x-bit random number. The number is also put into an internal store to check it will be never returned
     * again.
     *
     * @return BigInteger
     */
    public final BigInteger generateID() {
        return nextRand();
    }


}