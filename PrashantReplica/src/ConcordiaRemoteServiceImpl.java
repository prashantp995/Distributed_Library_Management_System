import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.omg.CORBA.ORB;

public class ConcordiaRemoteServiceImpl extends Thread implements ServerInterface {

  HashMap<String, LibraryModel> data = new HashMap<>();
  HashMap<String, ArrayList<String>> currentBorrowers = new HashMap<>();

  HashSet<String> managerId = new HashSet<>();
  HashSet<String> userIds = new HashSet<>();
  HashSet<String> completelyRemovedItems = new HashSet<String>();//removed items by Manager
  Logger logger = null;
  DatagramPacket packet;
  private static final String lib = LibConstants.CON_REG;
  private ORB orb;
  static ConcordiaRemoteServiceImpl exportedObj;
  private byte[] buf;
  private DatagramSocket socket;


  public ConcordiaRemoteServiceImpl(DatagramPacket packet, byte[] buf,
      DatagramSocket socket) {
    this.packet = packet;
    this.buf = buf;
    this.socket = socket;
  }

  protected ConcordiaRemoteServiceImpl(Logger logger) {
    super();
    initManagerID();
    initUserID();
    data.put("CON0001", new LibraryModel("DSD", 5));
    data.put("CON0002", new LibraryModel("ALGO", 0));
    this.logger = logger;
    this.logger.info("Valid Manager Ids" + managerId.toString());
    this.logger.info("Valid User Ids" + userIds.toString());


  }


  public void setORB(ORB orb_val) {
    orb = orb_val;
  }

  private void initManagerID() {
    managerId.add("CONM0001");
  }

  private void initUserID() {
    userIds.add("CONU0001");
    userIds.add("CONU0002");
  }


  public String findItem(String userId, String itemName) {
    logger.info(userId + "requested to find item" + itemName);
    String itemDetails;
    if (!isValidUser(userId)) {
      logger.info(userId + "is not valid/authorized to  find Item" + itemName);
      return userId + "is not valid/authorized to  find Item" + itemName;
    } else {
      logger.info(userId + " is valid");
      itemDetails = findItem(itemName, true);
      if (itemDetails.length() == 0) {
        logger.info(itemName + " Not Found In Concordia Server requested by " + userId);
        return "No item found in Concordia Server";
      }
    }
    return itemDetails;
  }


  public String returnItem(String userId, String itemID) {
    if (!isValidUser(userId)) {
      logger.info(userId + "is not present/authorised");
      return userId + "is not present/authorised";
    }
    String response = null;
    if (data.containsKey(itemID) || completelyRemovedItems.contains(itemID)) {
      response = performReturnItemOperation(userId, itemID, false);
      logger.info("Return Item Response is " + response);

    } else {
      logger.info(itemID + " belongs to external server");
      response = performReturnItemOperation(userId, itemID, true);
      if (response.equalsIgnoreCase(LibConstants.SUCCESS)) {
        logger.info("return item request for" + itemID + " by " + userId + "Approved");
        System.out.println("External Server Approved Return Item");
        removeFromCurrentBorrowers(userId, itemID);
      }
    }
    return response;
  }


  public String borrowItem(String userId, String itemID, int numberOfDays) {
    logger.info(userId + " has asked to borrow item " + itemID);
    if (!isValidUser(userId)) {
      logger.info(userId + "is not present/authorised");
      return userId + "is not present/authorised";
    }
    String x = validateBorrow(userId, itemID);
    if (!x.equalsIgnoreCase(LibConstants.SUCCESS)) {
      logger.info("Validation of the borrow item " + itemID + " by " + userId + " failed ");
      return x;
    }
    StringBuilder response = new StringBuilder();
    String result = performBorrowItemOperation(itemID, userId,
        numberOfDays);//this will fail if itemID is not in this server
    //if fails then we need to connect to Respective External Server to Borrow Item
    if (result.equals(LibConstants.FAIL)) {
      logger.info(
          "Calling ExternalUDP Servers To Borrow " + itemID + " For " + userId);
      String res = borrowItemFromExternalServer(userId, itemID, numberOfDays);
      response.append(res);
      if (res.equals(LibConstants.WAIT_LIST_POSSIBLE)) {
        return res;
      } else if (res.equalsIgnoreCase(LibConstants.SUCCESS)) {
        addOrUpdateInCurrentBorrowers(userId, itemID);//external server approved borrow item
        return LibConstants.SUCCESS;
      }


    } else if (result.equalsIgnoreCase(LibConstants.WAIT_LIST_POSSIBLE)) {
      return result;
    }

    return result;
  }

