

/*
 * Library CON port = 1301
 * Library MCG port = 1302
 * Library MON port = 1303
 * */

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;

public class Delegate implements Runnable{

    private Integer port;
    private ServerSARReplica library;

    public Delegate(Integer port, ServerSARReplica library){
        this.port = port;
        this.library = library;
    }

    @Override
    public void run() {
        DatagramSocket mySocket = null;
        try {
            mySocket = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                byte collector[] = new byte[1024];
                DatagramPacket receiver = new DatagramPacket(collector,collector.length);
                mySocket.receive(receiver);
                Thread newThread = new Thread(new RequestHandler(library,mySocket,receiver));
                newThread.start();
            }catch(IOException e){
                System.out.println("Input/Output exception");
                e.printStackTrace();
            }
        }
    }
}

class RequestHandler implements Runnable {

    private ServerSARReplica myServer = null;
    private DatagramSocket mySocket = null;
    private DatagramPacket receiver = null;
    private final Object lock;
    private File logFile;
    private PrintWriter logger;

    public RequestHandler(ServerSARReplica myServer,DatagramSocket mySocket,DatagramPacket receiver){
        this.myServer = myServer;
        this.mySocket = mySocket;
        this.receiver = receiver;
        lock = new Object();
        logFile = new File("/home/sarvesh/IdeaProjects/Distributed_Library_Management_System_new/SarveshReplica/src/Logs/" + myServer.library + "_IS_LOG.log");
        try{
            if(!logFile.exists())
                logFile.createNewFile();
            logger = new PrintWriter(new BufferedWriter(new FileWriter(logFile)));
        }catch (IOException io){
            System.out.println("Error in creating log file.");
            io.printStackTrace();
        }
        writeToLogFile(myServer.library + " Inter Server Started.");

    }
    /*format |    ServerName:RequestType:Argments     |
    ServerName = from which Server request came
    Request type =  Borrow from other lib.
                    Find at other lib.
                    Return to other lib.
     Arguments = [UserID]
                 [ItemID]
                 [ItemNama]
                 [NumberOfDays]
     */
    @Override
    public void run() {
        String data = new String(receiver.getData()).trim();
        String[] request = data.split(":");
        String reply = "";
        String userID,itemID,itemName;
        int numberOfDays;
        switch (request[1]){
            case "borrowFromOther" :
                if(request.length != 5){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                userID = request[2];
                itemID = request[3];
                numberOfDays = Integer.parseInt(request[4]);
                if(!myServer.item.containsKey(itemID)){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                Item requestedItem;
                synchronized (lock) {requestedItem = myServer.item.get(itemID);}
                if(requestedItem.getItemCount() == 0){
                    reply = "waitlist"+ServerConstants.SUCCESS;
                    break;
                }
                User currentUser = new User(userID);
                myServer.user.put(userID,currentUser);
                HashMap<Item,Integer> entry;
                myServer.decrementItemCount(itemID);
                if (myServer.borrow.containsKey(currentUser)) {
                    if (myServer.borrow.get(currentUser).containsKey(requestedItem)) {
                        reply = "Unsuccessful"+ServerConstants.FAILURE;
                        break;
                    } else {
                        synchronized (lock) {entry = myServer.borrow.get(currentUser);
                            myServer.borrow.remove(currentUser);
                        }
                    }
                } else {
                    entry = new HashMap<>();
                }
                synchronized (lock) {entry.put(requestedItem, numberOfDays);
                    myServer.borrow.put(currentUser, entry);
                }
                reply = "Successful";
                break;

            case "findAtOther" :
                if(request.length != 3){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                itemName = request[2];
                Iterator<Map.Entry<String, Item>> iterator;
                Item items ;
                synchronized (lock) {iterator = myServer.item.entrySet().iterator();}
                while(iterator.hasNext()){
                    Map.Entry<String, Item> pair = iterator.next();
                    if(pair.getValue().getItemName().equals(itemName)){
                        items = pair.getValue();
                        reply = items.toString();
                    }
                }
                break;

            case "returnToOther" :
                if(request.length != 4){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                userID = request[2];
                itemID = request[3];
                synchronized (lock) {currentUser = myServer.user.get(userID);}
                Iterator<Map.Entry<Item,Integer>> value;
                synchronized (lock) {
                    if(myServer.borrow.containsKey(currentUser)){
                    value = myServer.borrow.get(currentUser).entrySet().iterator();
                    }else{
                        reply = "Unsuccessful"+ServerConstants.FAILURE;
                        break;
                    }
                }
                if(!value.hasNext()){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                boolean status = false;
                while(value.hasNext()) {
                    Map.Entry<Item, Integer> pair = value.next();
                    if(pair.getKey().getItemID().equals(itemID)){
                        synchronized (lock) {
                            myServer.borrow.get(currentUser).remove(pair.getKey());
                            myServer.user.remove(userID);
                            myServer.increamentItemCount(itemID);
                            myServer.automaticAssignmentOfBooks(itemID);
                        }
                        status = true;
                        break;
                    }
                }
                if (status) {
                    reply = "Successful"+ServerConstants.SUCCESS;
                }else{
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                }
                break;

            case "isAvailable":
                if(request.length != 3){
                    reply = "Unsuccessful";
                    break;
                }
                itemID = request[2];
                if(myServer.item.containsKey(itemID)){
                    Item currentItem = myServer.item.get(itemID);
                    if(currentItem.getItemCount() >= 1)
                        reply = "true";
                    else
                        reply = "false";
                }else
                    reply = "false";
                break;
            case "addToWaitlist":
                if(request.length != 5){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                userID = request[2];
                itemID = request[3];
                numberOfDays = Integer.parseInt(request[4]);
                System.out.println("Delegate Add to another ID.");
                if (myServer.waitingQueue.containsKey(itemID)) {
                    if(!myServer.waitingQueue.get(itemID).containsKey(userID)){
                        myServer.waitingQueue.get(itemID).put(userID, numberOfDays);
                        reply = "Successful"+ServerConstants.SUCCESS;
                    }else{
                        reply = "Unsuccessful"+ServerConstants.FAILURE;
                    }
                } else {
                    HashMap<String, Integer> userList = new HashMap<>();
                    userList.put(userID, numberOfDays);
                    myServer.waitingQueue.put(itemID, userList);
                    reply = "Successful"+ServerConstants.SUCCESS;
                }
                break;
            case "addUserToBorrow":
                if(request.length != 5){
                    reply = "Unsuccessful"+ServerConstants.FAILURE;
                    break;
                }
                userID = request[2];
                itemID = request[3];
                numberOfDays = Integer.parseInt(request[4]);
                currentUser = myServer.user.get(userID);
                if(myServer.borrow.containsKey(userID)){
                    Item currentItem = new Item(itemID,itemID.substring(0,3),1);
                    myServer.borrow.get(currentUser).put(currentItem,numberOfDays);
                }else{
                    Item currentItem = new Item(itemID,itemID.substring(0,3),1);
                    HashMap<Item,Integer> record = new HashMap<>();
                    record.put(currentItem,numberOfDays);
                    myServer.borrow.put(currentUser,record);
                }
                Item curruntItem;
                if(myServer.item.containsKey(itemID)){
                    curruntItem = myServer.item.get(itemID);
                    curruntItem.setItemCount(curruntItem.getItemCount()+1);
                    myServer.item.remove(itemID);
                }else{
                    curruntItem = new Item(itemID,itemID.substring(0,3),1);
                }
                myServer.item.put(itemID,curruntItem);
                break;
            default:
                reply = "Unsuccessful";
        }
        writeToLogFile(request[1] + " " + reply);
        DatagramPacket sender = new DatagramPacket(reply.getBytes(), reply.length(), receiver.getAddress(), receiver.getPort());
        try {
            mySocket.send(sender); // send the response DatagramPacket object to the requester.
        } catch (IOException e) {
            System.out.println("IO Exception");
            e.printStackTrace();
        }
    }

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

}