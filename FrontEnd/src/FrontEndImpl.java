import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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
  public String returnItem(String userId, String itemID) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_RETURN_ITEM,
        userId);
    request.setItemId(itemID);
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
      System.out.println(reply);
      return reply;
    } catch (IOException e) {
      e.printStackTrace();
      return "Unsuccessful";
    }
  }

  @Override
  public String borrowItem(String userId, String itemID, int numberOfDays) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_BORROW_ITEM,
        itemID,
        userId, numberOfDays);
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
  public String addItem(String userId, String itemID, String itemName, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_ADD_ITEM, itemID,
        userId);
    request.setItemName(itemName);
    request.setQuantity(quantity);
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
  public String removeItem(String managerId, String itemId, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_REMOVE_ITEM,
        itemId, managerId);
    request.setQuantity(quantity);
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
  public String listItem(String managerId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_LIST_ITEM, managerId);
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
    //Validate and Return Response
  }

  @Override
  public String addUserInWaitingList(String userId, String itemId, int numberOfDays) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_ADD_USER_IN_WAITLIST,
        itemId, userId, numberOfDays);
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
  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_EXCHANGE_ITEM,
        oldItemId, userId);
    request.setNewItemId(newItemID);
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
  public String validateUserName(String userId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_VALIDATE_USER_NAME, userId);
    DatagramSocket socket;
    try {
      socket = new DatagramSocket();
      sendRequest(socket, request);

      ArrayList<String> replies = new ArrayList<>();
        byte[] requestBuffer = new byte[1000];
        DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
        socket.receive(requestReceived);
        String reply = new String(requestReceived.getData());
        reply = reply.trim();
        System.out.println(reply);
        replies.add(reply);
        return reply;
    } catch (IOException e) {
      e.printStackTrace();
      return "false";
    }
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
