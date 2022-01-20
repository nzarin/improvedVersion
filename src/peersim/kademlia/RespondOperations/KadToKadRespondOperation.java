package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadRespondOperation extends RespondOperation2 {

    private KadNode myself;
    private KadNode dest;
    private int kademliaid;
    private Message m;
    private LinkedHashMap<Long, FindOperation> findOpsMap;
    private int tid;
    private TreeMap<Long, Long> sentMsg;
    private MessageSender messageSender;

    public KadToKadRespondOperation(KadNode myself, KadNode destination, int kademliaid, Message lookupMessage,  LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid){
        this.myself = myself;
        this.dest = destination;
        this.kademliaid = kademliaid;
        this.m = lookupMessage;
        this.findOpsMap = findOpsMap;
        this.sentMsg = sentMsg;
        this.tid = tid;
        this.messageSender = new MessageSender(kademliaid, tid);
    }

    public KadToKadRespondOperation(){
        //empty constructor
    }


    @Override
    public void respond() {

        // get the k closest nodes to target node
        KadNode[] neighbours = this.myself.getRoutingTable().getKClosestNeighbours((KadNode) m.dest, (KadNode) m.src);

        //get the BETA closest nodes from the neighbours
        KadNode[] betaNeighbours = Arrays.copyOfRange(neighbours, 0, KademliaCommonConfig.BETA);

        // create a response message containing the neighbours (with the same id as of the request)
        Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
        response.operationId = m.operationId;
        response.dest = m.dest;
        response.src = this.myself;
        response.ackId = m.msgId;
        System.err.println("We are sending a RESPONSE message to " + m.dest.getNodeId() + " which is a KadNode" );
        messageSender.sendMessage(response, m.src, this.myself, sentMsg);

    }
}
