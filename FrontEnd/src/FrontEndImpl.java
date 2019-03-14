import org.omg.CORBA.ORB;

public class FrontEndImpl extends LibraryServicePOA {

  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  @Override
  public String findItem(String userId, String itemName) {
    return null;
  }

  @Override
  public String returnItem(String userId, String itemID) {
    return null;
  }

  @Override
  public String borrowItem(String userId, String itemID, int numberOfDays) {
    return null;
  }

  @Override
  public String addItem(String userId, String itemID, String itemName, int quantity) {
    return null;
  }

  @Override
  public String removeItem(String managerId, String itemId, int quantity) {
    return null;
  }

  @Override
  public String listItem(String managerId) {
    return null;
  }

  @Override
  public String addUserInWaitingList(String userId, String ItemId, int numberOfDays) {
    return null;
  }

  @Override
  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    return null;
  }
}
