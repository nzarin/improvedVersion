package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class implements a simple observer of search time and hop average in finding a node in the network
 */
public class KademliaObserver implements Control {

    /**
     * Parameter of the protocol we want to observe
     */
    private static final String PAR_PROT = "protocol";

    // OVERALL statistics
    public static IncrementalStats find_op_OVERALL = new IncrementalStats();
    public static IncrementalStats hopStore_OVERALL = new IncrementalStats();
    public static IncrementalStats messageStore_OVERALL = new IncrementalStats();
    public static IncrementalStats timeStore_OVERALL = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_OVERALL = new IncrementalStats();
    public static IncrementalStats finished_lookups_OVERALL = new IncrementalStats();
    public static IncrementalStats failed_lookups_OVERALL = new IncrementalStats();
    public static IncrementalStats successful_lookups_OVERALL = new IncrementalStats();

    // INTRA-DOMAIN LOOKUP statistics
    public static IncrementalStats hopStore_INTRA = new IncrementalStats();
    public static IncrementalStats messageStore_INTRA = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTRA = new IncrementalStats();
    public static IncrementalStats timeStore_INTRA = new IncrementalStats();
    public static IncrementalStats find_op_INTRA = new IncrementalStats();
    public static IncrementalStats finished_lookups_INTRA = new IncrementalStats();
    public static IncrementalStats failed_lookups_INTRA = new IncrementalStats();
    public static IncrementalStats successful_lookups_INTRA = new IncrementalStats();

    // INTER-DOMAIN LOOKUP statistics
    public static IncrementalStats hopStore_INTER = new IncrementalStats();
    public static IncrementalStats messageStore_INTER = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTER = new IncrementalStats();
    public static IncrementalStats timeStore_INTER = new IncrementalStats();
    public static IncrementalStats find_op_INTER = new IncrementalStats();
    public static IncrementalStats finished_lookups_INTER = new IncrementalStats();
    public static IncrementalStats failed_lookups_INTER = new IncrementalStats();
    public static IncrementalStats successful_lookups_INTER = new IncrementalStats();

    /**
     * Protocol id
     */
    private final int pid;

    /**
     * Prefix to be printed in output
     */
    private final String prefix;

    /**
     * Constructor that links the Control and Protocol objects.
     *
     * @param prefix
     */
    public KademliaObserver(String prefix) {
        this.prefix = prefix;
        pid = Configuration.getPid(prefix + "." + PAR_PROT);
    }

    /**
     * Print the statistical snapshot of the current situation
     *
     * @return boolean always false
     */
    public boolean execute() {
        // get the real network size
        int sz = Network.size();

        //update the network size such that it contains only reachable nodes
        for (int i = 0; i < Network.size(); i++)
            if (!Network.get(i).isUp())
                sz--;

        // calculate success ratio
        double success_lookups = successful_lookups_OVERALL.getSum();
        double failure_lookups = failed_lookups_OVERALL.getSum();
        double no_btstrp_completed_lookups = success_lookups + failure_lookups;
        double success_ratio = success_lookups / no_btstrp_completed_lookups;


        //format print result
        String s = String.format("[time=%d]:[N=%d current nodes UP] [%f min hops] [%f average hops] [%f max hops] [%d min ltcy] [%d msec average ltcy] [%d max ltcy] [%f created findops] [%f completed findops] [%f success lookups] [%f failed lookups]  [%f success ratio] [%f shortest amount of hops]",
                CommonState.getTime(), sz, hopStore_OVERALL.getMin(), hopStore_OVERALL.getAverage(), hopStore_OVERALL.getMax(), (int) timeStore_OVERALL.getMin(), (int) timeStore_OVERALL.getAverage(), (int) timeStore_OVERALL.getMax(), find_op_OVERALL.getSum(), no_btstrp_completed_lookups, success_lookups, failure_lookups, success_ratio, shortestAmountHops_OVERALL.getAverage());

        // create files
        try {
            File hopFile = new File("results/hops/avgHops.txt");
            File latencyFile = new File("results/latency/avgLatency.txt");
            File succesFile = new File("results/sucessratio/avgSR.txt");
            File messagefile = new File("results/messages/avgMessages.txt");
            hopFile.createNewFile();
            latencyFile.createNewFile();
            succesFile.createNewFile();
            messagefile.createNewFile();
            BufferedWriter outH = new BufferedWriter(new FileWriter(hopFile, true));
            BufferedWriter outL = new BufferedWriter(new FileWriter(latencyFile, true));
            BufferedWriter outS = new BufferedWriter(new FileWriter(succesFile, true));
            BufferedWriter outM = new BufferedWriter(new FileWriter(messagefile, true));

            outH.write(String.valueOf(hopStore_OVERALL.getAverage()).replace(".", ",") + ";\n");
            outL.write(String.valueOf(timeStore_OVERALL.getAverage()).replace(".", ",") + ";\n");
            outS.write(success_ratio + ";\n");
            outM.write(String.valueOf(messageStore_OVERALL.getAverage()).replace(".", ",") + ";\n");

            outH.close();
            outL.close();
            outS.close();
            outM.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println();
        System.err.println(s);

        return false;
    }
}
