
/**
* LibraryServiceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from LibraryServicesInterface.idl
* Wednesday, April 3, 2019 10:48:34 o'clock AM EDT
*/

public interface LibraryServiceOperations 
{
  String findItem (String userId, String itemName);
  String returnItem (String userId, String itemID);
  String borrowItem (String userId, String itemID, int numberOfDays);
  String addItem (String userId, String itemID, String itemName, int quantity);
  String removeItem (String managerId, String itemId, int quantity);
  String listItem (String managerId);
  String addUserInWaitingList (String userId, String ItemId, int numberOfDays);
  String exchangeItem (String userId, String oldItemId, String newItemID);
  String validateUserName (String userId);
  String simulateSoftwareBug ();
} // interface LibraryServiceOperations
