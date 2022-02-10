package peersim.kademlia;

import peersim.core.CommonState;

public class Statistician {


    /**
     * This method updates the statistics to the kademlia observer
     *
     * @param currentNode The node that initiated the lookup
     * @param fop         The find operation we are discussing now
     * @param kademliaid  The corresponding kademlia identifier
     */
    public static void updateLookupStatistics(KadNode currentNode, FindOperation fop, int kademliaid) {

        //if the target is found -> SUCCESSFUL LOOKUP
        if (fop.closestSet.containsKey(fop.destNode)) {

            //update statistics OVERALL
            KademliaObserver.finished_lookups_OVERALL.add(1);
            KademliaObserver.successful_lookups_OVERALL.add(1);
            KademliaObserver.messageStore_OVERALL.add(fop.nrMessages);
            KademliaObserver.shortestAmountHops_OVERALL.add(fop.shortestNrHops);
            long duration = (CommonState.getTime() - (fop.timestamp));
            KademliaObserver.timeStore_OVERALL.add(duration);
            KademliaObserver.fraction_f_CS_OVERALL.add(calculateFInClosestSet(fop));

            //update the correct statistics
            if (currentNode.getDomain() == fop.destNode.getDomain()) {
                updateIntraDomainLookupStatistics(fop, duration);
            } else {
                updateInterDomainLookupStatistics(fop, duration);
            }


            // if I and the destination node are up -> FAILURE LOOKUP
        } else if (Util.nodeIdtoNode(fop.destNode.getNodeId(), kademliaid).isUp() && Util.nodeIdtoNode(currentNode.getNodeId(), kademliaid).isUp()) {

            //update statistics OVERALL
            KademliaObserver.finished_lookups_OVERALL.add(1);
            KademliaObserver.failed_lookups_OVERALL.add(1);
            KademliaObserver.fraction_f_CS_OVERALL.add(calculateFInClosestSet(fop));

            //update statistics INTRA
            if (currentNode.getDomain() == fop.destNode.getDomain()) {
                KademliaObserver.finished_lookups_INTRA.add(1);
                KademliaObserver.failed_lookups_INTRA.add(1);
            } else {
                KademliaObserver.finished_lookups_INTER.add(1);
                KademliaObserver.failed_lookups_INTER.add(1);
            }
        }
    }

    public static double calculateFInClosestSet(FindOperation fop) {
        int nmrAdversarialNodes = 0;
        for (KadNode kadNode : fop.getClosestSet().keySet()) {
            if (kadNode.isMalicious()) {
                nmrAdversarialNodes++;
            }
        }

        //covert to doubles and return the value
        double nmrAdverserials = nmrAdversarialNodes;
        double sizeClosestSet = fop.getClosestSet().keySet().size();
        return (nmrAdverserials / sizeClosestSet);
    }


    public static void updateIntraDomainLookupStatistics(FindOperation fop, long duration) {
        KademliaObserver.finished_lookups_INTRA.add(1);
        KademliaObserver.messageStore_INTRA.add(fop.nrMessages);
        KademliaObserver.shortestAmountHops_INTRA.add(fop.shortestNrHops);
        KademliaObserver.timeStore_INTRA.add(duration);
        KademliaObserver.hopStore_INTRA.add(fop.nrHops);
        KademliaObserver.successful_lookups_INTRA.add(1);
        KademliaObserver.fraction_f_CS_INTRA.add(calculateFInClosestSet(fop));

    }


    public static void updateInterDomainLookupStatistics(FindOperation fop, long duration) {
        KademliaObserver.messageStore_INTER.add(fop.nrMessages);
        KademliaObserver.shortestAmountHops_INTER.add(fop.shortestNrHops);
        KademliaObserver.timeStore_INTER.add(duration);
        KademliaObserver.hopStore_INTER.add(fop.nrHops);
        KademliaObserver.finished_lookups_INTER.add(1);
        KademliaObserver.successful_lookups_INTER.add(1);
        KademliaObserver.fraction_f_CS_INTER.add(calculateFInClosestSet(fop));

    }


}
