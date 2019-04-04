import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
/*
      String replicaName = getReplicaNameFromPort(requestHandlerMain.requestHandlerPort);
*/
      // String replicaName = "Sarvesh";
      ServerInterface serverInterface = ServerFactory
          .getServerObject(RequestHandlerMain.replicaName,
              objForRM.getUserId().substring(0, 3));
      responseString = getResponse(objForRM, serverInterface);
      String[] responseArray = responseString.split(":");
      ResponseModel sendToFE = new ResponseModel();
      sendToFE.setClientId(objForRM.getUserId());
      sendToFE.setRequestId(objForRM.getRequestId());
      sendToFE.setResponse(responseArray[0]);
      sendToFE.setStatus(responseArray[1]);
      sendToFE.setReplicaName(RequestHandlerMain.replicaName);
      byte[] dataToSend = getByteArrayOfObj(sendToFE);
      DatagramSocket socket = new DatagramSocket();
      DatagramPacket response = new DatagramPacket(dataToSend,
          dataToSend.length,
          request.getAddress(), objForRM.getFrontEndPort());
      System.out.println(responseString);
      socket.send(response);
    } catch (ClassNotFoundException | IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private byte[] getByteArrayOfObj(ResponseModel sendToFE) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(sendToFE);
    oos.flush();
    return bos.toByteArray();
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
    } else if (objForRM.getMethodName()
        .equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {
      responseString = serverInterface.simulateSoftwareBug(objForRM.getUserId());
    }
    responseString = responseString.trim();
    responseString = appendStatus(objForRM.getMethodName(), responseString,
        RequestHandlerMain.replicaName);
    return responseString;
  }

  private String appendStatus(String methodName, String responseString, String replicaName) {
    if (replicaName.equalsIgnoreCase("pras")) {
      return appendStatusPras(methodName, responseString);
    } else if (replicaName.equalsIgnoreCase("Rohit")) {
      return appendStatusRohit(methodName, responseString);
    } else if (replicaName.equalsIgnoreCase("Shivam")) {
      return appendStatusShivam(methodName, responseString);
    } else if (replicaName.equalsIgnoreCase("Sarvesh")) {
      return appendStatusSarversh(methodName, responseString);
    }
    return ":";
  }

  private String appendStatusSarversh(String methodName, String responseString) {
    if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {

    }
    return responseString;
  }

  private String appendStatusShivam(String methodName, String responseString) {
    if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {
        return responseString+RequestHandlerConstants.RES_APPEND_SUCCESS;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
        if(responseString.startsWith("TRUE")||responseString.startsWith("true")){
          return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }else
          return RequestHandlerConstants.RES_FALSE_FAILURE;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {
        if(responseString.startsWith("invalid itemId")){
          return RequestHandlerConstants.RES_ITEMID_NOT_VALID;
        }else if(responseString.startsWith("invalid itemName")){
          return RequestHandlerConstants.RES_ITEM_NAME_ERROR;
        }else
          return RequestHandlerConstants.RES_TRUE_SUCCESS;

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {
        if(responseString.startsWith("User already in waitlist")){
          return RequestHandlerConstants.RES_ALREADY_IN_WAIT_LIST;
        }else if(responseString.startsWith("waitlist")){
          return RequestHandlerConstants.RES_WAIT_LIST_POSSIBLE;
        }else{
          return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {

    }
    return responseString;
  }

  private String appendStatusRohit(String methodName, String responseString) {
    if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {

    }
    return responseString;
  }

  private String appendStatusPras(String methodName, String responseString) {
    if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {

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
}
