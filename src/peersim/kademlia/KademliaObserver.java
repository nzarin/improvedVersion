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
    /**
     * Keep statistics of the number of hops of every message delivered.
     */
    public static IncrementalStats hopStore = new IncrementalStats();
    /**
     * Keep statistics of the time every message delivered.
     */
    public static IncrementalStats timeStore = new IncrementalStats();
    /**
     * Keep statistic of number of find operation
     */
    public static IncrementalStats find_op = new IncrementalStats();
    /**
     * Keep statistics on the number of finished lookups
     */
    public static IncrementalStats finished_lookups = new IncrementalStats();
    /**
     * Keep statistics on the number of failed lookups
     */
    public static IncrementalStats failed_lookups = new IncrementalStats();
    /**
     * Keep statistics on the number of successful lookups
     */
    public static IncrementalStats successful_lookups = new IncrementalStats();
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
        double success_lookups = successful_lookups.getSum();
        double failure_lookups = failed_lookups.getSum();
        double no_btstrp_completed_lookups = success_lookups + failure_lookups;

        double success_ratio = success_lookups / no_btstrp_completed_lookups;


        //format print result
        String s = String.format("[time=%d]:[N=%d current nodes UP] [%f min hops] [%f average hops] [%f max hops] [%d min ltcy] [%d msec average ltcy] [%d max ltcy] [%f created findops] [%f completed findops] [%f success lookups] [%f failed lookups]  [%f success ratio]",
                CommonState.getTime(), sz, hopStore.getMin(), hopStore.getAverage(), hopStore.getMax(), (int) timeStore.getMin(), (int) timeStore.getAverage(), (int) timeStore.getMax(), find_op.getSum(), no_btstrp_completed_lookups, success_lookups, failure_lookups, success_ratio);

        // create hop file
        try {
            String fileName = "results/hops/avgHops.txt";
            File f = new File(fileName);
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
            out.write(String.valueOf(hopStore.getAverage()).replace(".", ",") + ";\n");
            out.close();
        } catch (IOException e) {
        }

        // create latency file
        try {
            String fileName = "results/latency/avgLatency";
            File f = new File(fileName);
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
            out.write(String.valueOf(timeStore.getAverage()).replace(".", ",") + ";\n");
            out.close();
        } catch (IOException e) {
        }

        // create success and failure file
        try {
            String fileName = "results/successratio/avgSR";
            File f = new File(fileName);
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
            out.write(success_ratio + ";\n");
            out.close();
        } catch (IOException e) {
        }

        System.err.println(s);

        return false;
    }
}
