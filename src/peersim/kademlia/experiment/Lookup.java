package peersim.kademlia.experiment;

import peersim.kademlia.FindOperation;
import peersim.kademlia.KademliaNode;
import peersim.kademlia.Message;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public abstract class Lookup {
    String type;
    FindOperation2 findOp;
    RespondOperation2 resOp;
    HandleResponseOperation2 handleResOp;
    KademliaNode sender;
    KademliaNode receiver;

    //todo: removed the folder experiment and make everything here protected again (so remove public"
    abstract void prepare(KademliaNode sender, KademliaNode destination, int kademliaid, Message lookupMessage, LinkedHashMap<Long, FindOperation> findOpsMap, TreeMap<Long,Long> sentMsg, int tid);
    public abstract void performFindOp();
    public abstract void performRespondOp();
    public abstract void performHandleResponseOp();

    void setType(String s){
        this.type = s;
    }


    void setSender(KademliaNode s){
        this.sender = s;
    }

    void setReiver(KademliaNode r){
        this.receiver = r;
    }


}
