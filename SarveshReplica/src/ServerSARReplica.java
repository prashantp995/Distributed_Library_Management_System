
import java.io.*;
import java.net.*;
import java.util.*;


enum ServerDetails{

    CONCORDIA("CON",1301,0),
    MCGILL("MCG",1302,1),
    MONTREAL("MON",1303,2);

    private final String library;
    private final int port;
    private final int index;

    ServerDetails(String library,int port,int index){
        this.library = library;
        this.port = port;
        this.index = index;
    }

    public String getLibrary() {
        return library;
    }

    public int getPort() {
        return port;
    }

    public int getIndex() {
        return index;
    }
}


public class ServerSARReplica implements ServerInterface{

    protected final HashMap<String, User> user;
    protected final HashMap<String,Manager> manager;
    protected final HashMap<String, Item> item;
    protected final HashMap<User,HashMap<Item,Integer>> borrow;
    protected final HashMap<String, HashMap<String,Integer>> waitingQueue;
    protected String library;
    private Integer next_User_ID;
    private Integer next_Manager_ID;
    private File logFile;
    private PrintWriter logger;
    private final Object lock;



    public ServerSARReplica(String library){

        this.library = library;
        user = new HashMap<>();
        manager = new HashMap<>();
        item = new HashMap<>();
        borrow = new HashMap<>();
        waitingQueue = new HashMap<>();
        next_User_ID = 1003;
        next_Manager_ID = 1002;
        lock = new Object();
        logFile = new File("/home/sarvesh/IdeaProjects/Distributed_Library_Management_System_new/SarveshReplica/src/Logs/"
                +library+"_Log.log");
        try{
            if(!logFile.exists())
                logFile.createNewFile();
            logger = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
        }catch (IOException io){
            System.out.println("Error in creating log file.");
            io.printStackTrace();
        }
        writeToLogFile("Server " + library + " Started.");
        init();
    }

    private void init(){
        String initManagerID = library + "M" + "0001";
        Manager initManager = new Manager(initManagerID);
        manager.put(initManagerID,initManager);
        writeToLogFile("Initial manager created.");
        String initUserID1001 = library + "U" + "0001";
        String initUserID1002 = library + "U" + "0002";
        User initUser1001 = new User(initUserID1001);
        User initUser1002 = new User(initUserID1002);
        user.put(initUserID1001,initUser1001);
        user.put(initUserID1002,initUser1002);
        writeToLogFile("Initial users created.");
        String initItemID1001 = library + "0001";
        String initItemID1002 = library + "0002";
        Item initItem1001 = new Item(initItemID1001,"DSD",5);
        Item initItem1002 = new Item(initItemID1002,"ALGO",0);
        item.put(initItemID1001,initItem1001);
        item.put(initItemID1002,initItem1002);
        writeToLogFile("Initial items created.");
    }

    /**used to find the given item name in all library and return the itemID and availability.*/
    public String findItem(String userID, String itemName) {
        String reply = "";
        Iterator<Map.Entry<String, Item>> iterator;
        synchronized (lock) {
            iterator = item.entrySet().iterator();
        }
        Item itemList;
        while(iterator.hasNext()){
            Map.Entry<String, Item> pair = iterator.next();
            if(pair.getValue().getItemName().equals(itemName)){
                itemList = pair.getValue();
                reply += itemList.toString();
            }
        }
        reply += findAtOtherLibrary(itemName);
        reply += ServerConstants.SUCCESS;
        writeToLogFile(reply);
        return reply;
    }

    /**It shows all the available books in that library to the manager.*/
    public String listItem(String managerID) {
        String reply;
        ArrayList<Item> itemList = new ArrayList<>(item.values());
        reply = itemList.toString();
        reply += ServerConstants.SUCCESS;
        writeToLogFile(reply);
        return reply;
    }

