import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

//TODO :Add more loggers
public class Client {

  private static boolean isConcordiaUser = false; //may not require in future
  private static boolean isMcGillUser = false;//may not require in future
  private static boolean isMonUser = false;//may not require in future
  private static boolean isConcordiaManager = false;
  private static boolean isMcGillManager = false;
  private static boolean isMonManager = false;
  private static Logger logger = null;
  private static LibraryService libraryService;

  public static void main(String args[])
      throws IOException {
    boolean valid = false;
    while (!valid) {
      System.out.println("Enter your username: ");
      Scanner scanner = new Scanner(System.in);
      String username = scanner.nextLine();
      if (ClientUtilities.validateUserName(username)) {
        valid = true;
        determineUniversity(username);
        logger = ClientUtilities
            .setupLogger(Logger.getLogger("UserLogger"), username + ".log", false);
        setupConnectionInfo();
        if (validateUser(username)) {
          performValidOperation(username);
        } else {
          System.out.println("User name is not valid" + username);
          System.exit(0);
        }


      } else {
        System.out.println("Given user Name is not in valid format");
      }
    }

  }

  private static boolean validateUser(String username) {
    logger
        .info("validation of " + username + "starts");
    String response = libraryService.validateUserName(username);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    if (response.contains("true")||response.contains("TRUE")) {
      return true;
    }
    return false;
  }

