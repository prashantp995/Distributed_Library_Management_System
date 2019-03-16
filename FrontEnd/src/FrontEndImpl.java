import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import org.omg.CORBA.ORB;

public class FrontEndImpl extends LibraryServicePOA {

  InetSocketAddress sequencerAddress = new InetSocketAddress(9090);
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  @Override
  public String findItem(String userId, String itemName) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_FIND_ITEM, userId);
    request.setItemName(itemName);
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
    return "listItemCalled";
  }

  @Override
  public String addUserInWaitingList(String userId, String ItemId, int numberOfDays) {
    return null;
  }

  @Override
  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    return null;
  }

  private void sendRequest(ClientRequestModel call, DatagramSocket socket) throws IOException {

    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(bs);
    os.writeObject(call);
    os.close();
    bs.close();
    byte[] sendBuffer = bs.toByteArray();
    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
        sequencerAddress.getAddress(), sequencerAddress.getPort());
    socket.send(sendPacket);
  }
}
