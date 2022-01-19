package peersim.kademlia.experiment;

import peersim.core.Node;
import peersim.kademlia.*;

import java.sql.SQLSyntaxErrorException;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadFindOperation implements FindOperation2{

    private KadNode myself;
    private KadNode dest;
    private int kademliaid;
    private Message m;
    private LinkedHashMap<Long, FindOperation> findOpsMap;
    private int tid;
    private TreeMap<Long, Long> sentMsg;
    private MessageSender messageSender;

    public KadToKadFindOperation(KadNode myself, KadNode destination, int kademliaid, Message lookupMessage,  LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
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
    public void find() {
        Node dst = Util.nodeIdtoNode(m.dest.getNodeId(), kademliaid);
        if((m.dest.getNodeId() == this.myself.getNodeId()) || (!dst.isUp()))
            return;

        KademliaObserver.find_op.add(1);

        FindOperation fop = new FindOperation(m.dest, m.timestamp);
        findOpsMap.put(fop.operationId, fop);

        KadNode[] neighbours = this.myself.getRoutingTable().getKClosestNeighbours(m.dest, this.myself);

        fop.updateClosestSet(neighbours);
        fop.available_requests = KademliaCommonConfig.ALPHA;

        //set message operation id
        m.operationId = fop.operationId;
        m.type = Message.MSG_ROUTE;
        m.src = this.myself;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++){
            KadNode nextNode = fop.getNeighbour();
            if(nextNode != null){
                fop.nrHops++;
                System.err.println("We are sending a ROUTE message to " + nextNode.getNodeId() + " which is a KadNode" );
                messageSender.sendMessage(m.copy(), this.myself, m.dest, sentMsg);
            }
        }
    }
}
