import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_RETURN_ITEM,
        userId);
    request.setItemId(itemID);
    return null;
  }

  @Override
  public String borrowItem(String userId, String itemID, int numberOfDays) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_BORROW_ITEM,
        itemID,
        userId, numberOfDays);
    return null;
  }

  @Override
  public String addItem(String userId, String itemID, String itemName, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_ADD_ITEM, userId);
    request.setItemId(itemID);
    request.setItemName(itemName);
    request.setQuantity(quantity);
    return null;
  }

  @Override
  public String removeItem(String managerId, String itemId, int quantity) {
    ClientRequestModel request = new ClientRequestModel(FrontEndConstants.METHOD_RETURN_ITEM,
        managerId);
    request.setQuantity(quantity);
    return null;
  }

  @Override
  public String listItem(String managerId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_LIST_ITEM, managerId);
    DatagramSocket socket;
    try {
      socket = new DatagramSocket();
      sendRequest(socket,request);
      byte[] requestBuffer = new byte[1000];
      DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
      ArrayList<String> replies = new ArrayList<>();
      for(int i=0;i<3;i++){
        socket.receive(requestReceived);
        String reply = new String(requestReceived.getData());
        reply.trim();
        System.out.println(reply);
        replies.add(reply);
      }
      System.out.println(replies);
      return replies.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return "Unsuccessful";
    }
    //Validate and Return Response
  }

  @Override
  public String addUserInWaitingList(String userId, String ItemId, int numberOfDays) {
    return null;
  }

  @Override
  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    return null;
  }

  @Override
  public String validateUserName(String userId) {
    ClientRequestModel request = new ClientRequestModel(
        FrontEndConstants.METHOD_VALIDATE_USER_NAME, userId);
    DatagramSocket socket;
    try {
      socket = new DatagramSocket();
      sendRequest(socket,request);
      byte[] requestBuffer = new byte[1000];
      DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
      ArrayList<String> replies = new ArrayList<>();
      for(int i=0;i<3;i++){
        socket.receive(requestReceived);
        String reply = new String(requestReceived.getData());
        reply.trim();
        System.out.println(reply);
        replies.add(reply);
      }
      System.out.println(replies);
      return replies.toString();
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
        InetAddress.getByName("localhost"), sequencerAddress.getPort());
    socket.send(sendPacket);
  }
}
