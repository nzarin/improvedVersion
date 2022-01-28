package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

import java.util.Arrays;

/**
 *  This class represents the respond operation when the source and target are both KadNodes
 */
public class KadToKadRespondOperation extends RespondOperation2 {

    public KadToKadRespondOperation(int kademliaid, Message lookupMessage, int tid){
        this.source = (KadNode) lookupMessage.src;
        this.target = (KadNode) lookupMessage.target;
        this.sender = lookupMessage.sender;
        this.receiver = lookupMessage.receiver;
        this.kademliaid = kademliaid;
        this.lookupMessage = lookupMessage;
        messageSender = new MessageSender(kademliaid, tid);
    }


    @Override
    public void respond() {

        // get the k closest nodes to target node -> I AM RECEIVER OF THE MESSAGE
        KadNode[] neighbours = lookupMessage.receiver.getRoutingTable().getKClosestNeighbours((KadNode) lookupMessage.target, (KadNode) lookupMessage.sender);

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
        response.ackId = lookupMessage.msgId;
        System.err.println("    I am sending a RESPONSE message to " + response.receiver.getNodeId() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}
