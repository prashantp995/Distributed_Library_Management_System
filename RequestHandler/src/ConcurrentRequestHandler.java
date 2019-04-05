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
  static ArrayList<ClientRequestModel> successfullyExecutedReq = new ArrayList<>();

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
      ServerInterface serverInterface = ServerFactory
          .getServerObject(RequestHandlerMain.replicaName,
              objForRM.getUserId().substring(0, 3));
      responseString = getResponse(objForRM, serverInterface);
      System.out.println("Response String is " + responseString);
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
    //TODO Add more conditions below based on response
    if (responseString != null && (responseString.contains(RequestHandlerConstants.SUCCESS)
        || responseString
        .contains(RequestHandlerConstants.TRUE))) {
      successfullyExecutedReq.add(objForRM);
    }
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
        return responseString;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
        if (responseString.toLowerCase().contains("true")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        } else if (responseString.toLowerCase().contains("false")) {
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {
        if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }else if(responseString.toLowerCase().contains("item id and name does not match")){
            return RequestHandlerConstants.RES_ITEM_NAME_ERROR;
        }else{
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {
        if (responseString.toLowerCase().contains("failure")) {
            return RequestHandlerConstants.RES_ALREADY_IN_WAIT_LIST;
        } else if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        } else {
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {
        if (responseString.toLowerCase().contains("external library")) {
            return RequestHandlerConstants.RES_FOREIGN_LIB_ERROR;
        } else if (responseString.equalsIgnoreCase("waitlist")) {
            return RequestHandlerConstants.RES_WAITLIST_POSSIBLE;
        } else if (responseString.toLowerCase().contains("already borrowed")) {
            return RequestHandlerConstants.RES_ITEM_ALREADY_BORROWED;
        } else if (responseString.toLowerCase().contains("Item not found")) {
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        } else if (responseString.toLowerCase().contains("failure")) {
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        } else if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {
        if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        } else {
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {
        if (responseString.toLowerCase().contains("in inventory")) {
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        } else if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        } else if (responseString.toLowerCase().contains("incorrectqunatity")) {
            return RequestHandlerConstants.RES_INCORRECT_QUANTITY_ERROR;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {
        if (responseString.toLowerCase().contains("success")) {
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }  else if (responseString.toLowerCase().contains("Item not available")) {
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        } else if (responseString.toLowerCase().contains("not borrowed item")) {
            return RequestHandlerConstants.RES_ITEM_NOT_BORROWED;
        } else {
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {
        return responseString;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {
        return appendForSFBug(responseString);
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
        if(responseString.startsWith("Invalid itemId")){
            return RequestHandlerConstants.RES_ITEMID_NOT_VALID;
        }else if (responseString.startsWith("Can not borrow the same book again")){
            return RequestHandlerConstants.RES_ITEM_ALREADY_BORROWED;
        }else if (responseString.startsWith("you can not get two books from a foreign library")){
            return RequestHandlerConstants.RES_FOREIGN_LIB_ERROR;
        }else{
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {
        if(responseString.startsWith("Success")){
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }else{
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {
        if(responseString.startsWith("Item not present in the Library")){
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        }else if(responseString.startsWith("Incorrect quantity")){
            return RequestHandlerConstants.RES_INCORRECT_QUANTITY_ERROR;
        }else if(responseString.startsWith("Success")){
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }


    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {
        if(responseString.startsWith("failure")){
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        }else if(responseString.startsWith("You cannot submit this book")){
            return RequestHandlerConstants.RES_ITEM_NOT_BORROWED;
        }else{
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {
        if(responseString.startsWith("Item not found")){
            return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
        }else{
            return responseString+RequestHandlerConstants.RES_APPEND_SUCCESS;
        }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {
        if(responseString.startsWith("TRUE")){
            return RequestHandlerConstants.RES_TRUE_SUCCESS;
        }else{
            return RequestHandlerConstants.RES_FALSE_FAILURE;
        }
    }
    return responseString;
  }

    private String appendStatusRohit(String methodName, String responseString) {
        if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {
            return responseString+RequestHandlerConstants.RES_APPEND_SUCCESS;
        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
            if (responseString.toLowerCase().contains("validTrue User")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else{
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }

        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {
            if (responseString.toLowerCase().contains("already listed")||(responseString.toLowerCase().contains("increased"))){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("the item has been issued")||responseString.toLowerCase().contains("waitlist  for the item the remaining")){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("of the item has removed the item:")){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("The  value entered is invalid")){
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }

        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {
            if(responseString.toLowerCase().contains("already in the waitlist")){
                return RequestHandlerConstants.RES_ALREADY_IN_WAIT_LIST;
            }
            else if(responseString.toLowerCase().contains("sucessfully waitlisted")){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("Invalid response")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {
            if(responseString.toLowerCase().contains("already have a copy")){
                return RequestHandlerConstants.RES_ITEM_NOT_BORROWED;
            }
            else if(responseString.toLowerCase().contains("successfully borrowed")){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("like to be added to the waitlist?")){
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("Internal data error!")){
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("already borrowed an item from an outside library")){
                return RequestHandlerConstants.RES_FOREIGN_LIB_ERROR;
            }
            else if(responseString.toLowerCase().contains("no suitable server for this item")){
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("User is not authorized for this action")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }

        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {
            if(responseString.toLowerCase().contains("borrow is currently not available we cannot process the exchange")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("book you want to return in the exchange was never officially take under your ID")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("please return the foreign library's book first")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("Exchange Successful")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }

        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {

            if(responseString.toLowerCase().contains("item has not been listed in the library")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("All the copies are being recalled")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("manager of the item has removed the item:")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("been removed from the unborrowed section")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("entered value is more than the availablity")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("User is not authorized for this action")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {
            if(responseString.toLowerCase().contains("you don't have a copy of this item")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("successfully returned")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("added to the library")) {
                return RequestHandlerConstants.RES_TRUE_SUCCESS;
            }
            else if(responseString.toLowerCase().contains("ID does not exist in the library")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }

            else if(responseString.toLowerCase().contains("no suitable server for this item")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
            else if(responseString.toLowerCase().contains("he User is not authorized for this actio")) {
                return RequestHandlerConstants.RES_FALSE_FAILURE;
            }
        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {
            return responseString + RequestHandlerConstants.RES_APPEND_SUCCESS;

        }

        else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {
            return appendForSFBug(responseString);
        }
        return responseString;
    }

  private String appendStatusPras(String methodName, String responseString) {
    if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_LIST_ITEM)) {
      return responseString + RequestHandlerConstants.RES_APPEND_SUCCESS;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_VALIDATE_USER_NAME)) {
      if (responseString.toLowerCase().contains("true")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else if (responseString.toLowerCase().contains("false")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_ITEM)) {
      if (responseString.toLowerCase().contains("item add success") || responseString.toLowerCase()
          .contains("quantity updated")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else if (responseString.toLowerCase().contains("item fails")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      } else if (responseString.toLowerCase().contains("fail")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_ADD_USER_IN_WAITLIST)) {
      if (responseString.toLowerCase().contains("Already in Waiting")) {
        return RequestHandlerConstants.RES_ALREADY_IN_WAIT_LIST;
      } else if (responseString.toLowerCase().contains("success")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else if (responseString.toLowerCase().contains("fail")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_BORROW_ITEM)) {
      if (responseString.toLowerCase().contains("external library")) {
        return RequestHandlerConstants.RES_FOREIGN_LIB_ERROR;
      } else if (responseString.equalsIgnoreCase("waitlist")) {
        return RequestHandlerConstants.RES_WAITLIST_POSSIBLE;
      } else if (responseString.toLowerCase().contains("already borrowed")) {
        return RequestHandlerConstants.RES_ITEM_ALREADY_BORROWED;
      } else if (responseString.toLowerCase().contains("unknown to library")) {
        return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
      } else if (responseString.toLowerCase().contains("fail")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      } else if (responseString.toLowerCase().contains("success")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_EXCHANGE_ITEM)) {
      if (responseString.toLowerCase().contains("success")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_REMOVE_ITEM)) {
      if (responseString.toLowerCase().contains("in database")) {
        return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
      } else if (responseString.toLowerCase().contains("success")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else if (responseString.toLowerCase().contains("correct quantity")) {
        return RequestHandlerConstants.RES_INCORRECT_QUANTITY_ERROR;
      }
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_RETURN_ITEM)) {
      if (responseString.toLowerCase().contains("success")) {
        return RequestHandlerConstants.RES_TRUE_SUCCESS;
      } else if (responseString.toLowerCase().contains("fail")) {
        return RequestHandlerConstants.RES_FALSE_FAILURE;
      } else if (responseString.toLowerCase().contains("item not found")) {
        return RequestHandlerConstants.RES_ITEM_NOT_EROOR;
      } else if (responseString.toLowerCase().contains("item not borrowed")) {
        return RequestHandlerConstants.RES_ITEM_NOT_BORROWED;
      }

    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_FIND_ITEM)) {
      return responseString + RequestHandlerConstants.RES_APPEND_SUCCESS;
    } else if (methodName.equalsIgnoreCase(RequestHandlerConstants.METHOD_SIMULATE_SOFTWARE_BUG)) {

    }

    return responseString;
  }

    private String appendForSFBug(String responseString) {
        if (responseString.trim().equalsIgnoreCase(RequestHandlerConstants.BUGGY)) {
            return responseString + RequestHandlerConstants.RES_APPEND_FAILURE;
        } else {
            return responseString + RequestHandlerConstants.RES_APPEND_SUCCESS;
        }
    }

    public void performOperationsToRecoverFromCrash(ArrayList<ClientRequestModel> requests) {
        //if this method is executed , means crash happened . need to reset successfullyExecutedReq and start performing operations again
        successfullyExecutedReq = new ArrayList<ClientRequestModel>();
        for (ClientRequestModel request : requests) {
            try {
                ServerInterface serverInterface = ServerFactory
                        .getServerObject(RequestHandlerMain.replicaName,
                                request.getUserId().substring(0, 3));
                getResponse(request, serverInterface);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

