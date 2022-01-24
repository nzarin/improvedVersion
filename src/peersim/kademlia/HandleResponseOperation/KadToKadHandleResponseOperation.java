package peersim.kademlia.HandleResponseOperation;

import peersim.kademlia.*;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadHandleResponseOperation extends HandleResponseOperation2 {


    public KadToKadHandleResponseOperation(KadNode source, KadNode target, KadNode sender, KadNode receiver, int kid, Message lookupMsg,  int tid) {
//        System.err.println("~KadToKadHandleResponseOperation~ constructor");
        this.source = source;
        this.target = target;
        this.sender = sender;
        this.receiver = receiver;
        kademliaid = kid;
        lookupMessage = lookupMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
    }


    @Override
    public void handleResponse() {
//        System.err.println("~KadToKadHandleResponseOperation~ handleResponse()");
        //create new room for sending  s we received a response for the sent message -> I AM THE RECEIVER
//        System.err.println("this.receiver in handleResponse: " + this.receiver.getNodeId());
//        System.err.println("this.receiver.getSentMsgTracker in handleResponse: " + this.receiver.getSentMsgTracker());
        this.source.getSentMsgTracker().remove(lookupMessage.ackId);

        // add message sender to my routing table
        if(lookupMessage.sender != null){
            this.source.getRoutingTable().addNeighbour((KadNode) lookupMessage.sender);
        }
//        System.err.println(" i am trying to get the fop from " + this.receiver.getNodeId() + " for the operationid " + lookupMessage.operationId);
        // get corresponding find operation (using the message field operationId)

        System.err.println(" findoperationsmap looks now like this (before fetching it):  " + this.source.getFindOperationsMap().toString());
        System.err.println(" The lookupMessage.operationId is : " + lookupMessage.operationId);
        FindOperation fop = this.source.getFindOperationsMap().get(lookupMessage.operationId);

        if(fop != null){

            //update the closest set by saving the received neighbour
            try{
                fop.updateClosestSet((KadNode[]) lookupMessage.body);
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
                    request.src = lookupMessage.src;
                    request.target = lookupMessage.target;
                    request.sender = lookupMessage.receiver;
                    request.receiver = neighbour;
                    request.operationId = fop.operationId;
                    request.newLookup = false;

                    //increment hop count for bookkeeping
                    fop.nrHops++;

                    System.err.println("I have processed the response and I am sending a route message to " + neighbour.getNodeId());
//                    System.err.println("I am sending a ROUTE message to " + request.receiver.getNodeId());
                    System.err.println("    request.operationId " + request.operationId);
//                    System.err.println("    request.src " + request.src.getNodeId());
//                    System.err.println("    request.target " + request.target.getNodeId());
//                    System.err.println("    request.receiver " + request.receiver.getNodeId());
//                    System.err.println("    request.sender" + request.sender.getNodeId());
//                    System.err.println("    request.newLookup " + request.newLookup);
                    //send the ROUTE messages
                    messageSender.sendMessage(request);
//                    System.err.println(" KadToKadHandleResponseOperation.src: " + this.source.getNodeId());
//                    System.err.println(" KadToKadHandleResponseOperation.target: " + this.target.getNodeId());
//                    System.err.println(" KadToKadHandleResponseOperation.sender: " + this.sender.getNodeId());
//                    System.err.println(" KadToKadHandleResponseOperation.receiver: " + this.receiver.getNodeId());
                    //SCENARIO 2: no new neighbour and no outstanding requests
                } else if(fop.available_requests == KademliaCommonConfig.ALPHA){

                    // Search operation finished. The lookup terminates when the initiator has queried and gotten responses
                    // from the k closest nodes (from the closest set) it has seen.
                    this.receiver.getFindOperationsMap().remove(fop.operationId);

                    // if the lookup operation was not for bootstrapping purposes
                    if(fop.body.equals("Automatically Generated Traffic")){
                        Util.updateLookupStatistics((KadNode) this.receiver, fop, this.kademliaid);
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
