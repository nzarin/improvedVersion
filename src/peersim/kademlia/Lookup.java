package peersim.kademlia;

public interface Lookup {

    void find();
    void respond();
    void handleResponse();


}
