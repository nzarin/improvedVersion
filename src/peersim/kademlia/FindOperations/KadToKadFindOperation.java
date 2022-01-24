package peersim.kademlia.FindOperations;

import peersim.core.Node;
import peersim.kademlia.*;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public class KadToKadFindOperation extends FindOperation2 {


    public KadToKadFindOperation(KadNode source, KadNode target, KadNode sender, KadNode receiver, int kid, Message lookupMsg,  int tid) {
        System.err.println("~KadToKadFindOperation~ constructor");
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
    public void find() {
        System.err.println("~KadToKadFindOperation~ find()");

        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if((lookupMessage.target.getNodeId() == this.source.getNodeId()) || (!target.isUp()))
            return;
        KademliaObserver.find_op.add(1);

        FindOperation findOp = new FindOperation(this.target, lookupMessage.timestamp);
        findOp.body = lookupMessage.body;
        this.source.getFindOperationsMap().put(findOp.operationId, findOp);
        System.err.println(" we created a findop for sender " + source.getNodeId() + " with findOp.operationid = " + findOp.operationId);
        System.err.println(" its findoperationsmap is now " + source.getFindOperationsMap().toString());
        KadNode[] neighbours = this.source.getRoutingTable().getKClosestNeighbours((KadNode) lookupMessage.target, source);

        findOp.updateClosestSet(neighbours);
        findOp.available_requests = KademliaCommonConfig.ALPHA;

        //create a request message
        Message request = new Message(Message.MSG_ROUTE);
        request.src = lookupMessage.src;
        request.target = lookupMessage.target;
        request.sender = lookupMessage.src;
        request.operationId = findOp.operationId;
        request.newLookup = lookupMessage.newLookup;

        //send ALPHA route messages
        for (int i = 0; i < KademliaCommonConfig.ALPHA; i++){
            KadNode nextNode = findOp.getNeighbour();
            if(nextNode != null){
                findOp.nrHops++;
                request.receiver = nextNode;
                System.err.println("I am sending a ROUTE message to " + request.receiver.getNodeId());
                System.err.println("    request.operationId " + request.operationId);
                System.err.println("    request.src " + request.src.getNodeId());
                System.err.println("    request.target " + request.target.getNodeId());
                System.err.println("    request.receiver " + request.receiver.getNodeId());
                System.err.println("    request.sender" + request.sender.getNodeId());
                System.err.println("    request.newLookup " + request.newLookup);
                messageSender.sendMessage(request);
            }
        }
    }
}
