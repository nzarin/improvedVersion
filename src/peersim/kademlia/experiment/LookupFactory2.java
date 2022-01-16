package peersim.kademlia.experiment;

public interface LookupFactory2 {
    public FindOperation2 createFindOperation();
    public RespondOperation2 createRespondOperation();
    public HandleResponseOperation2 createHandleResponseOperation();

}
