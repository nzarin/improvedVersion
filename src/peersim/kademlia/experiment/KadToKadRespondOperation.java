package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KadNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadRespondOperation implements RespondOperation2 {

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


    @Override
    public void respond() {


        System.err.println(" we are in the kad to kad respond operation class");
    }
}
