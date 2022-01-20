package peersim.kademlia.FindOperations;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KadNode;
import peersim.kademlia.Message;
import peersim.kademlia.MessageSender;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class FindOperation2 {

    KadNode source;
    KadNode dest;
    int kademliaid;
    Message lookupMessage;
    LinkedHashMap<Long, FindOperation> findOperationsMap;
    int transportid;
    TreeMap<Long, Long> sentMsgTracker;
    MessageSender messageSender;
    FindOperation findOp;

    public abstract void find();

}
