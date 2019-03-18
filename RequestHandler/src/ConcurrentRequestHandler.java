import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConcurrentRequestHandler extends Thread {

  RequestHandlerMain requestHandlerMain;
  DatagramPacket request;
  ByteArrayInputStream byteArrayInputStream;
  ObjectInputStream ois;
  byte updatedByteArray[];
  InetAddress ip;
  ByteArrayOutputStream byteArrayOutputStream;
  ObjectOutputStream oos;

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
      String replicaName = getReplicaNameFromPort(requestHandlerMain.requestHandlerPort);
      ServerInterface serverInterface = ServerFactory
          .getServerObject(replicaName,
              objForRM.getUserId().substring(0, 3));
      if (objForRM.getMethodName().equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {
        responseString = serverInterface.listItem(objForRM.getUserId());
      }
      if (objForRM.getMethodName().equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
        responseString = serverInterface.validateUser(objForRM.getUserId());
      }
      String[] responseArray = responseString.split(":");
      ResponseModel sendToFE = new ResponseModel();
      sendToFE.setClientId(objForRM.getUserId());
      sendToFE.setRequestId(objForRM.getRequestId());
      sendToFE.setResponse(responseArray[0]);
      sendToFE.setStatus(responseArray[1]);
      sendToFE.setNote(responseArray[2]);


      DatagramSocket socket = new DatagramSocket();
      DatagramPacket response = new DatagramPacket(responseString.getBytes(),responseString.length(),
              request.getAddress(),objForRM.getFrontEndPort());
      socket.send(response);
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }

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
}
