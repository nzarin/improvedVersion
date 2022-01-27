package peersim.kademlia.FindOperations;

import peersim.core.CommonState;
import peersim.core.Node;
import peersim.kademlia.*;

public class KadToBridgeFindOperation extends FindOperation2 {

    public KadToBridgeFindOperation(int kid, Message lookupMsg, int tid){
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
    public void find() {
        //If I searched node is down, do nothing
        Node target = Util.nodeIdtoNode(lookupMessage.target.getNodeId(), kademliaid);
        if (!target.isUp())
            return;

        // create find operation
        FindOperation findOp = new FindOperation((KadNode) lookupMessage.target, lookupMessage.timestamp);
        KademliaObserver.find_op.add(1);
        findOp.body = lookupMessage.body;
        lookupMessage.receiver.getFindOperationsMap().put(findOp.operationId, findOp);
        findOp.available_requests = KademliaCommonConfig.ALPHA;
        findOp.nrHops++;

        System.err.println(" My list of bridge is as follows: ");
        for(BridgeNode b:  lookupMessage.receiver.getBridgeNodes()){
            System.err.print(b.getNodeId() + ", ");
        }
        System.err.println();

        // find a random bridge of our domain and other domain and send it the route message
        BridgeNode randomBridgeNodeThisDomain;
        do{
            randomBridgeNodeThisDomain = lookupMessage.receiver.getBridgeNodes().get(CommonState.r.nextInt(lookupMessage.receiver.getBridgeNodes().size()));
        } while (randomBridgeNodeThisDomain == null);

        System.err.println(" i am forwarding the FIND message to " + randomBridgeNodeThisDomain.getNodeId() + " which is in this domain");
        // create FINDNODE message to send it to this bridge node
        Message forward = new Message(Message.MSG_FINDNODE);
        forward.src = lookupMessage.src;
        forward.target = lookupMessage.target;
        forward.sender = lookupMessage.receiver;
        forward.receiver = randomBridgeNodeThisDomain;
        forward.operationId = lookupMessage.operationId;
        forward.newLookup = lookupMessage.newLookup;
        messageSender.sendMessage(forward);
    }
}