  public String validateBorrow(String userId, String itemID) {
    if (currentBorrowers.containsKey(userId)) {
      synchronized (currentBorrowers) {
        logger.info("Current Borrower" + userId + "Borrowed Item " + currentBorrowers.get(userId)
            .toString());
        for (String borrowedItem : currentBorrowers.get(userId)) {
          if (!borrowedItem.startsWith("CON") && !itemID.startsWith("CON")) {
            if (borrowedItem.startsWith("MON") && itemID.startsWith("MON")) {
              logger.info("validation for borrowItem Fails " +
                  userId + " Can not borrow more than one item from each of  external library");
              return LibConstants.FAIL
                  + "Can not borrow more than one item from each of  external library";
            }
            if (borrowedItem.startsWith("MCG") && itemID.startsWith("MCG")) {
              logger.info("validation for borrowItem Fails " +
                  userId + " Can not borrow more than one item from each of  external library");
              return LibConstants.FAIL
                  + "Can not borrow more than one item from each of  external library";
            }

          }

        }
      }

    }

    if (itemID.startsWith("CON") && !data.containsKey(itemID)) {
      logger.info(userId + " Can not borrow , Item id is unknown to Library  " + itemID);
      return LibConstants.FAIL + " Can not borrow , Item id is unknown to Library";
    }
    if (data.containsKey(itemID) && data.get(itemID).getCurrentBorrowerList().contains(userId)) {
      logger.info("user already borrowed item ");
      return LibConstants.FAIL + " user already borrowed item";
    }
    return LibConstants.SUCCESS;
  }


  public String addItem(String userId, String itemID, String itemName, int quantity) {
    logger.info("Add item is called on Concordia server by " + userId + " for " + itemID + " for "
        + quantity + " name " + itemName);
    StringBuilder response = new StringBuilder();
    if (!isValidManager(userId)) {
      logger.info(userId + "is not present/authorised");
      return "Item Add Fails,User is not Present/Authorised";
    }
    if (!data.containsKey(itemID)) {
      logger.info("Item id is not in existing database, Adding as new Item");
      LibraryModel libraryModel = new LibraryModel(itemName, quantity);
      data.put(itemID, libraryModel);
      handleAlreadyRemovedItems(itemID);
      response.append("Item Add Success");
    } else {
      logger.info("Item id   exist in  database, modifying  Item");
      if (validateItemIdAndName(itemID, itemName)) {
        logger.info("Item id and Item name matches");
        LibraryModel libraryModel = data.get(itemID);
        logger.info("previous item details" + libraryModel);
        int previousQuantity = libraryModel.getQuantity();
        libraryModel.setQuantity(previousQuantity + quantity);
        data.put(itemID, libraryModel);
        logger.info("updated item details" + libraryModel);
        if (previousQuantity == 0) {
          logger.info("previous quantity was 0 , now need to process waiting list");
          processWaitingListIfPossible(itemID);
        }
        handleAlreadyRemovedItems(itemID);
        response.append("Item add success, Quantity updated");
      } else {
        response.append("Item Add Fails , Item Id and Name Does not match");
      }
    }
    return response.toString();
  }


  public String removeItem(String managerId, String itemId, int quantity) {
    logger.info(
        "Remove item is called on Concordia server by " + managerId + " for " + itemId + " for "
            + quantity);
    StringBuilder response = new StringBuilder();
    if (!isValidManager(managerId)) {
      logger.info(managerId + "is not Present/Authorised");
      return "Item Remove Fails,User is not Present/Authorised";
    }
    if (!data.containsKey(itemId)) {
      response.append("Remove Item Fails , Item id is not present in database");
    } else {
      if (quantity <= 0 || data.get(itemId).getQuantity() - quantity == 0) {
        removeItemCompletely(itemId);
        logger.info("Remove Item Success , " + itemId + " Removed Completely by " + managerId);
        response
            .append("Remove Item Success , " + itemId + " Removed Completely by " + managerId);
      } else if (data.get(itemId).getQuantity() - quantity < 0) {
        logger.info(
            "Quantity Provided by " + managerId + " is not correct , removing " + quantity + "from "
                + itemId + "will result in negative Quantity");
        response.append("Please Provide Correct Quantity");
      } else {
        updateQuantity(itemId, quantity);
        logger.info("Remove Item Success , " + itemId + " Updated the Quantity by  " + managerId);
        response
            .append("Remove Item Success , " + itemId + " Updated the Quantity by  " + managerId);
      }
    }
    return response.toString();
  }


