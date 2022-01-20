package peersim.kademlia;

import peersim.kademlia.FindOperations.FindOperation2;
import peersim.kademlia.HandleResponseOperation.HandleResponseOperation2;
import peersim.kademlia.RespondOperations.RespondOperation2;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class Lookup {
    String type;
    FindOperation2 findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;
    Message lookupMessage;
    KademliaNode source;
    KademliaNode destination;
    int kademliaid;
    int transportid;
    LinkedHashMap<Long, FindOperation> findOperationsMap;
    TreeMap<Long,Long> sentMsgTracker;

    abstract void prepare(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
    abstract void performFindOp();
    abstract void performRespondOp();
    abstract void performHandleResponseOp();

    void setType(String s){
        this.type = s;
    }




}
