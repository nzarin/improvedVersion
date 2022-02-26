package peersim.kademlia.RespondOperations;

import peersim.kademlia.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ImprovedRespondOperation extends RespondOperation{

    public ImprovedRespondOperation(int kademliaid, Message lookupMessage, int tid){
        this.kademliaid = kademliaid;
        this.lookupMessage = lookupMessage;
        messageSender = new MessageSender(kademliaid, tid);
    }

    @Override
    public void respond() {

        //If I have received this message from a node from a different domain -> check if I am an octopus
        if(!lookupMessage.sender.getDomain().getDomainId().equals(lookupMessage.receiver.getDomain().getDomainId())){
            if(lookupMessage.receiver.getRole().equals(Role.OCTOPUS)){  //todo: check of dit logisch is (moet ik per se octo zijn?)
                lookupMessage.receiver.getRoutingTable().addNeighbour((KadNode) lookupMessage.sender);
            }
        } else{
            lookupMessage.receiver.getRoutingTable().addNeighbour((KadNode) lookupMessage.sender);
        }

        // get the k closest nodes to target node
        ArrayList<KadNode> neighbours = lookupMessage.receiver.getRoutingTable().getNextHopCandidates((KadNode) lookupMessage.target, (KadNode) lookupMessage.receiver, (KadNode) lookupMessage.src);

        //get the BETA closest nodes from the neighbours
        ArrayList<KadNode> betaNeighbours = new ArrayList<>(neighbours.subList(0, KademliaCommonConfig.BETA+1));


        // create a response message containing the neighbours (with the same id as of the request)
        Message response = new Message(Message.MSG_RESPONSE, betaNeighbours);
        response.operationId = lookupMessage.operationId;
        response.src = lookupMessage.src;
        response.target = lookupMessage.target;
        response.receiver = lookupMessage.sender;
        response.sender = lookupMessage.receiver;
        response.newLookup = false;
        response.ackId = lookupMessage.msgId;
//        System.err.println("Do I have node from the target domain in my routing table? " + lookupMessage.receiver.getRoutingTable().containsNodeFromTargetDomain(lookupMessage.target.getDomain().getDomainId()));
//        System.err.println(" I am sending a RESPONSE message to (" + response.receiver.getNodeId() + "," + response.receiver.getDomain().getDomainId() +  ") of role " + response.receiver.getRole() + " with msgId is " + response.msgId);
//        System.err.println(" The beta neighbours are : ");
//        for (KadNode n : betaNeighbours){
//            System.err.print("(" + n.getNodeId() + ", " + n.getDomain().getDomainId() + ")");
//        }
//        System.err.println();
        messageSender.sendMessage(response);
    }
}