  public String listItem(String managerId) {
    logger.info(managerId + "Requested to View Data");
    if (!isValidManager(managerId)) {
      logger.info(managerId + "is not registered");
      return "ManagerId is not registered";
    }
    return getData(data);
  }


  public String addUserInWaitingList(String userId, String ItemId, int numberOfDays) {
    return addUserInWaitList(ItemId, userId, numberOfDays, !ItemId.startsWith("CON"));

  }


  public String exchangeItem(String userId, String oldItemId, String newItemID) {
    String oldItemId_Lib = ServerUtils.determineLibOfItem(oldItemId);
    String newItemId_Lib = ServerUtils.determineLibOfItem(newItemID);
    if (newItemId_Lib.equalsIgnoreCase(lib)) {
      String validateBorrowForLocalUser = validateBorrow(userId, newItemID);
      if (!validateBorrowForLocalUser.equalsIgnoreCase(LibConstants.SUCCESS)) {
        return validateBorrowForLocalUser;
      }
    }
    if (oldItemId_Lib != null && newItemId_Lib != null) {
      if (oldItemId_Lib.equals(LibConstants.CON_REG) && newItemId_Lib
          .equals(LibConstants.CON_REG)) {
        return performExchange(userId, oldItemId, newItemID, false, oldItemId_Lib, newItemId_Lib);
      } else {
        return performExchange(userId, oldItemId, newItemID, true, oldItemId_Lib, newItemId_Lib);
      }
    }

    return LibConstants.FAIL;
  }

  @Override
  public String validateUser(String userId) {
    return "true";
  }

  /**
   * This function is to perform exchange
   *
   * @param userId user id
   * @param oldItemId item that user has already borrowed.
   * @param newItemID item that user wishes to borrow
   * @param callExternalServer true if any of old/new  or both Item id is out of home server
   */
  private String performExchange(String userId, String oldItemId, String newItemID,
      boolean callExternalServer, String oldItemId_Lib, String newItemId_Lib) {
    if (!callExternalServer) {
      if (data.containsKey(oldItemId)) {
        if (!data.get(oldItemId).getCurrentBorrowerList().contains(userId)) {
          logger.info("Exchange item is not valid");
          return oldItemId + " is not borrowed by user " + userId
              + "Can not perform exchange operation";
        } else if (data.get(newItemID).getCurrentBorrowerList().contains(userId)) {
          logger.info("Exchange item is not valid");
          return newItemID + " is already borrowed by user " + userId
              + "Can not perform exchange operation ";
        } else {
          logger.info("Exchange item is valid");
          if (data.containsKey(newItemID) && data.get(newItemID).getQuantity() > 0) {
            logger.info("Old item id and New item id both belongs to Concordia Server");
            boolean isValidBorrow = isItemAvailableToBorrow(newItemID, userId, 0);
            boolean isValidReturn = isValidReturn(userId, data.get(oldItemId));
            logger.info(
                "validation Result For Borrow " + isValidBorrow + " \n For Return" + isValidReturn);
            synchronized (data) {
              if (isValidBorrow && isValidReturn) {
                returnItem(userId, oldItemId);
                borrowItem(userId, newItemID, 2);
                return LibConstants.SUCCESS;
              }
            }

          } else {
            String result =
                newItemID + " is not recognized by library or enough quantity not available";
            logger.info(result);
            return result;
          }
        }
      }
    } else {
      logger.info("Need to connect to external server ....");
      if (oldItemId_Lib.equals(lib)) {
        logger.info(newItemID + "belongs to external server");
        boolean isValidReturn = isValidReturn(userId, data.get(oldItemId));
        logger.info("Verifying" + newItemID + " is is available to borrow In " + newItemId_Lib);
        String isValidBorrow = ServerUtils
            .validateBorrowOnExternalServer(userId, newItemID, logger);
        logger.info("Response received from " + newItemId_Lib + " is " + isValidBorrow);
        if (isValidReturn && isValidBorrow.equalsIgnoreCase("true")) {
          logger.info("validation successful");
          returnItem(userId, oldItemId);
          borrowItem(userId, newItemID, 2);
          return LibConstants.SUCCESS;
        } else {
          return LibConstants.FAIL;
        }

      } else if (newItemId_Lib.equals(lib)) {
        logger.info(oldItemId + "Belongs to external server");
        boolean isValidBorrow = isItemAvailableToBorrow(newItemID, userId, 0);
        String isValidReturn = ServerUtils
            .validateReturnOnExternalServer(userId, oldItemId, logger);
        if (isValidBorrow && isValidReturn.equalsIgnoreCase("true")) {
          returnItem(userId, oldItemId);
          borrowItem(userId, newItemID, 2);
          return LibConstants.SUCCESS;
        } else {
          return LibConstants.FAIL;
        }

      } else if (!oldItemId_Lib.equals(lib) && !newItemId_Lib
          .equals(lib)) {
        logger.info("both item id belongs to external server");
        String isValidReturn = ServerUtils
            .validateReturnOnExternalServer(userId, oldItemId, logger);
        String isValidBorrow = ServerUtils
            .validateBorrowOnExternalServer(userId, newItemID, logger);
        if (isValidBorrow.equalsIgnoreCase("true") && isValidReturn.equalsIgnoreCase("true")) {
          returnItem(userId, oldItemId);
          borrowItem(userId, newItemID, 2);
          return LibConstants.SUCCESS;
        } else {
          return LibConstants.FAIL;
        }
      }

    }
    return LibConstants.FAIL;
  }

