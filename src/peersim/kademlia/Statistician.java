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

        //if the target is found -> successful lookup
        if (fop.closestSet.containsKey(fop.destNode)) {

            long duration = (CommonState.getTime() - (fop.timestamp));

            if(currentNode.getDomain() == fop.destNode.getDomain()){

                //update statistics INTRA
                KademliaObserver.messageStore_INTRA.add(fop.nrMessages);
                KademliaObserver.shortestAmountHops_INTRA.add(fop.shortestNrHops);
                KademliaObserver.timeStore_INTRA.add(duration);
                KademliaObserver.hopStore_INTRA.add(fop.nrHops);
                KademliaObserver.finished_lookups_INTRA.add(1);
                KademliaObserver.successful_lookups_INTRA.add(1);

                //update statistics INTER
            } else {
                KademliaObserver.messageStore_INTER.add(fop.nrMessages);
                KademliaObserver.shortestAmountHops_INTER.add(fop.shortestNrHops);
                KademliaObserver.timeStore_INTER.add(duration);
                KademliaObserver.hopStore_INTER.add(fop.nrHops);
                KademliaObserver.finished_lookups_INTER.add(1);
                KademliaObserver.successful_lookups_INTER.add(1);
            }

            //update statistics OVERALL
            KademliaObserver.messageStore_OVERALL.add(fop.nrMessages);
            KademliaObserver.shortestAmountHops_OVERALL.add(fop.shortestNrHops);
            KademliaObserver.timeStore_OVERALL.add(duration);
            KademliaObserver.hopStore_OVERALL.add(fop.nrHops);
            KademliaObserver.finished_lookups_OVERALL.add(1);
            KademliaObserver.successful_lookups_OVERALL.add(1);

//            System.err.println("\n!!!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP SUCCEEDED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

            // if I and the destination node are up -> failed lookup
        } else if (Util.nodeIdtoNode(fop.destNode.getNodeId(), kademliaid).isUp() && Util.nodeIdtoNode(currentNode.getNodeId(), kademliaid).isUp()) {

            //update statistics INTRA
            if (currentNode.getDomain() == fop.destNode.getDomain()){
                KademliaObserver.finished_lookups_INTRA.add(1);
                KademliaObserver.failed_lookups_INTRA.add(1);
            } else {
                KademliaObserver.finished_lookups_INTER.add(1);
                KademliaObserver.failed_lookups_INTER.add(1);
            }


            //update statistics OVERALL
            KademliaObserver.finished_lookups_OVERALL.add(1);
            KademliaObserver.failed_lookups_OVERALL.add(1);
//            System.err.println("\n!!!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP FAILED !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

        }
    }
}