  /**
   * setup connection with FE
   */
  private static void setupConnectionInfo() {
    String[] serverInfo = getServerInfo();
    ORB orb = ORB.init(serverInfo, null);
    org.omg.CORBA.Object objRef = null;
    try {
      objRef = orb.resolve_initial_references("NameService");
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
      libraryService = LibraryServiceHelper
          .narrow(ncRef.resolve_str(getKeyForNamingService()));
    } catch (InvalidName invalidName) {
      invalidName.printStackTrace();
    } catch (CannotProceed cannotProceed) {
      cannotProceed.printStackTrace();
    } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
      invalidName.printStackTrace();
    } catch (NotFound notFound) {
      notFound.printStackTrace();
    }
  }

  private static void determineUniversity(String username) {
    if (username.startsWith("CON")) {
      isConcordiaUser = true;
      if (username.startsWith("CONM")) {
        isConcordiaManager = true;
      }
    } else if (username.startsWith("MCG")) {
      isMcGillUser = true;
      if (username.startsWith("MCGM")) {
        isMcGillManager = true;
      }
    } else if (username.startsWith("MON")) {
      isMonUser = true;
      if (username.startsWith("MONM")) {
        isMonManager = true;
      }
    }
  }

  private static void performValidOperation(String username) {
    boolean valid = false;
    if (isManagerUser()) {
      while (!valid) {
        System.out.println("Please select option from below");
        System.out.println(" 1  for addItem");
        System.out.println(" 2  for removeItem");
        System.out.println(" 3  for ListItemAvailability");
        System.out.println(" 4  to simulate Software bug handling");
        System.out.println(" 5  to simulate Crash");
        System.out.println(" 0  Exit");
        Scanner scanner = new Scanner(System.in);
        try {

          int choice;
          if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            if (choice == 4) {
              performMultiThreading();
            }
            if (choice == 0) {
              valid = true;
              ClientUtilities.closeLoggerHandlers(logger);
              System.exit(0);
              break;
            }
            if (choice == 1 || choice == 2 || choice == 3 || choice == 4 || choice == 5) {
              performManagerOperation(choice, username);
            } else {
              System.out.println("please enter valid choice");
            }
          } else {
            System.out.println("Please enter number only");

          }

        } catch (java.util.InputMismatchException e) {
          System.out.println("Please enter properInput");
        }
      }

    } else {
      while (!valid) {
        System.out.println("Please select option from below");
        System.out.println(" 1  for borrowItem");
        System.out.println(" 2  for findItem");
        System.out.println(" 3  for returnItem");
        System.out.println(" 4  for exchangeItem");
        System.out.println(" 0  Exit");
        Scanner scanner = new Scanner(System.in);
        try {

          int choice;
          if (scanner.hasNextInt()) {
            choice = scanner.nextInt();
            if (choice == 0) {
              valid = false;
              ClientUtilities.closeLoggerHandlers(logger);
              System.exit(0);
              break;
            }
            if (choice == 1 || choice == 2 || choice == 3 || choice == 4) {
              performOperation(choice, username);
            } else {
              System.out.println("please enter valid choice");
            }
          } else {
            System.out.println("Please enter number only");

          }

        } catch (java.util.InputMismatchException e) {
          System.out.println("Please enter properInput");
        }

      }
    }


  }

  private static void performManagerOperation(int choice, String username) {
    switch (choice) {
      case 1:
        System.out.println("perform Add item");
        logger.info(username + " choose to Add Item");
        performAddItem(username);
        break;
      case 2:
        System.out.println("perform Remove item");
        logger.info(username + " choose to Remove Item");
        performRemoveItem(username);
        break;
      case 3:
        System.out.println("perform List item");
        logger.info(username + " choose to List Item");
        performListItem(username);
        break;
      case 4:
        System.out.println("simulate Software Bug");
        logger.info(username + " choose to simulate software bug");
        simulateSoftwareBug(username);
      case 5:
        System.out.println("simulate Crash");
        logger.info(username + " choose to simulate software bug");
        simulateSoftwareBug(username);
      default:
        logger.info(username + " Entered invalid choice");
        System.out.println("please enter valid choice");
    }
  }

  private static String simulateSoftwareBug(String username) {
    System.out.println(
        "Simulate Software Bug will be called 3 times to get enough data to decide software bug happened ");
    String response = "";
    for (int i = 0; i < 3; i++) {
      response = libraryService.simulateSoftwareBug(username);
      logger.info("Response Received for counter" + i + " from the server is " + response);
      System.out.println("Response Received from the server is " + response);
    }
    return response;
  }

    private static String simulateCrash (String username){
        System.out.println("Please enter which replica to simulate crash:");
        Scanner scanner = new Scanner(System.in);
        String replicaName = scanner.nextLine();
        String response = libraryService.simulateCrash(username,replicaName);
        logger.info("Response Received from the server is " + response);
        System.out.println("Response Received from the server is " + response);
        return response;
    }

  private static void performAddItem(String username) {
    String itemId = getItemId();
    logger.info(username + " is trying to Add item with item id " + itemId);
    try {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Please Enter Item Name");
      String itemName = scanner.nextLine();
      System.out.println("Please Enter Quantity");
      int quantity = scanner.nextInt();
      getResponseOfAddItem(username, itemId, itemName, quantity);
    } catch (java.util.InputMismatchException e) {
      System.out.println("Please enter properInput");
    }


  }


  private static void performRemoveItem(String username) {
    String itemId = getItemId();
    logger.info(username + " is trying to remove " + itemId);
    try {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Please Enter Quantity");
      int quantity = scanner.nextInt();
      String response = getResponseFromRemoveItem(username, itemId, quantity);
    } catch (java.util.InputMismatchException e) {
      System.out.println("Please enter properInput");
    }
  }

  private static String getResponseFromRemoveItem(String username, String itemId,
      int quantity) {
    logger.info(
        username + " Requested to Remove Item " + itemId + "Quantity" + quantity);
    String response = libraryService.removeItem(username, itemId, quantity);
    System.out.println("Response Received from the server is " + response);
    logger.info("Response Received from the server is " + response);
    return response;
  }

  private static void performListItem(String username) {
    getResponseFromListItem(username);
  }

  private static String getResponseFromListItem(String username) {

    logger.info(
        username + " Requested to List Item ");
    String response = libraryService.listItem(username);
    System.out.println("Response Received from the server is " + response);
    logger.info("Response Received from the server is " + response);
    return response;
  }

  private static String getKeyForNamingService() {
    return ClientConstants.FRONT_END_REG;
  }

  private static String getResponseOfAddItem(String username, String itemId,
      String itemName, int quantity) {
    logger
        .info(username + " asked to Add item " + itemId + " " + itemName + " Quantity " + quantity);
    logger.info(
        username + " Requested to Add Item " + itemName);
    String response = libraryService.addItem(username, itemId, itemName, quantity);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    return response;
  }

  private static boolean isManagerUser() {
    return isMonManager || isConcordiaManager || isMcGillManager;
  }

  private static void performOperation(int choice, String username) {
    switch (choice) {
      case 1:
        System.out.println("perform borrow item");
        performBorrowItem(username);
        break;
      case 2:
        System.out.println("perform find item");
        performFindItem(username);
        break;
      case 3:
        System.out.println("perform return item");
        performReturnItem(username);
        break;
      case 4:
        System.out.println("perform exchange item");
        performExchangeItem(username);
        break;
      default:
        System.out.println("please enter valid choice");
    }
  }

  private static void performExchangeItem(String username) {
    System.out.println("Please enter item id of the book that you already borrowed");
    String oldItemID = getItemId();
    System.out.println("Please enter item id of the book that you want to get in exchange");
    String newItemID = getItemId();
    logger.info(username + " requested to exchange " + oldItemID + "with" + newItemID);
    getExchangeItemResponse(username, oldItemID, newItemID);
  }

  private static String getExchangeItemResponse(String username, String oldItemID,
      String newItemID) {
    String response = libraryService.exchangeItem(username, oldItemID, newItemID);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    return response;
  }

  private static void performReturnItem(String username) {
    boolean valid = false;
    String itemId = getItemId();
    logger.info(
        username + " Requested to Return Item " + itemId);
    String response = getReturnItemResponse(username, itemId);
    System.out.println(response);


  }


  private static void performFindItem(String username) {
    String itemName = getItemName();
    getItemFindResponse(username, itemName);

  }

  private static String getItemFindResponse(String username, String itemName) {
    logger.info(
        username + " Requested to Find Item " + itemName);
    String response = libraryService.findItem(username, itemName);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    return response;
  }

  private static String getReturnItemResponse(String username, String itemId) {
    logger.info(
        username + " Requested to Return Item " + itemId);
    String response = libraryService.returnItem(username, itemId);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    return response;
  }

  private static String getBorrowItemResponse(String itemId, int numberOfDays, String username) {
    logger.info(
        username + " Requested to Borrow Item " + itemId);
    String response = libraryService.borrowItem(username, itemId, numberOfDays);
    logger.info("Response Received from the server is " + response);
    System.out.println("Response Received from the server is " + response);
    if (response.equalsIgnoreCase("waitList")) {
      Scanner scanner = new Scanner(System.in);
      System.out.println(
          "Item is not available now , WaitList Possible, Do you wish to enroll your self in waitList "
              + "\n" +
              "Please enter 1 for yes" + "\n" +
              "Please enter 0 for No");
      int choice = scanner.nextInt();
      if (choice == 1) {
        logger.info(username + "is requesting to enroll in waitList of " + itemId);
        String waitListResponse = libraryService
            .addUserInWaitingList(username, itemId, numberOfDays);
        logger.info("Response regarding to waitList for " + itemId + " : " + username + " is "
            + waitListResponse);
        System.out.println("Response regarding to waitList for " + itemId + " " + username + " is "
            + waitListResponse);
      }
    }
    return response;
  }


  private static void performBorrowItem(String username) {
    boolean valid = false;
    String itemId;
    int numberOfDays;
    while (!valid) {
      Scanner scanner = new Scanner(System.in);
      itemId = getItemId();
      System.out.println("Please Enter number Of days");
      numberOfDays = scanner.nextInt();
      if (ClientUtilities.validateItemIdAndNumberOfDays(itemId, numberOfDays)) {
        valid = true;
        logger.info(
            username + " Requested to Borrow Item " + itemId + " for " + numberOfDays + " days.");
        getBorrowItemResponse(itemId, numberOfDays, username);
      } else {
        System.out.println("Please Enter Correct Details as specified");
      }

    }


  }


  private static String getItemId() {
    boolean valid = false;
    String itemId = null;
    while (!valid) {
      System.out.println("Please Enter  Item Id ");
      Scanner scanner = new Scanner(System.in);
      itemId = scanner.nextLine();
      if (validateItemId(itemId)) {
        valid = true;
      } else {
        System.out.println("Please enter Valid Id");
      }
    }
    return itemId;
  }

  private static String getItemName() {
    System.out.println("Please Enter  Item Name");
    Scanner scanner = new Scanner(System.in);
    String itemName = scanner.nextLine();
    return itemName;
  }

  private static String[] getServerInfo() {
    String serverInfo[] = new String[4];

    serverInfo[0] = "-ORBInitialHost";
    serverInfo[1] = "localhost";
    serverInfo[2] = "-ORBInitialPort";
    serverInfo[3] = "8090";

    return serverInfo;
  }

  public static boolean validateItemId(String itemId) {
    if (isManagerUser()) {
      return itemId.length() == 7 &&
          ((itemId.startsWith("CON") && isConcordiaManager) || (itemId
              .startsWith("MCG") && isMcGillManager) || (itemId
              .startsWith("MON") && isMonManager));
    }
    return itemId.length() == 7 && (itemId.startsWith("CON") || itemId.startsWith("MCG") || itemId
        .startsWith("MON"));
  }

  public static void performMultiThreading() {
    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        getResponseOfAddItem("CONM1111", "CON1015", "DSD", 1);
      }
    };

    Runnable runnable2 = new Runnable() {
      @Override
      public void run() {
        //getResponseOfAddItem("CONM1111", "CON1015", "DSD", 1);
        getResponseFromRemoveItem("CONM1111", "CON1015", 5);
        //getReturnItemResponse("CONU1111", "CON1012");
      }
    };
    Runnable runnable3 = new Runnable() {
      @Override
      public void run() {
        getReturnItemResponse("CONU1111", "CON1015");
      }
    };
    Thread thread = new Thread(runnable);
    Thread thread2 = new Thread(runnable2);
    Thread thread3 = new Thread(runnable3);
    thread.start();
    thread2.start();
    thread3.start();


  }
}