  public String simulateSoftwareBug(String username) {
    if (RequestHandlerMain.isSimulateSoftwareBug()) {
      System.out.println("Sending Correct result");
      return RequestHandlerConstants.CORRECT;
    } else {
      //alternative implementation in case of software bug
      System.out.println("Sending Buggy result");
      return RequestHandlerConstants.BUGGY;
    }
  }

  @Override
  public String simulateCrash(String username,String replicaName) {
    if(replicaName.equalsIgnoreCase("pras")){
      if (!RequestHandlerMain.isSimulateCrash("pras")) {
        RequestHandlerMain.setSimulateCrash(!RequestHandlerMain.isSimulateCrash(replicaName),replicaName);
        return RequestHandlerConstants.CRASH;
      } else {
        //alternative implementation in case of software bug
        return RequestHandlerConstants.RECOVER;
      }
    }
    else {
      return "alive";
    }
  }

  private boolean isValidReturn(String userId, LibraryModel model) {
    if (model == null) {
      return false;
    }
    return model.getCurrentBorrowerList() != null && model.getCurrentBorrowerList()
        .contains(userId);
  }

  public boolean isValidReturn(String userId, String itemId) {
    logger.info("is valid return ? checking for" + itemId);
    return isValidReturn(userId, data.get(itemId));
  }

  private String getData(HashMap<String, LibraryModel> data) {
    ArrayList<LibraryModel> response = new ArrayList<>();
    for (Entry<String, LibraryModel> letterEntry : data.entrySet()) {
      LibraryModel libraryModel = letterEntry.getValue();
      libraryModel.setItemId(letterEntry.getKey());
      response.add(libraryModel);
    }
    return response.toString();
  }

  private boolean validateItemIdAndName(String itemID, String itemName) {
    for (Entry<String, LibraryModel> letterEntry : data.entrySet()) {
      String id = letterEntry.getKey();
      if (id.equals(itemID)) {
        LibraryModel libraryModel = letterEntry.getValue();
        return libraryModel.getItemName().equals(itemName);

      }

    }
    return false;
  }

  private synchronized void updateQuantity(String itemId, int quantity) {
    LibraryModel libraryModel = data.get(itemId);
    int previousQuantity = libraryModel.getQuantity();
    libraryModel.setQuantity(previousQuantity - quantity);
    logger.info("updating existing item " + libraryModel);
    data.put(itemId, libraryModel);
  }

  private synchronized void removeItemCompletely(String itemId) {
    LibraryModel libraryModel = data.get(itemId);
    logger.info("removing" + libraryModel);
    if (libraryModel.getCurrentBorrowerList() != null
        && libraryModel.getCurrentBorrowerList().size() > 0) {
      logger
          .info("Manager is trying to remove item completely but item is already assigned to"
              + libraryModel.getCurrentBorrowerList().toString());
      completelyRemovedItems.add(itemId);

    }
    data.remove(itemId);
  }


