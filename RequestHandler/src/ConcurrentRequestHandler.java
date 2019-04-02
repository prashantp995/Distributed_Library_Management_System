import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class ConcurrentRequestHandler extends Thread {

  RequestHandlerMain requestHandlerMain;
  DatagramPacket request;
  ByteArrayInputStream byteArrayInputStream;
  ObjectInputStream ois;
  byte updatedByteArray[];
  InetAddress ip;
  ByteArrayOutputStream byteArrayOutputStream;
  ObjectOutputStream oos;
  static ArrayList<ClientRequestModel> successfullyExecutedReq = new ArrayList<ClientRequestModel>();

  public ConcurrentRequestHandler(RequestHandlerMain requestHandlerMain,
      DatagramPacket requestReceived) {
    this.requestHandlerMain = requestHandlerMain;
    this.request = requestReceived;
  }

  @Override
  public void run() {
    try {
      ip = InetAddress.getByName("localhost");
      String responseString = "";
      byteArrayOutputStream = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(byteArrayOutputStream);
      byteArrayInputStream = new ByteArrayInputStream(request.getData());
      ois = new ObjectInputStream(byteArrayInputStream);
      //need to add sequence number in the client request
      ClientRequestModel objForRM = (ClientRequestModel) ois.readObject();
/*
      String replicaName = getReplicaNameFromPort(requestHandlerMain.requestHandlerPort);
*/
      String replicaName = RequestHandlerMain.replicaName;
      ServerInterface serverInterface = ServerFactory
          .getServerObject(replicaName,
              objForRM.getUserId().substring(0, 3));
      responseString = getResponse(objForRM, serverInterface);
      String[] responseArray = responseString.split(":");
      ResponseModel sendToFE = new ResponseModel();
      sendToFE.setClientId(objForRM.getUserId());
      sendToFE.setRequestId(objForRM.getRequestId());
      sendToFE.setResponse(responseArray[0]);
      sendToFE.setReplicaName(replicaName);
      DatagramSocket socket = new DatagramSocket();
      DatagramPacket response = new DatagramPacket(responseString.getBytes(),
          responseString.length(),
          request.getAddress(), objForRM.getFrontEndPort());
      System.out.println(responseString);
      socket.send(response);
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private String getResponse(ClientRequestModel objForRM,
      ServerInterface serverInterface) {
    String responseString = null;
    if (objForRM.getMethodName().equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {
      responseString = serverInterface.listItem(objForRM.getUserId());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
      responseString = serverInterface.validateUser(objForRM.getUserId());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {
      responseString = serverInterface
          .addItem(objForRM.getUserId(), objForRM.getItemId(), objForRM.getItemName(),
              objForRM.getQuantity());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {
      responseString = serverInterface
          .addUserInWaitingList(objForRM.getUserId(), objForRM.getItemId(),
              objForRM.getNumberOfDays());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {
      responseString = serverInterface
          .borrowItem(objForRM.getUserId(), objForRM.getItemId(),
              objForRM.getNumberOfDays());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {
      responseString = serverInterface
          .exchangeItem(objForRM.getUserId(), objForRM.getItemId(), objForRM.getNewItemId());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {
      responseString = serverInterface
          .removeItem(objForRM.getUserId(), objForRM.getItemId(), objForRM.getQuantity());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {
      responseString = serverInterface
          .returnItem(objForRM.getUserId(), objForRM.getItemId());
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {
      responseString = serverInterface.findItem(objForRM.getUserId(), objForRM.getItemName());
    }
    //TODO Add more conditions below based on response
    if (responseString != null && (responseString.contains(RequestHandlerConstants.SUCCESS)
        || responseString
        .contains(RequestHandlerConstants.TRUE))) {
      successfullyExecutedReq.add(objForRM);
    }
    return responseString;
  }

  public static String getReplicaNameFromPort(int port) {
    if (port == 9001) {
      return "Sarvesh";
    } else if (port == 9002) {
      return "Pras";
    } else if (port == 9003) {
      return "Shivam";
    } else if (port == 9004) {
      return "Rohit";
    }
    return null;
  }

  //TODO this method should be called from  ReplicaManager
  public void performOperationsToRecoverFromCrash(ArrayList<ClientRequestModel> requests) {
    //if this method is executed , means crash happened . need to reset successfullyExecutedReq and start performing operations again
    successfullyExecutedReq = new ArrayList<ClientRequestModel>();
    for (ClientRequestModel request : requests) {
      try {
        ServerInterface serverInterface = ServerFactory.getServerObject("Pras",
            request.getUserId().substring(0, 3));
        getResponse(request, serverInterface);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

  }
}
