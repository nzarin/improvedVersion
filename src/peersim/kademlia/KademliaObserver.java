package peersim.kademlia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

/**
 * This class implements a simple observer of search time and hop average in finding a node in the network
 */
public class KademliaObserver implements Control {

	/**
	 * Keep statistics of the number of hops of every message delivered.
	 */
	public static IncrementalStats hopStore = new IncrementalStats();

	/**
	 * Keep statistics of the time every message delivered.
	 */
	public static IncrementalStats timeStore = new IncrementalStats();

	/**
	 * Keep statistic of number of message delivered
	 */
	public static IncrementalStats msg_deliv = new IncrementalStats();

	/**
	 * Keep statistic of number of find operation
	 */
	public static IncrementalStats find_op = new IncrementalStats();

	/** Parameter of the protocol we want to observe */
	private static final String PAR_PROT = "protocol";

	/** Protocol id */
	private int pid;

	/** Prefix to be printed in output */
	private String prefix;

	/**
	 * Constructor that links the Control and Protocol objects.
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

		String s = String.format("[time=%d]:[N=%d current nodes UP] [D=%f msg delivered] [%f min hops] [%f average hops] [%f max hops] [%d min ltcy] [%d msec average ltcy] [%d max ltcy] [%s nmr findops]", CommonState.getTime(), sz, msg_deliv.getSum(), hopStore.getMin(), hopStore.getAverage(), hopStore.getMax(), (int) timeStore.getMin(), (int) timeStore.getAverage(), (int) timeStore.getMax(), find_op.toString());

		//todo: write once at the end

		// create hop file
		try {
			File f = new File("hopcountNEW.txt"); // " + sz + "
			f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(String.valueOf(hopStore.getAverage()).replace(".", ",") + ";\n");
			out.close();
		} catch (IOException e) {
		}
		// create latency file
		try {
			File f = new File("latencyNEW.txt");
			f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(String.valueOf(timeStore.getAverage()).replace(".", ",") + ";\n");
			out.close();
		} catch (IOException e) {
		}

//		}

		System.err.println(s);

		return false;
	}
}