  public synchronized String performBorrowItemOperation(String itemID, String userId,
      int numberOfDays) {
    if (isItemAvailableToBorrow(itemID, userId, numberOfDays)) {
      LibraryModel libraryModel = data.get(itemID);
      libraryModel.getCurrentBorrowerList().add(userId);
      libraryModel.setQuantity(libraryModel.getQuantity() - 1);
      data.put(itemID, libraryModel);
      addOrUpdateInCurrentBorrowers(userId, itemID);
      return LibConstants.SUCCESS;
    } else if (isUserInWaitList(itemID, userId)) {
      logger.info(userId + " is already in waiting list of " + itemID);
      return LibConstants.ALREADY_WAITING_LIST;
    } else if (isWaitListPossible(itemID, userId)) {
      logger.info(userId + " can register for the waiting list of " + itemID);
      return LibConstants.WAIT_LIST_POSSIBLE;

    }
    return LibConstants.FAIL;
  }

  /**
   * check if wait list possible by looking at quantity and user has not borrowed that item already
   */
  private boolean isWaitListPossible(String itemID, String userId) {
    if (data.containsKey(itemID) && data.get(itemID).getQuantity() == 0 && !data.get(itemID)
        .getCurrentBorrowerList().contains(userId)) {
      logger.info(userId + " can register for waiting list of " + itemID);
      return true;
    }
    return false;
  }

  private boolean isUserInWaitList(String itemID, String userId) {
    if (data.containsKey(itemID) && data.get(itemID).getWaitingList() != null && data.get(itemID)
        .getWaitingList().contains(userId)) {
      logger.info(userId + " is already in waiting list of " + itemID);
      return true;
    }
    return false;
  }

  public boolean isItemAvailableToBorrow(String itemID, String userId, int numberOfDays) {
    if (data.containsKey(itemID)) {
      if (data.get(itemID).getQuantity() > 0) {
        //check if user has already borrowed the item
        return !(currentBorrowers.containsKey(userId) && currentBorrowers.get(userId)
            .contains(itemID));
      }
    }
    return false;
  }

  private boolean isValidManager(String managerId) {
    return this.managerId.contains(managerId);
  }

  /**
   * Once item is return by user , remove user from the current borrower list
   *
   * @param userId userId
   * @param itemID itemID
   */
  private synchronized void removeFromCurrentBorrowers(String userId, String itemID) {
    if (currentBorrowers.containsKey(userId)) {
      ArrayList<String> borrowedItems = currentBorrowers.get(userId);
      borrowedItems.remove(itemID);
      currentBorrowers.put(userId, borrowedItems);
    }
  }

  private void addOrUpdateInCurrentBorrowers(String userId, String itemID) {
    if (!currentBorrowers.containsKey(userId)) {
      ArrayList<String> itemBorrowed = new ArrayList<>();
      itemBorrowed.add(itemID);
      currentBorrowers.put(userId, itemBorrowed);
    } else {
      currentBorrowers.get(userId).add(itemID);
    }
    logger.info("updated current borrower List is  " + currentBorrowers.toString());
  }

  private void processWaitingListIfPossible(String itemID) {
    if (data.containsKey(itemID)) {
      logger.info("Now attempting to process wait list");
      synchronized (data) {
        while (data.get(itemID).waitingList.size() > 0) {
          String waitlistUser = data.get(itemID).getWaitingList().get(0);
          if (data.get(itemID).getQuantity() > 0) {
            logger.info("Waiting List Found For The Item Id " + itemID);
            String res = isUsereligibleToGetbook(waitlistUser, itemID,
                true);
            if (res.equalsIgnoreCase(LibConstants.FAIL)) {
              logger.info(
                  waitlistUser + "Already got one thing out of library , can not assign "
                      + itemID);
              data.get(itemID).getWaitingList().remove(waitlistUser);//remove from waiting list
              continue;
            } else {
              data.get(itemID).getCurrentBorrowerList().add(waitlistUser);//add in borrower list
              data.get(itemID).getWaitingList().remove(waitlistUser);//remove from waiting list
              data.get(itemID).setQuantity(data.get(itemID).getQuantity() - 1);
              addOrUpdateInCurrentBorrowers(waitlistUser, itemID);
              logger.info(itemID + "  is assigned to " + waitlistUser);
            }
          }
        }


      }

    }
  }

