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

    // general intra-domain lookup statistics
    public static IncrementalStats messageStore_INTRA = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTRA = new IncrementalStats();
    public static IncrementalStats timeStore_INTRA = new IncrementalStats();
    public static IncrementalStats finished_lookups_INTRA = new IncrementalStats();
    public static IncrementalStats failed_lookups_INTRA = new IncrementalStats();
    public static IncrementalStats successful_lookups_INTRA = new IncrementalStats();
    public static IncrementalStats fraction_f_CS_INTRA = new IncrementalStats();

    // general inter-domain lookup statistics
    public static IncrementalStats messageStore_INTER = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTER = new IncrementalStats();
    public static IncrementalStats timeStore_INTER = new IncrementalStats();
    public static IncrementalStats finished_lookups_INTER = new IncrementalStats();
    public static IncrementalStats failed_lookups_INTER = new IncrementalStats();
    public static IncrementalStats successful_lookups_INTER = new IncrementalStats();
    public static IncrementalStats fraction_f_CS_INTER = new IncrementalStats();

    // successful intra-domain lookup statistics
    public static IncrementalStats messageStore_INTRA_SUCCESS = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTRA_SUCCESS = new IncrementalStats();
    public static IncrementalStats timeStore_INTRA_SUCCESS = new IncrementalStats();
    public static IncrementalStats fraction_f_INTRA_SUCCESS = new IncrementalStats();

    // SUCCESSFUL inter-domain lookup statistics
    public static IncrementalStats messageStore_INTER_SUCCESS = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTER_SUCCESS = new IncrementalStats();
    public static IncrementalStats timeStore_INTER_SUCCESS = new IncrementalStats();
    public static IncrementalStats fraction_f_INTER_SUCCESS = new IncrementalStats();

    // FAILED intra-domain lookup statistics
    public static IncrementalStats messageStore_INTRA_FAILURE = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTRA_FAILURE = new IncrementalStats();
    public static IncrementalStats timeStore_INTRA_FAILURE = new IncrementalStats();
    public static IncrementalStats fraction_f_INTRA_FAILURE = new IncrementalStats();

    // FAILED inter-domain lookup statistics
    public static IncrementalStats messageStore_INTER_FAILURE = new IncrementalStats();
    public static IncrementalStats shortestAmountHops_INTER_FAILURE = new IncrementalStats();
    public static IncrementalStats timeStore_INTER_FAILURE = new IncrementalStats();
    public static IncrementalStats fraction_f_INTER_FAILURE = new IncrementalStats();


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


        //calculate intra-domain success ratio
        double success_lookups_intra = successful_lookups_INTRA.getSum();
        double failure_lookups_intra = failed_lookups_INTRA.getSum();
        double completed_lookups_intra = success_lookups_intra + failure_lookups_intra;
        double success_ratio_intra = success_lookups_intra / completed_lookups_intra;

        //calculate inter-domain success ratio
        double success_lookups_inter = successful_lookups_INTER.getSum();
        double failure_lookups_inter = failed_lookups_INTER.getSum();
        double completed_lookups_inter = success_lookups_inter + failure_lookups_inter;
        double success_ratio_inter = success_lookups_inter / completed_lookups_inter;

        double completed_findoperations = finished_lookups_INTER.getSum() + finished_lookups_INTRA.getSum();

        //format print result
        String s = String.format("[time=%d]:[N=%d current nodes UP]  [%f completed findops] [%f success lookups TOTAL] [%f failed lookups TOTAL]  [%f shortest amount of hops INTRA] [%f shortest amount of hops INTER] [%f INTRA-DOMAIN lookups] [%f INTER-DOMAIN lookups]",
                CommonState.getTime(), sz, completed_findoperations, success_lookups_inter + success_lookups_intra, failure_lookups_inter + failure_lookups_intra, shortestAmountHops_INTRA.getAverage(), shortestAmountHops_INTER.getAverage(), finished_lookups_INTRA.getSum(), finished_lookups_INTER.getSum());

        // create files
        try {

            //create INTRA-DOMAIN LOOKUP files
            File hop_file_INTRA = new File("results/intradomain/hops.txt");
            hop_file_INTRA.createNewFile();
            BufferedWriter outH_INTRA = new BufferedWriter(new FileWriter(hop_file_INTRA, false));
            outH_INTRA.write(shortestAmountHops_INTRA.toString() + "\n");
            outH_INTRA.close();

            File latency_file_INTRA = new File("results/intradomain/latency.txt");
            latency_file_INTRA.createNewFile();
            BufferedWriter outL_INTRA = new BufferedWriter(new FileWriter(latency_file_INTRA, false));
            outL_INTRA.write(timeStore_INTRA.toString() + "\n");
            outL_INTRA.close();

            File success_file_INTRA = new File("results/intradomain/success-rate.txt");
            success_file_INTRA.createNewFile();
            BufferedWriter outS_INTRA = new BufferedWriter(new FileWriter(success_file_INTRA, false));
            outS_INTRA.write(success_ratio_intra + "\n");
            outS_INTRA.close();

            File message_file_INTRA = new File("results/intradomain/messages.txt");
            message_file_INTRA.createNewFile();
            BufferedWriter outM_INTRA = new BufferedWriter(new FileWriter(message_file_INTRA, false));
            outM_INTRA.write(messageStore_INTRA.toString() + "\n");
            outM_INTRA.close();

            File adversarial_file_INTRA = new File("results/intradomain/fraction-f.txt");
            adversarial_file_INTRA.createNewFile();
            BufferedWriter outA_INTRA = new BufferedWriter(new FileWriter(adversarial_file_INTRA, false));
            outA_INTRA.write(fraction_f_CS_INTRA.toString() + "\n");
            outA_INTRA.close();

            //create INTER-DOMAIN LOOKUP files
            File hop_file_INTER = new File("results/interdomain/hops.txt");
            hop_file_INTER.createNewFile();
            BufferedWriter outH_INTER= new BufferedWriter(new FileWriter(hop_file_INTER, false));
            outH_INTER.write(shortestAmountHops_INTER.toString() + "\n");
            outH_INTER.close();

            File latency_file_INTER = new File("results/interdomain/latency.txt");
            latency_file_INTER.createNewFile();
            BufferedWriter outL_INTER = new BufferedWriter(new FileWriter(latency_file_INTER, false));
            outL_INTER.write(timeStore_INTER.toString() + "\n");
            outL_INTER.close();

            File success_file_INTER = new File("results/interdomain/success-rate.txt");
            success_file_INTER.createNewFile();
            BufferedWriter outS_INTER = new BufferedWriter(new FileWriter(success_file_INTER, false));
            outS_INTER.write(success_ratio_inter + "\n");
            outS_INTER.close();

            File message_file_INTER = new File("results/interdomain/messages.txt");
            message_file_INTER.createNewFile();
            BufferedWriter outM_INTER = new BufferedWriter(new FileWriter(message_file_INTER, false));
            outM_INTER.write(messageStore_INTER.toString() + "\n");
            outM_INTER.close();

            File adversarial_file_INTER = new File("results/interdomain/fraction-f.txt");
            adversarial_file_INTER.createNewFile();
            BufferedWriter outA_INTER = new BufferedWriter(new FileWriter(adversarial_file_INTER, false));
            outA_INTER.write(fraction_f_CS_INTER.toString() + "\n");
            outA_INTER.close();


            //create successful intra-domain lookup statistics files
            File hop_file_SUCCESS = new File("results/intradomain/successfulLookups/hops.txt");
            hop_file_SUCCESS.createNewFile();
            BufferedWriter outH_SUCCESS= new BufferedWriter(new FileWriter(hop_file_SUCCESS, false));
            outH_SUCCESS.write(shortestAmountHops_INTRA_SUCCESS.toString() + "\n");
            outH_SUCCESS.close();

            File latency_file_SUCCESS = new File("results/intradomain/successfulLookups/latency.txt");
            latency_file_SUCCESS.createNewFile();
            BufferedWriter outL_SUCCESS = new BufferedWriter(new FileWriter(latency_file_SUCCESS, false));
            outL_SUCCESS.write(timeStore_INTRA_SUCCESS.toString() + "\n");
            outL_SUCCESS.close();

            File message_file_SUCCESS = new File("results/intradomain/successfulLookups/messages.txt");
            message_file_SUCCESS.createNewFile();
            BufferedWriter outM_SUCCESS = new BufferedWriter(new FileWriter(message_file_SUCCESS, false));
            outM_SUCCESS.write(messageStore_INTRA_SUCCESS.toString() + "\n");
            outM_SUCCESS.close();

            File adversarial_file_SUCCESS = new File("results/intradomain/successfulLookups/fraction-f.txt");
            adversarial_file_SUCCESS.createNewFile();
            BufferedWriter outA_SUCCESS = new BufferedWriter(new FileWriter(adversarial_file_SUCCESS, false));
            outA_SUCCESS.write(fraction_f_INTRA_SUCCESS.toString() + "\n");
            outA_SUCCESS.close();

            //create successful inter-domain lookup statistics files
            File hop_file_INTER_SUCCESS = new File("results/interdomain/successfulLookups/hops.txt");
            hop_file_INTER_SUCCESS.createNewFile();
            BufferedWriter outH_INTER_SUCCESS= new BufferedWriter(new FileWriter(hop_file_INTER_SUCCESS, false));
            outH_INTER_SUCCESS.write(shortestAmountHops_INTER_SUCCESS.toString() + "\n");
            outH_INTER_SUCCESS.close();

            File latency_file_INTER_SUCCESS = new File("results/interdomain/successfulLookups/latency.txt");
            latency_file_INTER_SUCCESS.createNewFile();
            BufferedWriter outL_INTER_SUCCESS = new BufferedWriter(new FileWriter(latency_file_INTER_SUCCESS, false));
            outL_INTER_SUCCESS.write(timeStore_INTER_SUCCESS.toString() + "\n");
            outL_INTER_SUCCESS.close();

            File message_file_INTER_SUCCESS = new File("results/interdomain/successfulLookups/messages.txt");
            message_file_INTER_SUCCESS.createNewFile();
            BufferedWriter outM_INTER_SUCCESS = new BufferedWriter(new FileWriter(message_file_INTER_SUCCESS, false));
            outM_INTER_SUCCESS.write(messageStore_INTER_SUCCESS.toString() + "\n");
            outM_INTER_SUCCESS.close();

            File adversarial_file_INTER_SUCCESS = new File("results/interdomain/successfulLookups/fraction-f.txt");
            adversarial_file_INTER_SUCCESS.createNewFile();
            BufferedWriter outA_INTER_SUCCESS = new BufferedWriter(new FileWriter(adversarial_file_INTER_SUCCESS, false));
            outA_INTER_SUCCESS.write(fraction_f_INTER_SUCCESS.toString() + "\n");
            outA_INTER_SUCCESS.close();


            //create failed intra-domain lookup statistics files
            File latency_file_INTRA_FAIL = new File("results/intradomain/failedLookups/latency.txt");
            latency_file_INTRA_FAIL.createNewFile();
            BufferedWriter outL_INTRA_FAIL = new BufferedWriter(new FileWriter(latency_file_INTRA_FAIL, false));
            outL_INTRA_FAIL.write(timeStore_INTRA_FAILURE.toString() + "\n");
            outL_INTRA_FAIL.close();

            File message_file_INTRA_FAILED = new File("results/intradomain/failedLookups/messages.txt");
            message_file_INTRA_FAILED.createNewFile();
            BufferedWriter outM_INTRA_FAIL = new BufferedWriter(new FileWriter(message_file_INTRA_FAILED, false));
            outM_INTRA_FAIL.write(messageStore_INTRA_FAILURE.toString() + "\n");
            outM_INTRA_FAIL.close();

            File adversarial_file_INTRA_FAIL = new File("results/intradomain/failedLookups/fraction-f.txt");
            adversarial_file_INTRA_FAIL.createNewFile();
            BufferedWriter outA_INTRA_FAIL = new BufferedWriter(new FileWriter(adversarial_file_INTRA_FAIL, false));
            outA_INTRA_FAIL.write(fraction_f_INTRA_FAILURE.toString() + "\n");
            outA_INTRA_FAIL.close();

            //create failed inter-domain lookup statistics files
            File latency_file_INTER_FAIL = new File("results/interdomain/failedLookups/latency.txt");
            latency_file_INTER_FAIL.createNewFile();
            BufferedWriter outL_INTER_FAIL = new BufferedWriter(new FileWriter(latency_file_INTER_FAIL, false));
            outL_INTER_FAIL.write(timeStore_INTER_FAILURE.toString() + "\n");
            outL_INTER_FAIL.close();

            File message_file_INTER_FAILED = new File("results/interdomain/failedLookups/messages.txt");
            message_file_INTER_FAILED.createNewFile();
            BufferedWriter outM_INTER_FAIL = new BufferedWriter(new FileWriter(message_file_INTER_FAILED, false));
            outM_INTER_FAIL.write(messageStore_INTER_FAILURE.toString() + "\n");
            outM_INTER_FAIL.close();

            File adversarial_file_INTER_FAIL = new File("results/interdomain/failedLookups/fraction-f.txt");
            adversarial_file_INTER_FAIL.createNewFile();
            BufferedWriter outA_INTER_FAIL = new BufferedWriter(new FileWriter(adversarial_file_INTER_FAIL, false));
            outA_INTER_FAIL.write(fraction_f_INTER_FAILURE.toString() + "\n");
            outA_INTER_FAIL.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println();
        System.err.println(s);

        return false;
    }
}
