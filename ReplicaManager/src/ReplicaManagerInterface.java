public interface ReplicaManagerInterface {

  //not creating dedicated constants file, Once replica manager implements this interface ,
  // it can access all constants
  String RES_WAIT_LIST_POSSIBLE = "waitList";
  String METHOD_ADD_ITEM = "addItem";
  String METHOD_REMOVE_ITEM = "removeItem";
  String METHOD_LIST_ITEM = "listItem";
  String METHOD_BORROW_ITEM = "borrowItem";
  String METHOD_EXCHANGE_ITEM = "exchangeItem";
  String METHOD_ADD_USER_IN_WAITLIST = "addUserInWaitList";
  String METHOD_RETURN_ITEM = "returnItem";
}