  public String isUsereligibleToGetbook(String firstUserInWaitingList, String itemId,
      boolean callExternalServers) {

    if (callExternalServers && isOtherLibraryUser(firstUserInWaitingList)) {
      UdpRequestModel requestModel = new UdpRequestModel();
      requestModel.setMethodName(LibConstants.USER_BORROWED_ITEMS);
      requestModel.setUserId(firstUserInWaitingList);
      requestModel.setItemId(itemId);
      String response = ServerUtils.callUDPServer(requestModel, LibConstants.UDP_MON_PORT, logger);
      String response2 = ServerUtils.callUDPServer(requestModel, LibConstants.UDP_MCG_PORT, logger);
      System.out.println("reseponse received" + response + "response received 2" + response2);
      if (response.equalsIgnoreCase(LibConstants.FAIL) || response2
          .equalsIgnoreCase(LibConstants.FAIL)) {
        return LibConstants.FAIL;
      }
    } else {

      if (currentBorrowers.containsKey(firstUserInWaitingList)) {
        synchronized (currentBorrowers) {
          if (isOtherLibraryUser(firstUserInWaitingList)) {
            for (String borrowedItem : currentBorrowers.get(firstUserInWaitingList)) {
              if (borrowedItem.startsWith("CON")) {
                return LibConstants.FAIL;
              }

            }
          }
          for (String borrowedItem : currentBorrowers.get(firstUserInWaitingList)) {
            if (!borrowedItem.startsWith("CON") && !itemId.startsWith("CON")) {
              return LibConstants.FAIL;
            }

          }
        }

      }
    }
    return LibConstants.SUCCESS;
  }

  private boolean isOtherLibraryUser(String firstUserInWaitingList) {
    return !isValidUser(firstUserInWaitingList);
  }


  public String findItem(String itemName, boolean callExternalServers) {
    StringBuilder response = new StringBuilder();
    for (Entry<String, LibraryModel> letterEntry : data.entrySet()) {
      if (letterEntry.getValue().getItemName().equals(itemName)) {
        LibraryModel libraryModel = new LibraryModel(letterEntry.getKey(), itemName,
            letterEntry.getValue().getQuantity());
        response.append(libraryModel.toString());
      }
    }
    if (callExternalServers) {
      UdpRequestModel udpRequestModel = new UdpRequestModel("findItem", itemName);
      String montrealServerResponse = ServerUtils
          .callUDPServer(udpRequestModel, LibConstants.UDP_MON_PORT, logger);

      String mcgServerResponse = ServerUtils
          .callUDPServer(udpRequestModel, LibConstants.UDP_MCG_PORT, logger);
      if (mcgServerResponse != null && mcgServerResponse.length() > 0) {
        logger.info("Response Received from McGill Server Is" + mcgServerResponse
            + " for requested item " + itemName);
        if (!mcgServerResponse.equalsIgnoreCase("No Data Found")) {
          response.append(mcgServerResponse);
        }
      }
      if (montrealServerResponse != null && montrealServerResponse.length() > 0) {
        logger.info("Response Received from Montreal Server Is" + montrealServerResponse
            + " for requested item " + itemName);
        if (!montrealServerResponse.equalsIgnoreCase("No Data Found")) {
          response.append(montrealServerResponse);
        }

      }

    }

    return response.toString();
  }


  private boolean isValidUser(String userId) {
    return userIds.contains(userId);
  }

  /**
   * If user want to enroll him/her self in the wait list then this function will be called
   *
   * @param itemID itemID
   * @param externalServerCallRequire true if itemid belongs to non home library
   */
  public String addUserInWaitList(String itemID, String userId, int numberOfDays,
      boolean externalServerCallRequire) {
    String response = null;
    if (externalServerCallRequire) {
      int port = ServerUtils.getPortFromItemId(itemID);
      UdpRequestModel udpRequestModel = new UdpRequestModel(LibConstants.OPR_WAIT_LIST, itemID,
          numberOfDays, userId);

      if (port != 0) {
        response = ServerUtils
            .callUDPServer(udpRequestModel, port, logger);
      } else {
        response = "invalid item id";
      }
    } else {
      synchronized (data) {
        LibraryModel libraryModel = data.get(itemID);
        libraryModel.getWaitingList().add(userId);
        data.put(itemID, libraryModel);
        logger.info(userId + " has been added in waiting list of " + itemID);
        response = LibConstants.SUCCESS;
      }
    }
    return response;
  }

