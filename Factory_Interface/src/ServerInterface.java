public interface ServerInterface {

  String findItem(String userId, String itemName);

  String returnItem(String userId, String itemID);

  String borrowItem(String userId, String itemID, int numberOfDays);

  String addItem(String userId, String itemID, String itemName, int quantity);

  String removeItem(String managerId, String itemId, int quantity);

  String listItem(String managerId);

  String addUserInWaitingList(String userId, String ItemId, int numberOfDays);

  String exchangeItem(String userId, String oldItemId, String newItemID);

  String validateUser(String userId);

  String simulateSoftwareBug(String username);

  String simulateCrash(String username,String replicaName);

}
