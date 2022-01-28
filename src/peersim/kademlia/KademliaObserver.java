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
        String s = String.format("[time=%d]:[N=%d current nodes UP] [%f min hops] [%f average hops] [%f max hops] [%d min ltcy] [%d msec average ltcy] [%d max ltcy] [%f created findops] [%f completed findops] [%f success lookups] [%f failed lookups]  [%f success ratio] [%f shortest amount of hops] [%f INTRA-DOMAIN lookups] [%f INTER-DOMAIN lookups]",
                CommonState.getTime(), sz, hopStore_OVERALL.getMin(), hopStore_OVERALL.getAverage(), hopStore_OVERALL.getMax(), (int) timeStore_OVERALL.getMin(), (int) timeStore_OVERALL.getAverage(), (int) timeStore_OVERALL.getMax(), find_op_OVERALL.getSum(), no_btstrp_completed_lookups, success_lookups, failure_lookups, success_ratio, shortestAmountHops_OVERALL.getAverage(), find_op_INTRA.getSum(), find_op_INTER.getSum());

        // create files
        try {

            //create OVERALL files
            File hop_file_OVERALL = new File("results/hops/avgHops-OVERALL.txt");
            hop_file_OVERALL.createNewFile();
            BufferedWriter outH_OVERALL = new BufferedWriter(new FileWriter(hop_file_OVERALL, true));
            outH_OVERALL.write(hopStore_OVERALL.getAverage() + "\n");
            outH_OVERALL.close();

            File latency_file_OVERALL = new File("results/latency/avgLatency-OVERALL.txt");
            latency_file_OVERALL.createNewFile();
            BufferedWriter outL_OVERALL = new BufferedWriter(new FileWriter(latency_file_OVERALL, true));
            outL_OVERALL.write(timeStore_OVERALL.getAverage() + "\n");
            outL_OVERALL.close();

            File success_file_OVERALL = new File("results/successratio/avgSR-OVERALL.txt");
            success_file_OVERALL.createNewFile();
            BufferedWriter outS_OVERALL = new BufferedWriter(new FileWriter(success_file_OVERALL, true));
            outS_OVERALL.write(success_ratio + "\n");
            outS_OVERALL.close();

            File message_file_OVERALL = new File("results/messages/avgMessages-OVERALL.txt");
            message_file_OVERALL.createNewFile();
            BufferedWriter outM_OVERALL = new BufferedWriter(new FileWriter(message_file_OVERALL, true));
            outM_OVERALL.write(messageStore_OVERALL.getAverage() + "\n");
            outM_OVERALL.close();

            //create INTRA-DOMAIN LOOKUP files
            File hop_file_INTRA = new File("results/hops/avgHops-INTRA.txt");
            hop_file_INTRA.createNewFile();
            BufferedWriter outH_INTRA = new BufferedWriter(new FileWriter(hop_file_INTRA, true));
            outH_INTRA.write(hopStore_INTRA.getAverage() + "\n");
            outH_INTRA.close();

            File latency_file_INTRA = new File("results/latency/avgLatency-INTRA.txt");
            latency_file_INTRA.createNewFile();
            BufferedWriter outL_INTRA = new BufferedWriter(new FileWriter(latency_file_INTRA, true));
            outL_INTRA.write(timeStore_INTRA.getAverage() + "\n");
            outL_INTRA.close();

            File success_file_INTRA = new File("results/successratio/avgSR-INTRA.txt");
            success_file_INTRA.createNewFile();
            BufferedWriter outS_INTRA = new BufferedWriter(new FileWriter(success_file_INTRA, true));
            outS_INTRA.write(success_ratio + "\n");
            outS_INTRA.close();

            File message_file_INTRA = new File("results/messages/avgMessages-INTRA.txt");
            message_file_INTRA.createNewFile();
            BufferedWriter outM_INTRA = new BufferedWriter(new FileWriter(message_file_INTRA, true));
            outM_INTRA.write(messageStore_INTRA.getAverage() + "\n");
            outM_INTRA.close();


            //create INTER-DOMAIN LOOKUP files
            File hop_file_INTER = new File("results/hops/avgHops-INTER.txt");
            hop_file_INTER.createNewFile();
            BufferedWriter outH_INTER= new BufferedWriter(new FileWriter(hop_file_INTER, true));
            outH_INTER.write(hopStore_INTER.getAverage() + "\n");
            outH_INTER.close();

            File latency_file_INTER = new File("results/latency/avgLatency-INTER.txt");
            latency_file_INTER.createNewFile();
            BufferedWriter outL_INTER = new BufferedWriter(new FileWriter(latency_file_INTER, true));
            outL_INTER.write(timeStore_INTER.getAverage() + "\n");
            outL_INTER.close();

            File success_file_INTER = new File("results/successratio/avgSR-INTER.txt");
            success_file_INTER.createNewFile();
            BufferedWriter outS_INTER = new BufferedWriter(new FileWriter(success_file_INTER, true));
            outS_INTER.write(success_ratio + "\n");
            outS_INTER.close();

            File message_file_INTER = new File("results/messages/avgMessages-INTER.txt");
            message_file_INTER.createNewFile();
            BufferedWriter outM_INTER = new BufferedWriter(new FileWriter(message_file_INTER, true));
            outM_INTER.write(messageStore_INTER.getAverage() + "\n");
            outM_INTER.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println();
        System.err.println(s);

        return false;
    }
}
