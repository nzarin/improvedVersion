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

//        System.err.println("findOp " + fop.operationId + " has finished and we are collecting statistics");

        //if the target is found AND IT was online -> SUCCESSFUL LOOKUP
        if (fop.closestSet.containsKey(fop.destNode) && Util.nodeIdtoNode(fop.destNode.getNodeId(), kademliaid).isUp()) {

            updateSuccessfulLookup(currentNode, fop);
            System.err.println();
            System.err.println("SUCCESFULL LOOKUP! ");
            // if I am still up -> FAILURE LOOKUP
        } else if (Util.nodeIdtoNode(currentNode.getNodeId(), kademliaid).isUp()) {

            updateFailedLookup(currentNode, fop);
            System.err.println();
            System.err.println("FAILED LOOKUP! ");

        } else {

            System.err.println("Check this case in Statistician");

        }
    }

    public static double calculateFInClosestSet(FindOperation findOp) {
        int nmrAdversarialNodes = 0;
        for (KadNode kadNode : findOp.getClosestSet().keySet()) {
            if (kadNode.isMalicious()) {
                nmrAdversarialNodes++;
            }
        }

        //covert to doubles and return the value
        double nmrAdverserials = nmrAdversarialNodes;
        double sizeClosestSet = findOp.getClosestSet().keySet().size();
        return (nmrAdverserials / sizeClosestSet);
    }


    public static void updateIntraDomainLookupStatistics(FindOperation findOp, long duration, boolean success) {
        KademliaObserver.finished_lookups_INTRA.add(1);
        KademliaObserver.messageStore_INTRA.add(findOp.nrMessages);
        KademliaObserver.timeStore_INTRA.add(duration);
        KademliaObserver.fraction_f_CS_INTRA.add(calculateFInClosestSet(findOp));

        if (success) {
            KademliaObserver.successful_lookups_INTRA.add(1);
            KademliaObserver.shortestAmountHops_INTRA.add(findOp.shortestNrHops);
        } else {
            KademliaObserver.failed_lookups_INTRA.add(1);
        }
    }


    public static void updateInterDomainLookupStatistics(FindOperation findOp, long duration, boolean success) {
        KademliaObserver.finished_lookups_INTER.add(1);
        KademliaObserver.messageStore_INTER.add(findOp.nrMessages);
        KademliaObserver.timeStore_INTER.add(duration);
        KademliaObserver.fraction_f_CS_INTER.add(calculateFInClosestSet(findOp));

        if (success) {
            KademliaObserver.successful_lookups_INTER.add(1);
            KademliaObserver.shortestAmountHops_INTER.add(findOp.shortestNrHops);
        } else {
            KademliaObserver.failed_lookups_INTER.add(1);
        }
    }

    public static void updateSuccessfulLookup(KadNode currentNode, FindOperation findOp) {

        long duration = (CommonState.getTime() - (findOp.timestamp));

        if (currentNode.getDomain() == findOp.destNode.getDomain()) {
            updateIntraDomainLookupStatistics(findOp, duration, true);
            KademliaObserver.shortestAmountHops_INTRA_SUCCESS.add(findOp.shortestNrHops);
            KademliaObserver.timeStore_INTRA_SUCCESS.add(duration);
            KademliaObserver.messageStore_INTRA_SUCCESS.add(findOp.nrMessages);
            KademliaObserver.fraction_f_INTRA_SUCCESS.add(calculateFInClosestSet(findOp));
        } else {
            updateInterDomainLookupStatistics(findOp, duration, true);
            KademliaObserver.shortestAmountHops_INTER_SUCCESS.add(findOp.shortestNrHops);
            KademliaObserver.timeStore_INTER_SUCCESS.add(duration);
            KademliaObserver.messageStore_INTER_SUCCESS.add(findOp.nrMessages);
            KademliaObserver.fraction_f_INTER_SUCCESS.add(calculateFInClosestSet(findOp));
        }

    }

    public static void updateFailedLookup(KadNode currentNode, FindOperation findOp) {

        long duration = (CommonState.getTime() - (findOp.timestamp));

        //update the correct inter or intra domain statistics
        if (currentNode.getDomain() == findOp.destNode.getDomain()) {
            updateIntraDomainLookupStatistics(findOp, duration, false);
            KademliaObserver.timeStore_INTRA_FAILURE.add(duration);
            KademliaObserver.messageStore_INTRA_FAILURE.add(findOp.nrMessages);
            KademliaObserver.fraction_f_INTRA_FAILURE.add(calculateFInClosestSet(findOp));
        } else {
            updateInterDomainLookupStatistics(findOp, duration, false);
            KademliaObserver.timeStore_INTER_FAILURE.add(duration);
            KademliaObserver.messageStore_INTER_FAILURE.add(findOp.nrMessages);
            KademliaObserver.fraction_f_INTER_FAILURE.add(calculateFInClosestSet(findOp));
        }
    }


}
