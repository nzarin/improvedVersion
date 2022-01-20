package peersim.kademlia.FindOperations;

import peersim.core.Node;
import peersim.kademlia.*;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadFindOperation extends FindOperation2 {


    public KadToKadFindOperation(KadNode me, KadNode destination, int kid, Message lookupMsg,  LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid) {
        source = me;
        dest = destination;
        kademliaid = kid;
        lookupMessage = lookupMsg;
        findOperationsMap = findOpsMap;
        sentMsgTracker = sentMsg;
        transportid = tid;
        messageSender = new MessageSender(kademliaid, tid);
        findOp = new FindOperation((KadNode) lookupMsg.dest, lookupMsg.timestamp);

    }

    public KadToKadFindOperation(){
        //empty constructor
    }



    @Override
    public void find() {
        Node dst = Util.nodeIdtoNode(lookupMessage.dest.getNodeId(), kademliaid);
        if((lookupMessage.dest.getNodeId() == this.source.getNodeId()) || (!dst.isUp()))
            return;

        KademliaObserver.find_op.add(1);

        findOperationsMap.put(findOp.operationId, findOp);

        KadNode[] neighbours = this.source.getRoutingTable().getKClosestNeighbours((KadNode) lookupMessage.dest, this.source);

        findOp.updateClosestSet(neighbours);
        findOp.available_requests = KademliaCommonConfig.ALPHA;

        //set message operation id
        lookupMessage.operationId = findOp.operationId;
        lookupMessage.type = Message.MSG_ROUTE;
        lookupMessage.src = this.source;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++){
            KadNode nextNode = findOp.getNeighbour();
            if(nextNode != null){
                findOp.nrHops++;
                System.err.println("We are sending a ROUTE message to " + nextNode.getNodeId() + " which is a KadNode" );
                messageSender.sendMessage(lookupMessage.copy(), this.source, lookupMessage.dest, sentMsgTracker);
            }
        }
    }
}
