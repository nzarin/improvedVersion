package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadHandleResponseOperation extends HandleResponseOperation2 {

    private KadNode myself;
    private KadNode dest;
    private int kademliaid;
    private Message m;
    private LinkedHashMap<Long, FindOperation> findOpsMap;
    private int tid;
    private TreeMap<Long, Long> sentMsg;
    private MessageSender messageSender;

    public KadToKadHandleResponseOperation(KadNode myself, KadNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        this.myself = myself;
        this.dest = destination;
        this.kademliaid = kademliaid;
        this.m = lookupMessage;
        this.findOpsMap = findOpsMap;
        this.sentMsg = sentMsg;
        this.tid = tid;
        this.messageSender = new MessageSender(kademliaid, tid);
    }

    public KadToKadHandleResponseOperation(){
        //empty constructor
    }

    @Override
    public void handleResponse() {

        //create new room for sending  s we received a response for the sent message
        sentMsg.remove(m.ackId);

        // add message source to my routing table
        if(m.src != null){
            this.myself.getRoutingTable().addNeighbour((KadNode) m.src);
        }

        // get corresponding find operation (using the message field operationId)
        FindOperation fop = this.findOpsMap.get(m.operationId);

        if(fop != null){

            //update the closest set by saving the received neighbour
            try{
                fop.updateClosestSet((KadNode[]) m.body);
            } catch (Exception e){
                fop.available_requests++;
            }

            // send the new requests if allowed
            while(fop.available_requests > 0){
                KadNode neighbour = fop.getNeighbour();

                //SCENARIO 1: there exists some neighbour we can visit.
                if(neighbour != null){

                    //create new request to send to this neighbour
                    Message request = new Message(Message.MSG_ROUTE);
                    request.operationId = m.operationId;
                    request.src = this.myself;
                    request.dest = m.dest;

                    //increment hop count for bookkeeping
                    fop.nrHops++;

                    System.err.println("We are sending a route message to " + neighbour.getNodeId());

                    //send the ROUTE messages
                    messageSender.sendMessage(request, this.myself, neighbour, sentMsg);

                    //SCENARIO 2: no new neighbour and no outstanding requests
                } else if(fop.available_requests == KademliaCommonConfig.ALPHA){

                    // Search operation finished. The lookup terminates when the initiator has queried and gotten responses
                    // from the k closest nodes (from the closest set) it has seen.
                    this.findOpsMap.remove(fop.operationId);

                    // if the lookup operation was not for bootstrapping purposes
                    if(fop.body.equals("Automatically Generated Traffic")){
                        Util.updateLookupStatistics(this.myself, fop, this.kademliaid);
                    } else{
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
