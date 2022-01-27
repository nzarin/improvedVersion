package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

/**
 * This class represents how the response should be handled when source and target are both KadNodes
 */
public class KadToKadHandleResponseOperation extends HandleResponseOperation2 {


    public KadToKadHandleResponseOperation(int kid, Message lookupMsg, int tid) {
        this.source = (KadNode) lookupMsg.src;
        this.target = (KadNode) lookupMsg.target;
        this.sender = lookupMsg.sender;
        this.receiver = lookupMsg.receiver;
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }


    @Override
    public void handleResponse() {
        // remove the timer for the deadline because we already received a response on time
        lookupMessage.src.getSentMsgTracker().remove(lookupMessage.ackId);

        // add message sender to my routing table
        if (lookupMessage.sender != null) {
            lookupMessage.src.getRoutingTable().addNeighbour((KadNode) lookupMessage.sender);
        }

        // get corresponding find operation (using the message field operationId)
        FindOperation fop = lookupMessage.src.getFindOperationsMap().get(lookupMessage.operationId);

        if (fop != null) {

            //Step 1: update the closest set by saving the received neighbour
            try {
                fop.updateClosestSet((KadNode[]) lookupMessage.body);
            } catch (Exception e) {
                fop.available_requests++;
            }

            // Step 2: send the new requests if allowed
            while (fop.available_requests > 0) {
                KadNode neighbour = fop.getNeighbour();

                //SCENARIO 1: there exists some neighbour we can visit.
                if (neighbour != null) {

                    //create new request to send to this neighbour
                    Message request = new Message(Message.MSG_REQUEST);
                    request.src = lookupMessage.src;
                    request.target = lookupMessage.target;
                    request.sender = lookupMessage.receiver;
                    request.receiver = neighbour;
                    request.operationId = fop.operationId;

                    request.newLookup = false;
                    //increment hop count for bookkeeping
                    fop.nrHops++;

                    //send the ROUTE message
                    messageSender.sendMessage(request);

                    //SCENARIO 2: no new neighbour and no outstanding requests
                } else if (fop.available_requests == KademliaCommonConfig.ALPHA) {

                    // Search operation finished. The lookup terminates when the initiator has queried and gotten responses
                    // from the k closest nodes (from the closest set) it has seen.
                    this.receiver.getFindOperationsMap().remove(fop.operationId);

                    // if the lookup operation was not for bootstrapping purposes
                    if (fop.body.equals("Automatically Generated Traffic")) {
                        Util.updateLookupStatistics((KadNode) this.receiver, fop, this.kademliaid);
                    } else {
                        System.err.println("This is a bootstrap message. Let it be. ");
                    }
                    return;

                    //SCENARIO 3: no neighbour available, but there are some open outstanding requests so just wait.
                } else {
                    return;
                }
            }
        } else {
            System.err.println("There has been some error in the protocol");
        }


    }
}
