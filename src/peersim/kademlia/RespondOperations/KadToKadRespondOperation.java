package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

import java.util.Arrays;


public class KadToKadRespondOperation extends RespondOperation2 {

    public KadToKadRespondOperation(KadNode source, KadNode target, KadNode sender, KadNode receiver, int kademliaid, Message lookupMessage, int tid){
        System.err.println("~KadToKadRespondOperation~ constructor");
        this.source = source;
        this.target = target;
        this.sender = sender;
        this.receiver = receiver;
        this.kademliaid = kademliaid;
        this.lookupMessage = lookupMessage;
        messageSender = new MessageSender(kademliaid, tid);
    }


    @Override
    public void respond() {
//
//        System.err.println("   before we respond: message.operationId : " + this.lookupMessage.operationId);
//        System.err.println("   before we respond: this.source " + this.source.getNodeId());
//        System.err.println("   before we respond: this.target " + this.target.getNodeId());
//        System.err.println("   before we respnod: this.sender " + this.sender.getNodeId());
//        System.err.println("   before we respond: this.receiver: " + this.receiver.getNodeId());

//        System.err.println("~KadToKadRespondOperation~ respond()");

        // get the k closest nodes to target node -> I AM RECEIVER OF THE MESSAGE
        KadNode[] neighbours = this.receiver.getRoutingTable().getKClosestNeighbours(this.target, (KadNode) this.sender);

        //get the BETA closest nodes from the neighbours
        KadNode[] betaNeighbours = Arrays.copyOfRange(neighbours, 0, KademliaCommonConfig.BETA);

        // create a response message containing the neighbours (with the same id as of the request)
        Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
        response.operationId = lookupMessage.operationId;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.receiver = lookupMessage.sender;
        response.sender = lookupMessage.receiver;
        response.newLookup = false;
        System.err.println("    lookupMessage.operationId " + lookupMessage.operationId);
        System.err.println("    response.operationId " + response.operationId);
//        System.err.println("    response.src " + response.src.getNodeId());
//        System.err.println("    response.target " + response.target.getNodeId());
//        System.err.println("    response.receiver " + response.receiver.getNodeId());
//        System.err.println("    response.sender " + response.sender.getNodeId());
//        System.err.println("    response.newLookup " + response.newLookup);
        response.ackId = lookupMessage.msgId;
        System.err.println("I am sending a RESPONSE message to " + response.receiver.getNodeId() + " with body: " + response.body.toString());
        messageSender.sendMessage(response);
//        System.err.println(" KadToKadRespondOperation.src: " + this.source.getNodeId());
//        System.err.println(" KadToKadRespondOperation.target: " + this.target.getNodeId());
//        System.err.println(" KadToKadRespondOperation.sender: " + this.sender.getNodeId());
//        System.err.println(" KadToKadRespondOperation.receiver: " + this.receiver.getNodeId());

    }
}