    /**used to return the item borrowed by the user. If user has not borrowed any
     * book return unsuccessful note. Returned book will be automatically given*/
    public String returnItem(String userID, String itemID) {
        User currentUser;
        String message ="";
        currentUser = user.get(userID);
        if(!itemID.substring(0,3).equals(library)){
            message = returnToOtherLibrary(userID,itemID);
            if(message.contains("failure")){
                message = "Item not available.";
            }
            return message;
        }
        if(borrow.containsKey(currentUser)){
            HashMap<Item,Integer> set = borrow.get(currentUser);
            Iterator<Map.Entry<Item,Integer>> value;
            synchronized (lock){ value = set.entrySet().iterator(); }
            if(!value.hasNext()){
                message ="You have not borrowed item."+ServerConstants.FAILURE;
                writeToLogFile(message);
                return message;
            }
            while(value.hasNext()) {
                Map.Entry<Item, Integer> pair = value.next();
                if(pair.getKey().getItemID().equals(itemID)){
                    synchronized (lock){
                        borrow.get(currentUser).remove(pair.getKey());
                    }
                    increamentItemCount(itemID);
                    System.out.println("--------------------------------Library " + library);
                    System.out.println("---------------------------------before auto assignment." + waitingQueue);
                    automaticAssignmentOfBooks(itemID);
                    System.out.println("---------------------------------after Autoassignment." + waitingQueue);
                    message = " Successful" + ServerConstants.SUCCESS;
                    writeToLogFile(message);
                    return message;
                }
            }
        }
        message = "Item have never been borrowed"+ServerConstants.FAILURE;
        writeToLogFile(message);
        return message;
    }


    public String borrowItem(String userID, String itemID, int numberOfDays) {
        User currentUser = user.get(userID);
        String reply = "";
        if(!item.containsKey(itemID)){
            reply = borrowFromOtherLibrary(userID,itemID,numberOfDays);
            if(reply.contains("failure"))
                reply = "Item not found";
            writeToLogFile(reply);
            return reply;
        }

        Item requestedItem;
        requestedItem = item.get(itemID);
        if(requestedItem.getItemCount() == 0){
            return "waitList" + ServerConstants.SUCCESS;
        }else {
            HashMap<Item,Integer> entry;
            if (borrow.containsKey(currentUser)) {
                if (borrow.get(currentUser).containsKey(requestedItem)) {
                    reply = "User have already borrowed." + ServerConstants.FAILURE;
                    writeToLogFile(reply);
                    return reply;
                } else {
                    entry = borrow.get(currentUser);
                    synchronized (lock){ borrow.remove(currentUser);}
                }
            } else {
                entry = new HashMap<>();
            }
            entry.put(requestedItem, numberOfDays);
            synchronized (lock) {
                borrow.put(currentUser, entry);
            }
            decrementItemCount(itemID);
            reply = "Successful" + ServerConstants.SUCCESS;
            writeToLogFile(reply);
            return reply;
        }
    }

    /**String addItem(String managerID, String itemID,String itemName, int quantity)*/
    public String addItem(String managerID, String itemID, String itemName, int quantity) {
        String message;
        Item currentItem;
        if(authenticateItemID(itemID)){
            if(item.containsKey(itemID)) {
                currentItem = item.get(itemID);
                if (currentItem.getItemName().equals(itemName)) {
                    currentItem.setItemCount(currentItem.getItemCount() + quantity);
                    synchronized (lock) {
                        item.remove(itemID);
                        item.put(itemID, currentItem);
                    }
                    message = "Successful" + ServerConstants.SUCCESS;
                    automaticAssignmentOfBooks(itemID);
                }else{
                    message = "itemidanditemnamedoesnotmatch";
                }
            }else {
                currentItem = new Item(itemID, itemName, quantity);
                synchronized (lock) {
                    item.put(itemID, currentItem);
                }
                message = "Successful" + ServerConstants.SUCCESS;
                automaticAssignmentOfBooks(itemID);
            }
            writeToLogFile(message);
            return message;
        }
        message =  "Unsuccessful" + ServerConstants.FAILURE;
        writeToLogFile(message);
        return message;
    }

    /**used by the manager to remove the item completely or decrease the quantity of it.*/
    public String removeItem(String managerID, String itemID, int quantity) {
        String message;

        if(!item.containsKey(itemID)){
            message = "The item do not exist in inventory." + ServerConstants.FAILURE;
            writeToLogFile(message);
            return message;
        }
        Item currentItem = item.get(itemID);

        if(quantity < 0){
            item.remove(itemID);
            waitingQueue.remove(currentItem);
            for (Map.Entry<User, HashMap<Item, Integer>> pair : borrow.entrySet()) {
                if (pair.getValue().containsKey(currentItem)) {
                    borrow.get(pair.getKey()).remove(currentItem);
                }
            }
            message = "Successful" + ServerConstants.SUCCESS;
            writeToLogFile(message);
            return message;
        }else if(currentItem.getItemCount() < quantity){
            message = "incorrectqunatity" + ServerConstants.FAILURE;
            writeToLogFile(message);
            return message;
        }else if(currentItem.getItemCount() > quantity){
            currentItem.setItemCount(currentItem.getItemCount() - quantity);
            synchronized (lock){
                item.remove(itemID);
                item.put(itemID,currentItem);
            }
            message  = "Successful " + ServerConstants.SUCCESS;
            writeToLogFile(message);
            return message;
        }else{
            synchronized (lock){item.remove(itemID);}
            message = "Successful" + ServerConstants.SUCCESS;
            writeToLogFile(message);
            return message;
        }
    }