  public String performReturnItemOperation(String userId, String itemID,
      boolean callExternalServer) {
    String response = null;
    if (callExternalServer) {
      UdpRequestModel udpRequestModel = new UdpRequestModel("returnItem", itemID, userId);
      int port = ServerUtils.getPortFromItemId(itemID);
      if (port != 0) {
        response = ServerUtils.callUDPServer(udpRequestModel, port, logger);
      }
      return response;
    } else {
      if (completelyRemovedItems.contains(itemID)) {
        logger.info(userId + " is trying to return item which is removed from library by manager");
        //we do not update the data in this case , simply accept the return request from user
        return LibConstants.SUCCESS;
      }
      if (data.containsKey(itemID)) {
        LibraryModel model = data.get(itemID);
        if (model.getCurrentBorrowerList() != null && !model.getCurrentBorrowerList()
            .contains(userId)) {
          return "item not borrowed";
        }
        if (model.getCurrentBorrowerList() != null && model.getCurrentBorrowerList()
            .contains(userId)) {
          synchronized (data) {
            model.getCurrentBorrowerList().remove(userId);
            int previousQuantity = model.getQuantity();
            model.setQuantity(model.getQuantity() + 1);
            data.put(itemID, model);
            removeFromCurrentBorrowers(userId, itemID);
            if (previousQuantity == 0) {
              logger.info("Previous Quantity for the item " + itemID
                  + " was 0 ,Now processing waitinglist");
              processWaitingListIfPossible(itemID);
            }

            return LibConstants.SUCCESS;
          }
        }
      } else {
        return "item not found";
      }
      return LibConstants.FAIL;
    }
  }

  /**
   * Borrow  item from the external server.
   */
  private String borrowItemFromExternalServer(String userId, String itemID, int numberOfDays) {
    UdpRequestModel udpRequestModel = new UdpRequestModel("borrowItem", itemID, numberOfDays,
        userId);
    String response = null;
    int port = ServerUtils.getPortFromItemId(itemID);
    if (port != 0) {
      response = ServerUtils
          .callUDPServer(udpRequestModel, port, logger);
    } else {
      response = "invalid item id";
    }
    return response;


  }

  private void handleAlreadyRemovedItems(String itemID) {
    if (completelyRemovedItems.contains(itemID)) {
      logger
          .info(itemID
              + " was removed completely by manager in past, removing item id from the records of removed item ");
      synchronized (completelyRemovedItems) {
        completelyRemovedItems
            .remove(
                itemID);// remove from the set as Now the Item is added by the Manager , so that users can borrow/return it
      }

    }
  }

  public static ConcordiaRemoteServiceImpl getConcordiaObject() {
    if (exportedObj == null) {
      Logger logger = null;
      try {
        logger = ServerUtils
            .setupLogger(Logger.getLogger("CONServerlog"), "ConcordiaServer.log",
                true);
        exportedObj = new ConcordiaRemoteServiceImpl(logger);
        main(null);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        return exportedObj;
      }

    }
    return exportedObj;
  }

  public static void main(String args[]) throws IOException {
    getConcordiaObject().start();


  }

