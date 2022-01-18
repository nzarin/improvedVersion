package peersim.kademlia.experiment;

public interface LookupIngredientFactory2 {
     FindOperation2 createFindOperation();
     RespondOperation2 createRespondOperation();
     HandleResponseOperation2 createHandleResponseOperation();
}