    /**It adds the given userID to the waiting list for given itemID with numberOfDays.*/
    public String addUserInWaitingList(String userID, String itemID, int numberOfDays) {
        if(item.containsKey(itemID)) {
            if (waitingQueue.containsKey(itemID)) {
                if(!waitingQueue.get(itemID).containsKey(userID)){
                    waitingQueue.get(itemID).put(userID, numberOfDays);
                    writeToLogFile("Successful"+ServerConstants.SUCCESS + " " + itemID + " " + userID);
                    return "Successful"+ServerConstants.SUCCESS;
                }
                else{
                    writeToLogFile("Unsuccessful"+ServerConstants.FAILURE + " " + itemID + " " + userID);
                    return "Unsuccessful"+ServerConstants.FAILURE;
                }
            } else {
                HashMap<String, Integer> userList = new HashMap<>();
                userList.put(userID, numberOfDays);
                waitingQueue.put(itemID, userList);
                writeToLogFile("Successful"+ServerConstants.SUCCESS + " " + itemID + " " + userID);
                return "Successful"+ServerConstants.SUCCESS;
            }
        }else{
            String message = addToForeignWaitlist(userID,itemID,numberOfDays);
            writeToLogFile(message);
            return message;
        }
    }

    public String addToForeignWaitlist(String userID, String itemID, int numberOfDays){
        String reply = "";
        try{
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            int port1 = -1;
            if (itemID.substring(0,3).equals("CON")){
                port1 = 1301;
            }
            if (itemID.substring(0,3).equals("MCG")){
                port1 = 1302;
            }
            if (itemID.substring(0,3).equals("MON")){
                port1 = 1303;
            }
            String request = library+":addToWaitlist:"+userID+":"+itemID+":"+numberOfDays;
            DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,port1);
            mySocket.send(sendRequest);
            byte[] receive = new byte[1024];
            DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
            mySocket.receive(receivedReply);
            reply = new String(receivedReply.getData()).trim();
            writeToLogFile(reply);
            return reply;
        }catch (SocketException e){
            writeToLogFile("Socket Exception");
            System.out.println("Socket Exception.");
            e.printStackTrace();
            return " ";
        }catch (UnknownHostException e){
            writeToLogFile("Unknown host Exception");
            System.out.println("Unknown host Exception.");
            e.printStackTrace();
            return " ";
        }catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
            return " ";
        }
    }

    /**validate the client*/
    public String validateUser(String clientID) {
        if(clientID == null){
            writeToLogFile("Validate clientID request : clientID : "+ clientID);
            return "false"+ ServerConstants.FAILURE;
        }
        else if(clientID.charAt(3) == 'U'){
            String message;
            if(!user.containsKey(clientID)){
                writeToLogFile("Client Validate:Unsuccessful:Invalid UserID " + clientID);
                return "false" + ServerConstants.FAILURE + "Sarvesh";
            }else{
                writeToLogFile("Validate clientID request : clientID : "+ clientID +" Status : Successful");
                message = "true" + ServerConstants.SUCCESS + "Sarvesh";
                return message;
            }
        }
        else if(clientID.charAt(3) == 'M'){
            if(!manager.containsKey(clientID)){
                writeToLogFile("Validate clientID request : clientID : "+ clientID +" Status : Unsuccessful");
                return "false" + ServerConstants.FAILURE + "Sarvesh";
            }else{
                writeToLogFile("Validate clientID request : clientID : "+ clientID +" Status : Successful");
                return "true" + ServerConstants.SUCCESS + "Sarvesh";
            }
        }
        writeToLogFile("Validate clientID request : clientID : "+ clientID + " Status : Unsuccessful");
        return "false" + ServerConstants.FAILURE + "Sarvesh";
    }

    /**used by the user to exchange an item with another, first we return the item,
     * if successful, we attempt to borrow new item, if both operations are successful,
     * the whole operation is successful otherwise not.*/
    public String exchangeItem(String userID, String oldItemID, String newItemID) {
        User currentUser = user.get(userID);
        String reply ;
        boolean libraryItemBorrow;
            switch (newItemID.substring(0, 3)) {
                case "CON":
                    libraryItemBorrow = currentUser.getOutsourced()[0];
                    break;
                case "MCG":
                    libraryItemBorrow = currentUser.getOutsourced()[1];
                    break;
                case "MON":
                    libraryItemBorrow = currentUser.getOutsourced()[2];
                    break;
                default:
                    libraryItemBorrow = true;
            }
        String borrowReply,returnReply;
        int numberOfDays = 5;
        /*First check whether old item is borrowed.*/
        if(borrow.containsKey(currentUser)){
            /*Second check new item is available or not.*/
            if(isItemAvailable(newItemID) && (!libraryItemBorrow || oldItemID.substring(0,3).equals(newItemID.substring(0,3)))) {
                /*Third return the old item to particular library*/
                returnReply = returnItem(userID, oldItemID);
                if (returnReply.contains("SUCCESS")) {
                    /*Forth borrow new item.*/
                    borrowReply = borrowItem(userID, newItemID, numberOfDays);
                    if (borrowReply.contains("SUCCESS")) {
                        reply = "Successful" + ServerConstants.SUCCESS;
                    } else {
                        reply = "Error in borrowing the new book." + ServerConstants.FAILURE;
                    }
                } else {
                    reply = "Error in returning the old book.";
                }
            }else{
                reply = "mentioned new item not available or you have already " +
                        "borrowed one item from other library." + ServerConstants.FAILURE;
            }
        }else{
            reply = "you have not borrowed the mentioned old item." + ServerConstants.FAILURE;
        }
        return reply;
    }

    /**It allows the user to borrow an item from other library. If not present
     * at all libraries then it will ask user whether to add the user into the queue or not.*/
    public String borrowFromOtherLibrary(String userID, String itemID, Integer numberOfDays){
        User currentUser;
        synchronized (lock) { currentUser = user.get(userID); }
        String reply = "";
        try {
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            String result = "Unsuccessful";
            String request = library+":borrowFromOther:"+userID+":"+itemID+":"+numberOfDays;
            boolean isValidBorrow;
            ServerDetails serverDetails = null;
            switch (itemID.substring(0, 3)) {
                case "CON":
                    serverDetails = ServerDetails.CONCORDIA;
                    break;
                case "MCG":
                    serverDetails = ServerDetails.MCGILL;
                    break;
                case "MON":
                    serverDetails = ServerDetails.MONTREAL;
                    break;
            }
            isValidBorrow = currentUser.getOutsourced()[serverDetails.getIndex()];
            if(!isValidBorrow){
                DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,serverDetails.getPort());
                mySocket.send(sendRequest);
                byte[] receive = new byte[1024];
                DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
                mySocket.receive(receivedReply);
                result = new String(receivedReply.getData()).trim();
            }else{
                reply = "You have already borrowed from this library." + ServerConstants.FAILURE ;
            }
            if(result.contains("Successful")){
                reply = result;
                boolean[] isOutsourced;
                synchronized (lock) {isOutsourced = currentUser.getOutsourced();}
                isOutsourced[serverDetails.getIndex()] = true;
                synchronized (lock) {currentUser.setOutsourced(isOutsourced);}
                Item curruntItem;
                if(item.containsKey(itemID)){
                    curruntItem = item.get(itemID);
                    curruntItem.setItemCount(curruntItem.getItemCount()+1);
                    item.remove(itemID);
                }else{
                    curruntItem = new Item(itemID,serverDetails.getLibrary(),1);
                }
                item.put(itemID,curruntItem);
                if(borrow.containsKey(currentUser)){
                borrow.get(currentUser).put(curruntItem,numberOfDays);
                }else{
                    HashMap<Item,Integer> borrowed = new HashMap<>();
                    borrowed.put(curruntItem,numberOfDays);
                    borrow.put(currentUser,borrowed);
                }
            }else if(result.contains("waitlist")){
                reply = "waitList";
            }else{
                reply = "Unsuccessful" + ServerConstants.FAILURE;
            }
        }catch (SocketException e){
            writeToLogFile("Socket Exception");
            System.out.println("Socket Exception.");
            e.printStackTrace();
        }catch (UnknownHostException e){
            writeToLogFile("Unknown host Exception");
            System.out.println("Unknown host Exception.");
            e.printStackTrace();
        }catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
        }
        writeToLogFile(reply);
        return reply;
    }

    /**finds the given item name in other library and returns the itemID and availability.*/
    private String findAtOtherLibrary(String itemName){
        String reply = "";
        try{
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            int port1 = -1, port2 = -1;
            if (library.equals("CON")){
                port1 = 1302;
                port2 = 1303;
            }
            if (library.equals("MCG")){
                port1 = 1303;
                port2 = 1301;
            }
            if (library.equals("MON")){
                port1 = 1301;
                port2 = 1302;
            }

            String request = library+":findAtOther:"+itemName;
            DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,port1);
            mySocket.send(sendRequest);
            byte[] receive = new byte[1024];
            DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
            mySocket.receive(receivedReply);
            reply += new String(receivedReply.getData()).trim();
            sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,port2);
            mySocket.send(sendRequest);
            receive = new byte[1024];
            receivedReply = new DatagramPacket(receive,receive.length);
            mySocket.receive(receivedReply);
            reply += new String(receivedReply.getData()).trim();
        }catch (SocketException e){
            writeToLogFile("Socket Exception");
            System.out.println("Socket Exception.");
            e.printStackTrace();
        }catch (UnknownHostException e){
            writeToLogFile("Unknown host Exception");
            System.out.println("Unknown host Exception.");
            e.printStackTrace();
        }catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
        }
        writeToLogFile(reply);
        return reply;
    }

    /**it simply returns the given item to the library where it belongs to.*/
    private String returnToOtherLibrary(String userID, String itemID){
        User currentUser = user.get(userID);
        String reply ="";
        try{
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            ServerDetails serverDetails = null;
            if (itemID.substring(0,3).equals("CON")){
                serverDetails = ServerDetails.CONCORDIA;
            }
            if (itemID.substring(0,3).equals("MCG")){
                serverDetails = ServerDetails.MCGILL;
            }
            if (itemID.substring(0,3).equals("MON")){
                serverDetails = ServerDetails.MONTREAL;
            }
            String request = library+":returnToOther:"+userID+":"+itemID;
            DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,serverDetails.getPort());
            mySocket.send(sendRequest);
            byte[] receive = new byte[1024];
            DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
            mySocket.receive(receivedReply);
            String message = new String(receivedReply.getData()).trim();
            if(message.contains("Successful")){
                reply =  "Successful" + ServerConstants.SUCCESS;
                Item curruntItem;
                curruntItem = item.get(itemID);
                curruntItem.setItemCount(curruntItem.getItemCount()-1);
                item.remove(itemID);
                borrow.get(currentUser).remove(curruntItem);
                boolean[] isOutsourced;
                synchronized (lock) {isOutsourced = currentUser.getOutsourced();}
                isOutsourced[serverDetails.getIndex()] = false;
                synchronized (lock) {currentUser.setOutsourced(isOutsourced);}
            }else{
                reply = "Unsuccessful" + ServerConstants.FAILURE;
            }
        }catch (SocketException e){
            writeToLogFile("Socket Exception");
            System.out.println("Socket Exception.");
            e.printStackTrace();
        }catch (UnknownHostException e){
            writeToLogFile("Unknown host Exception");
            System.out.println("Unknown host Exception.");
            e.printStackTrace();
        }catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
        }
        writeToLogFile(reply);
        return reply;
    }

    /**it increases the item count by 1.*/
    protected synchronized void increamentItemCount(String itemID){
        Item currentItem = item.get(itemID);
        currentItem.setItemCount(currentItem.getItemCount()+1);
        item.remove(itemID);
        item.put(itemID,currentItem);
    }

    protected synchronized void decrementItemCount(String itemID){
        Item currentItem = item.get(itemID);
        currentItem.setItemCount(currentItem.getItemCount()-1);
        item.remove(itemID);
        item.put(itemID,currentItem);
    }

    /**to authenticate legal item id*/
    private boolean authenticateItemID(String id){
        return id.substring(0, 3).equals(library) && id.length() == 7;
    }

    /**to write the logs into log file.*/
    synchronized private void writeToLogFile(String message) {
        try {
            if (logger == null)
                return;
            // print the time and the message to log file
            logger.println(Calendar.getInstance().getTime().toString() + " - " + message);
            logger.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /***/
    private boolean isItemAvailable(String itemID){
        if(itemID.substring(0,3).equals(library)){
            if(item.containsKey(itemID)){
                Item currentItem = item.get(itemID);
                return currentItem.getItemCount() >= 1;
            }else
                return false;
        }else{
            try{
                DatagramSocket mySocket = new DatagramSocket();
                InetAddress host = InetAddress.getLocalHost();
                ServerDetails serverDetails = null;
                if (itemID.substring(0,3).equals("CON")){
                    serverDetails = ServerDetails.CONCORDIA;
                }
                if (itemID.substring(0,3).equals("MCG")){
                    serverDetails = ServerDetails.MCGILL;
                }
                if (itemID.substring(0,3).equals("MON")){
                    serverDetails = ServerDetails.MONTREAL;
                }
                String request = library+":isAvailable:"+itemID;
                DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,serverDetails.getPort());
                mySocket.send(sendRequest);
                byte[] receive = new byte[1024];
                DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
                mySocket.receive(receivedReply);
                String message = new String(receivedReply.getData()).trim();
                return message.equals("true");
            } catch (IOException e) {
                writeToLogFile("IO Exception");
                System.out.println("IO Exception.");
                e.printStackTrace();
                return false;
            }
        }
    }

    /**If the returned item is in the waiting queue list, it will
     * automatically assign the item to the first user and send
     * a message to the user.*/
    protected void automaticAssignmentOfBooks(String itemID) {
        System.out.println("---------------------------------In autoAssignement." + itemID);
        if(waitingQueue.containsKey(itemID)){
            HashMap<String,Integer> userList = waitingQueue.get(itemID);
            Iterator<Map.Entry<String,Integer>> iterator = userList.entrySet().iterator();
            System.out.println("---------------------------------in Autoassignment.");
            if(iterator.hasNext()){

                Map.Entry<String, Integer> pair = iterator.next();
                String message = borrowItem(pair.getKey(),itemID,pair.getValue());
                System.out.println(message);
                if(message.substring(message.length()-10).contains(ServerConstants.SUCCESS)) {
                    waitingQueue.get(itemID).remove(pair.getKey());
                    writeToLogFile(message);
                    if (!user.containsKey(pair.getKey())) {
                        addForgineUserInBorrow(pair.getKey(),itemID,pair.getValue());
                    }
                }
            }
        }
    }

    protected void addForgineUserInBorrow(String userID, String itemID, int numberOfDays){
        String reply = "";
        try{
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress host = InetAddress.getLocalHost();
            int port1 = -1;
            if (userID.substring(0,3).equals("CON")){
                port1 = 1301;
            }
            if (userID.substring(0,3).equals("MCG")){
                port1 = 1302;
            }
            if (userID.substring(0,3).equals("MON")){
                port1 = 1303;
            }
            System.out.println("---------------------------------in addFUB." + port1);
            String request = library+":addUserToBorrow:"+userID+":"+itemID+":"+numberOfDays;
            DatagramPacket sendRequest = new DatagramPacket(request.getBytes(),request.length(),host,port1);
            mySocket.send(sendRequest);
            byte[] receive = new byte[1024];
            DatagramPacket receivedReply = new DatagramPacket(receive,receive.length);
            mySocket.receive(receivedReply);
            reply = new String(receivedReply.getData()).trim();
        }catch (SocketException e){
            writeToLogFile("Socket Exception");
            System.out.println("Socket Exception.");
            e.printStackTrace();
        }catch (UnknownHostException e){
            writeToLogFile("Unknown host Exception");
            System.out.println("Unknown host Exception.");
            e.printStackTrace();
        }catch (IOException e){
            writeToLogFile("IO Exception");
            System.out.println("IO Exception.");
            e.printStackTrace();
        }
        writeToLogFile(reply);
    }

    public String simulateSoftwareBug(String username) {
        if (RequestHandlerMain.isSimulateSoftwareBug()) {
            return RequestHandlerConstants.CORRECT;
        } else {
            //alternative implementation in case of software bug
            return RequestHandlerConstants.BUGGY;
        }
    }

    public String simulateCrash(String username,String replicaName) {
        if(replicaName.equalsIgnoreCase("sarvesh")){
        if (RequestHandlerMain.isSimulateCrash()) {
            return RequestHandlerConstants.CRASH;
        } else {
            //alternative implementation in case of software bug
            return RequestHandlerConstants.RECOVER;
        }
        }
        else {
            return "false";
        }
    }
}
