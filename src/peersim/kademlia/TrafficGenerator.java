package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

/**
 * This control generates random search traffic from nodes to random destination node.
 *
 */
public class TrafficGenerator implements Control {

	/**
	 * Protocol to act
	 */
	private final static String PAR_PROT = "protocol";

	/**
	 * Protocol ID to act
	 */
	private final int pid;

	/**
	 * Constructor that links the CONTROL and PROTOCOL objects.
	 * @param prefix
	 */
	public TrafficGenerator(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	/**
	 * Generates a find node message, by randomly selecting the destination.
	 * 
	 * @return Message
	 */
	private Message generateFindNodeMessage() {
		Message m = Message.makeEmptyMessage("Automatically Generated Traffic", Message.MSG_FINDNODE);
		m.timestamp = CommonState.getTime();

		// existing active destination node
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		while (!n.isUp()) {
			n = Network.get(CommonState.r.nextInt(Network.size()));
		}
		m.dest = ((KademliaProtocol) (n.getProtocol(pid))).nodeId;

		return m;
	}

	// ______________________________________________________________________________________________
	/**
	 * Every call of this control generates and send a random find node message
	 * 
	 * @return boolean
	 */
	public boolean execute() {
		Node start;
		do {
			start = Network.get(CommonState.r.nextInt(Network.size()));
		} while ((start == null) || (!start.isUp()));


		// send message
		EDSimulator.add(0, generateFindNodeMessage(), start, pid);

		return false;
	}

}