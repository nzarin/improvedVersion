package peersim.kademlia;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.transport.UnreliableTransport;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class IntraDomainLookup implements Lookup{

    private int kademliaid;
    private KadNode currentNode;
    private LinkedHashMap<Long, FindOperation> findOpMap;
    private Message m;
    private int transportID;
    private UnreliableTransport transport;
    private TreeMap<Long, Long> sentMsg;

    public IntraDomainLookup(){
        System.err.println("do nothing for now");
    }

    public IntraDomainLookup(int kademliaId, KadNode current, LinkedHashMap<Long, FindOperation> findOps, Message lookupMessage, int transportID, TreeMap<Long,Long> sentMsg) {
        this.kademliaid = kademliaId;
        this.currentNode =  current;
        this.findOpMap = findOps;
        this.m = lookupMessage;
        this.transportID = transportID;
        this.sentMsg = sentMsg;
    }

    /**
     * Start a find node operation.
     * Find the ALPHA closest node and send find request to them.
     *
     */
    @Override
    public void find() {

        // if I am the searched node or searched node is down -> skip (should not happen in kademlia)
        Node dst = Util.nodeIdtoNode(m.dest.getNodeId(), kademliaid);
        if((m.dest.getNodeId() == this.currentNode.getNodeId()) || (!dst.isUp()))
            return;

        // increase the number of find operations
        KademliaObserver.find_op.add(1);

        // create find operation and add to operations array for bookkeeping
        FindOperation fop = new FindOperation(m.dest, m.timestamp);
        fop.body = m.body;
        findOpMap.put(fop.operationId, fop);


        // get the K closest node to search key
        KadNode[] neighbours = this.currentNode.getRoutingTable().getKClosestNeighbours(m.dest, this.currentNode);

        // update the list of closest nodes and re-initialize available requests
        fop.updateClosestSet(neighbours);
        fop.available_requests = KademliaCommonConfig.ALPHA;

        // set message operation id
        m.operationId = fop.operationId;
        m.type = Message.MSG_ROUTE;
        m.src = this.currentNode;

        // send alpha ROUTE messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++) {
            KadNode nextNode = fop.getNeighbour();
            if (nextNode != null) {
                KademliaObserver.next_node_distance.add(Util.distance(nextNode.getNodeId(), m.dest.getNodeId()).doubleValue());
                fop.nrHops++;
                sendMessage(m.copy(), nextNode, this.kademliaid);

            }
        }
    }


    /**
     * Response to a route request.
     * Find the ALPHA closest node consulting the k-buckets and return them to the sender.
     */
    @Override
    public void respond() {

        // get the k closest nodes to target node
        KadNode[] neighbours = this.currentNode.getRoutingTable().getKClosestNeighbours(m.dest, m.src);

        //get the BETA closest nodes from the neighbours
        KadNode[] betaNeighbours = Arrays.copyOfRange(neighbours, 0, KademliaCommonConfig.BETA);

        // create a response message containing the neighbours (with the same id as of the request)
        Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
        response.operationId = m.operationId;
        response.dest = m.dest;
        response.src = this.currentNode;
        response.ackId = m.id; // set ACK number

        // send back the neighbours to the source of the message
        sendMessage(response, m.src, this.kademliaid);
    }

    @Override
    public void handleResponse() {

        // add message source to my routing table
        if(m.src != null){
            this.currentNode.getRoutingTable().addNeighbour(m.src);
        }

        // get corresponding find operation (using the message field operationId)
        FindOperation fop = this.findOpMap.get(m.operationId);

        // if the fop is valid
        if(fop != null) {

            //update the closest set by saving the received neighbour
            try{
                fop.updateClosestSet((KadNode[]) m.body);
            }catch (Exception e){
                fop.available_requests++;
            }

            // send the new requests if allowed
            while(fop.available_requests > 0) {

                KadNode neighbour = fop.getNeighbour();

                //SCENARIO 1: there exists some neighbour we can visit.
                if(neighbour != null){

                    //create new request to send to neighbour
                    Message request = new Message(Message.MSG_ROUTE);
                    request.operationId = m.operationId;
                    request.src = this.currentNode;
                    request.dest = m.dest;

                    //increment hop count
                    fop.nrHops++;

                    //send messages
                    sendMessage(request, neighbour, this.kademliaid);

                    //SCENARIO 2: no new neighbour and no outstanding requests
                } else if(fop.available_requests == KademliaCommonConfig.ALPHA){

                    // Search operation finished. The lookup terminates when the initiator has queried and gotten responses
                    // from the k closest nodes (from the closest set) it has seen.
                    findOpMap.remove(fop.operationId);

                    // if the find operation was not for bootstrapping purposes
                    if(fop.body.equals("Automatically Generated Traffic")){
                        updateLookupStatistics(fop);
                    } else {
                        System.err.println("This is a bootstrap message. Let it be.");
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

    /**
     * Update the lookup statistics for the Kademlia Observer
     * @param fop
     */
    private void updateLookupStatistics(FindOperation fop) {

        // if the target is found -> successful lookup
        if(fop.closestSet.containsKey(fop.destNode)){

            // update statistics
            long timeInterval = (CommonState.getTime()) - (fop.timestamp);
            KademliaObserver.timeStore.add(timeInterval);
            KademliaObserver.hopStore.add(fop.nrHops);
            KademliaObserver.finished_lookups.add(1);
            KademliaObserver.successful_lookups.add(1);
//            System.err.println("!!!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP SUCCEEDED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            // if relevant parties were up -> failed lookup
        } else if(Util.nodeIdtoNode(fop.destNode.getNodeId(), kademliaid).isUp() && Util.nodeIdtoNode(this.currentNode.getNodeId(), kademliaid).isUp()){

            // update statistics
            KademliaObserver.finished_lookups.add(1);
            KademliaObserver.failed_lookups.add(1);
//            System.err.println("!!!!!!!!!!!!!!! ATTENTION: THIS LOOKUP FAILED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        }

    }


    /**
     * Send a message with current transport layer and starting the timeout timer (which is an event) if the message is a request
     *
     * @param m
     *            The message to send.
     * @param destId
     *            The Id of the destination node.
     * @param myPid
     *            The sender Pid.
     */
    private void sendMessage(Message m, KadNode destId, int myPid) {
        // add destination to routing table
        this.currentNode.getRoutingTable().addNeighbour(destId);

        Node src = Util.nodeIdtoNode(this.currentNode.getNodeId(), kademliaid);
        Node dest = Util.nodeIdtoNode(destId.getNodeId(), kademliaid);

        transport = (UnreliableTransport) (Network.prototype).getProtocol(transportID);
        transport.send(src, dest, m, kademliaid);

        if (m.getType() == Message.MSG_ROUTE) { // is a request
            Timeout t = new Timeout(destId, m.id, m.operationId);

            // set delay at 2*RTT
            long latency = transport.getLatency(src, dest);
            long delay = 4*latency;

            // add to sent msg
            this.sentMsg.put(m.id, m.timestamp);
            EDSimulator.add(delay, t, src, myPid);
        }
    }


}
