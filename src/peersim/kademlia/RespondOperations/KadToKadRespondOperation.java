package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

import java.util.Arrays;

/**
 *  This class represents the respond operation when the source and target are both KadNodes
 */
public class KadToKadRespondOperation extends RespondOperation {

    public KadToKadRespondOperation(int kademliaid, Message lookupMessage, int tid){
        this.kademliaid = kademliaid;
        this.lookupMessage = lookupMessage;
        messageSender = new MessageSender(kademliaid, tid);
    }


    @Override
    public void respond() {

        //try to update the routing table
        if(lookupMessage.sender instanceof KadNode){
            lookupMessage.receiver.getRoutingTable().addNeighbour((KadNode) lookupMessage.sender);
        }

        // get the k closest nodes to target node -> I AM RECEIVER OF THE MESSAGE
        KadNode[] neighbours = lookupMessage.receiver.getRoutingTable().getKNeighbours(lookupMessage.target.getNodeId(), (KadNode) lookupMessage.receiver, (KadNode) lookupMessage.src);

        //get the BETA closest nodes from the neighbours
        KadNode[] betaNeighbours = Arrays.copyOfRange(neighbours, 0, KademliaCommonConfig.BETA);

        //update statistics
        FindOperation findOp = lookupMessage.sender.getFindOperationsMap().get(lookupMessage.operationId);
        findOp.nrMessages++;

        // create a response message containing the neighbours (with the same id as of the request)
        Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
        response.operationId = lookupMessage.operationId;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.receiver = lookupMessage.sender;
        response.sender = lookupMessage.receiver;
        response.newLookup = false;
        response.ackId = lookupMessage.msgId;
//        System.err.println(" I am sending a RESPONSE message to (" + response.receiver.getNodeId() + "," + response.receiver.getDomain() +  ") of type " + response.receiver.getType() + " with msgId is " + response.msgId);
        messageSender.sendMessage(response);

    }
}
