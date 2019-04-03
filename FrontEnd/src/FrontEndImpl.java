import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.omg.CORBA.ORB;

public class FrontEndImpl extends LibraryServicePOA {

  InetSocketAddress sequencerAddress = new InetSocketAddress(9090);
  InetSocketAddress udpFrontEndAddress = new InetSocketAddress(9006);
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  @Override
  public String findItem(String userId, String itemName) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_FIND_ITEM, userId);
    request.setItemName(itemName);
    return returnResult(request);
  }

  @Override
  public String returnItem(String userId, String itemID) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_RETURN_ITEM,
        userId);
    request.setItemId(itemID);
    return returnResult(request);
  }

  @Override
  public String borrowItem(String userId, String itemID, int numberOfDays) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_BORROW_ITEM,
        itemID,
        userId, numberOfDays);
    return returnResult(request);
  }

  @Override
  public String addItem(String userId, String itemID, String itemName, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_ADD_ITEM, itemID,
        userId);
    request.setItemName(itemName);
    request.setQuantity(quantity);
    return returnResult(request);
  }

  @Override
  public String removeItem(String managerId, String itemId, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_REMOVE_ITEM,
        itemId, managerId);
    request.setQuantity(quantity);
    return returnResult(request);
  }

  @Override
  public String listItem(String managerId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_LIST_ITEM, managerId);
    return returnResult(request);
    //Validate and Return Response
  }

  private String returnResult(ClientRequestModel request) {
    DatagramSocket socket;
    try {
      socket = new DatagramSocket();
      sendRequest(socket, request);
      byte[] requestBuffer = new byte[1000];
      DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
      socket.receive(requestReceived);
      String reply = new String(requestReceived.getData());
      reply = reply.trim();
      System.out.println(reply);
      return reply;
    } catch (IOException e) {
      e.printStackTrace();
      return "Unsuccessful";
    }
  }

  @Override
  public String addUserInWaitingList(String userId, String itemId, int numberOfDays) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_ADD_USER_IN_WAITLIST,
        itemId, userId, numberOfDays);
    return returnResult(request);
  }

  @Override
  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_EXCHANGE_ITEM,
        oldItemId, userId);
    request.setNewItemId(newItemID);
    return returnResult(request);
  }

  @Override
  public String validateUserName(String userId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_VALIDATE_USER_NAME, userId);
    return returnResult(request);
  }

  @Override
  public String simulateSoftwareBug() {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_SIMULATE_SOFTWARE_BUG);
    return returnResult(request);
  }

  private void sendRequest(DatagramSocket socket, ClientRequestModel call) throws IOException {
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(bs);
    os.writeObject(call);
    os.close();
    bs.close();
    byte[] sendBuffer = bs.toByteArray();
    DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
        InetAddress.getByName("localhost"), udpFrontEndAddress.getPort());
    socket.send(sendPacket);
  }
}