  @Override
  public void run() {
    DatagramSocket socket = null;
    try {
      socket = new DatagramSocket(LibConstants.UDP_CON_PORT);
    } catch (SocketException e) {
      e.printStackTrace();
    }

    byte[] buf = new byte[1000];
    boolean running = true;
    System.out.println("UDP Server is listening on port" + LibConstants.UDP_CON_PORT);
    while (running) {
      DatagramPacket packet
          = new DatagramPacket(buf, buf.length);
      try {

       /* ConcordiaRemoteServiceImpl concordiaRemoteService = new ConcordiaRemoteServiceImpl(packet,
            buf, socket);
        concordiaRemoteService.start();*/
        this.packet = packet;
        this.buf = buf;
        this.socket = socket;
        socket.receive(packet);
        InetAddress address = this.packet.getAddress();
        int port = this.packet.getPort();
        String received
            = new String(packet.getData(), 0, packet.getLength());
        byte[] data = packet.getData();
        DatagramPacket reponsePacket = null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = null;
        try {
          is = new ObjectInputStream(in);
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {

          assert is != null;
          UdpRequestModel request = (UdpRequestModel) is.readObject();
          ConcordiaRemoteServiceImpl.getConcordiaObject().logger
              .info(request.getMethodName() + " is called by " + address + ":" + port);
          String response = null;
          reponsePacket = getDatagramPacket(reponsePacket, address, port, request, response,
              ConcordiaRemoteServiceImpl.getConcordiaObject(),
              ConcordiaRemoteServiceImpl.getConcordiaObject().logger);
          ConcordiaRemoteServiceImpl.getConcordiaObject().logger
              .info("sending response " + reponsePacket.getData().toString());

        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          socket.send(reponsePacket);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


  }


  private static synchronized DatagramPacket getDatagramPacket(DatagramPacket reponsePacket,
      InetAddress address,
      int port, UdpRequestModel request, String response, ConcordiaRemoteServiceImpl exportedObj,
      Logger logger) {
    if (request.getMethodName().equalsIgnoreCase("findItem")) {
      response = getFindItemResponse(request, exportedObj, logger);
    } else if (request.getMethodName().equalsIgnoreCase("borrowItem")) {
      response = getBorrowItemResponse(request, exportedObj, logger);
    } else if (request.getMethodName().equalsIgnoreCase(LibConstants.OPR_WAIT_LIST)) {
      response = getWaitListResponse(request, exportedObj, logger);
    } else if (request.getMethodName().equalsIgnoreCase("returnItem")) {
      response = getReturnItemResponse(request, exportedObj, logger);
    } else if (request.getMethodName().equalsIgnoreCase(LibConstants.USER_BORROWED_ITEMS)) {
      response = exportedObj
          .isUsereligibleToGetbook(request.getUserId(), request.getItemId(), false);
    } else if (request.getMethodName().equalsIgnoreCase("validateBorrow")) {
      response = String.valueOf(
          exportedObj.isItemAvailableToBorrow(request.getItemId(), request.getUserId(),
              request.getNumberOfDays()));
    } else if (request.getMethodName().equalsIgnoreCase("validateReturn")) {
      response = String.valueOf(
          exportedObj.isValidReturn(request.getUserId(), request.getItemId()));
    }
    System.out.println("Response to send from udp is " + response);

    if (response != null && response.length() > 0) {
      reponsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
          address, port);
    } else {
      String noResponse = "No Data Found";
      reponsePacket = new DatagramPacket(noResponse.getBytes(), noResponse.getBytes().length,
          address, port);
    }
    return reponsePacket;
  }

  private static String getReturnItemResponse(UdpRequestModel request,
      ConcordiaRemoteServiceImpl exportedObj, Logger logger) {
    String response;
    logger.info(
        "Return item is Called :   Item is " + request.getItemId() + " User " + request
            .getUserId());
    response = exportedObj
        .performReturnItemOperation(request.getUserId(), request.getItemId(), false);
    logger.info("Response is" + response);
    return response;
  }

  private static String getWaitListResponse(UdpRequestModel request,
      ConcordiaRemoteServiceImpl exportedObj, Logger logger) {
    String response;
    logger.info(
        "User Selected to be in Wait List For item" + request.getItemId() + " User " + request
            .getUserId());
    response = exportedObj
        .addUserInWaitList(request.getItemId(), request.getUserId(), request.getNumberOfDays(),
            false);
    logger.info("Response is" + response);
    return response;
  }

  private static String getBorrowItemResponse(UdpRequestModel request,
      ConcordiaRemoteServiceImpl exportedObj, Logger logger) {
    String response;
    logger.info(
        "Borrow item is Called : Requested Item is " + request.getItemId() + " User " + request
            .getUserId() + " for days " + request.getNumberOfDays());

    response = exportedObj.performBorrowItemOperation(request.getItemId(), request.getUserId(),
        request.getNumberOfDays());

    logger.info("Response is" + response);
    return response;
  }

  private static String getFindItemResponse(UdpRequestModel request,
      ConcordiaRemoteServiceImpl exportedObj, Logger logger) {
    String response;
    logger.info("FindItem is Called : Requested Item is " + request.getItemName());
    response = exportedObj.findItem(request.getItemName(), false);
    logger.info("Response is  " + response);
    return response;
  }

}
