import org.omg.CORBA.ORB;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.*;


public class Server_Base implements Runnable, ServerInterface {
    private ORB orb;
    private String servername;
    private String globalString;
    private HashMap<String, ArrayList<String>> userUpdateMessages;
    private HashMap<String, HashMap> libLendingRec;
    private HashMap waitlistRec;
    private HashMap libBooksRec;
    private HashMap<String, String> syncHeap;
    private DatagramSocket ds;
    private DatagramSocket ds1;
    private DatagramPacket dps;
    private DatagramPacket dpr;
    private Thread t;
    private final Object lock = new Object();
    private int universalPort;
    private ArrayList interLibraryBlockUsers;

    public DatagramSocket getDs1() {
        return ds1;
    }

    public void setDs1() {
        try {
            if(!(this.ds1.isBound())){
                this.ds1 = new DatagramSocket(this.universalPort);
            }

            System.out.println(this.ds1.getPort());


        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void setUserlist(ArrayList userlist) {
        this.userlist = userlist;
    }

    private ArrayList userlist;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }



    public HashMap<String, ArrayList<String>> getUserUpdateMessages() {
        return this.userUpdateMessages;
    }

    public void setUserUpdateMessages(HashMap<String, ArrayList<String>> userUpdateMessages) {
        this.userUpdateMessages = userUpdateMessages;
    }

    /** This method creates string of the inbox of different users who have unread messages.
     * @param userID
     * @return
     */
    public String getUserMessageStr(String userID) {
        HashMap<String, ArrayList<String>> msgHolder = getUserUpdateMessages();
        ArrayList<String> valueHolder = msgHolder.get(userID);
        int i = 0;
        String returnString = "";
        while (i < valueHolder.size()) {
            System.out.println("I am stuck in the loop");
            returnString = returnString + valueHolder.get(i) + "\n";
            i++;
        }
        msgHolder.remove(userID);
        System.out.println(returnString);
        return returnString;

    }

    /**
     * A constructor to insitate the Class instance
     * @param name
     * @throws IOException
     */
    public Server_Base(String name) throws IOException {
        this.servername = name;
        this.syncHeap = new HashMap<String, String>();
        this.userUpdateMessages = new HashMap<String, ArrayList<String>>();
        //this.ds = new DatagramSocket();
        this.t = new Thread(this, getServername().substring(0,3));
        this.loadServerRec(this.servername);
        this.ds1 = new DatagramSocket(this.universalPort);
        //System.out.println("The server "+this.getServername()+ "is up"+this.ds1.getPort());
        this.t.start();

    }


    /** Setter and getter methoods
     * @return
     */
    public HashMap getLibBooksRec() {
        return this.libBooksRec;
    }

    public void setLibBooksRec(HashMap libBooksRec) {
        this.libBooksRec = libBooksRec;
    }

    public HashMap getLibLendingRec() {
        return this.libLendingRec;
    }

    public void setLibLendingRec(HashMap libLendingRec) {
        this.libLendingRec = libLendingRec;
    }


    public HashMap getWaitlistRec() {
        return this.waitlistRec;
    }

    public void setWaitlistRec(HashMap waitlistRec) {
        this.waitlistRec = waitlistRec;
    }

    public String getServername() {
        return this.servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    /**Loads the prerequisite data to the class instances of the library servers
     * @param xLib
     * @return
     */
    public void loadServerRec(String xLib) {
        if (xLib.equals("CONCORDIA")) {
            try {
                this.ds = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            HashMap<String, ArrayList<String>> tempStore = new HashMap<String, ArrayList<String>>();


            HashMap<String, ArrayList<String>> tempHash1 = new HashMap<String, ArrayList<String>>();
            tempHash1.put("CON0001", new ArrayList<String>(Arrays.asList("DSD", "5")));
            tempHash1.put("CON0002", new ArrayList<String>(Arrays.asList("ALGO", "0")));

            HashMap<String, ArrayList<String>> tempHash2 = new HashMap<String, ArrayList<String>>();

            this.setLibLendingRec(tempStore);
            this.setLibBooksRec(tempHash1);
            this.setWaitlistRec(tempHash2);
            this.universalPort = 8081;
            this.interLibraryBlockUsers = new ArrayList<String>();
            ArrayList<String> holder = new ArrayList<String>(Arrays.asList("CONM0001", "CONU0001","CONU0002"));
            this.setUserlist(holder);



        } else if (this.getServername().equals("MCGILL")) {
            try {
                this.ds = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            HashMap<String, ArrayList<String>> tempStore = new HashMap<String, ArrayList<String>>();

            HashMap<String, ArrayList<String>> tempHash1 = new HashMap<String, ArrayList<String>>();
            tempHash1.put("MCG0001", new ArrayList<String>(Arrays.asList("DSD", "5")));
            tempHash1.put("MCG0002", new ArrayList<String>(Arrays.asList("ALGO", "0")));

            HashMap<String, ArrayList<String>> tempHash2 = new HashMap<String, ArrayList<String>>();

            this.setLibLendingRec(tempStore);
            this.setLibBooksRec(tempHash1);
            this.setWaitlistRec(tempHash2);
            this.universalPort = 8082;
            this.interLibraryBlockUsers = new ArrayList<String>();
            ArrayList<String> holder = new ArrayList<String>(Arrays.asList("MCGM0001", "MCGU0001","MCGU0002"));
            this.setUserlist(holder);
//            try {
//                setDs1(new DatagramSocket(this.universalPort));
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }


        } else if (this.getServername().equals("MONTREALU")) {
            try {
                this.ds = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            HashMap<String, ArrayList<String>> tempStore = new HashMap<String, ArrayList<String>>();

            HashMap<String, ArrayList<String>> tempHash1 = new HashMap<String, ArrayList<String>>();
            tempHash1.put("MON0001", new ArrayList<String>(Arrays.asList("DSD", "5")));
            tempHash1.put("MON0002", new ArrayList<String>(Arrays.asList("ALGO", "0")));

            HashMap<String, ArrayList<String>> tempHash2 = new HashMap<String, ArrayList<String>>();

            this.setLibLendingRec(tempStore);
            this.setLibBooksRec(tempHash1);
            this.setWaitlistRec(tempHash2);
            this.universalPort = 8083;
            this.interLibraryBlockUsers = new ArrayList<String>();
            ArrayList<String> holder = new ArrayList<String>(Arrays.asList("MONM0001", "MONU0001","MONU0002"));
            this.setUserlist(holder);
//            try {
//                setDs1(new DatagramSocket(this.universalPort));
//            } catch (SocketException e) {
//                e.printStackTrace();
//            }


        } else {
            System.out.println("Server is invalid");
        }
    }

    /**This method helps a manager to add the items into the library
     * @param managerID
     * @param itemID
     * @param itemName
     * @param quantity
     * @return
     */
    public String addItem(String managerID, String itemID, String itemName, int quantity) {

        String finalString = "";
        if (managerID.substring(3, 4).equals("M")) {

            if (getLibBooksRec().containsKey(itemID)) {
                System.out.println(getLibBooksRec().get(itemID));
                System.out.println(getWaitlistRec().get(itemID));
                System.out.println(getLibLendingRec().get(itemID));
                ArrayList<String> value = (ArrayList<String>) getLibBooksRec().get(itemID);
                if (quantity > 0) {
                    this.syncHeap.put(itemID, managerID);
                    if (!(getWaitlistRec().containsKey(itemID))) {
                        getLibBooksRec().remove(itemID);
                        System.out.println(value.get(0) + " " + value.get(1));
                        getLibBooksRec().put(itemID, new ArrayList<String>(Arrays.asList((value.get(0)), Integer.toString(Integer.parseInt(value.get(1)) + quantity))));
                        this.syncHeap.remove(itemID);
                        appendStrToFile("This item " + itemID + " is already listed in the " + getServername() + " library and the quantity is increased by " + Integer.toString(quantity)+"\n");
                        return ("This item " + itemID + " is already listed in the " + getServername() + " library and the quantity is increased by " + Integer.toString(quantity));
                    } else {
                        int least = 0;
                        ArrayList<String> lendHolder;
                        ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
                        if(getLibLendingRec().containsKey(itemID)) {
                            lendHolder = (ArrayList<String>) getLibLendingRec().get(itemID);
                            getLibLendingRec().remove(itemID);
                        }
                        else{
                            lendHolder = new ArrayList<String>();
                        }
                        getWaitlistRec().remove(itemID);
                        if (quantity < waitHolder.size())
                            least = quantity;
                        else
                            least = waitHolder.size();
                        int iter = 0;
                        do {
                            System.out.println(waitHolder.get(0));
                            this.updateMessageHash("Your waitlist for the item " + itemID + " is clear the item is being issued to ",waitHolder.get(0));
                            appendStrToFile("Your waitlist for the item " + itemID + " is clear the item is being issued to "+waitHolder.get(0)+"\n");
                            System.out.println("Item " + itemID + " is automatically used to clear the waitlist, the item has been issued to " + waitHolder.get(0));
                            appendStrToFile("Item " + itemID + " is automatically used to clear the waitlist, the item has been issued to " + waitHolder.get(0)+"\n");
                            finalString = finalString + ("Item " + itemID + " is automatically used to clear the waitlist, the item has been issued to " + waitHolder.get(0) + "\n");
                            appendStrToFile(finalString);
                            System.out.println(lendHolder);
                            System.out.println(waitHolder.get(0));
                            lendHolder.add(waitHolder.get(0));
                            if(!(waitHolder.get(0).substring(0,3).equals(getServername().substring(0,3))))
                            {
                                this.interLibraryBlockUsers.add(waitHolder.get(0));
                            }
                            waitHolder.remove(waitHolder.get(0));
                            iter = iter + 1;
                            System.out.println(iter);
                            System.out.println(least);
                            System.out.println(waitHolder);
                        } while (iter < least);
                        if (quantity != iter) {
                            getLibBooksRec().remove(itemID);
                            System.out.println(value.get(0) + " " + value.get(1));
                            getLibBooksRec().put(itemID, new ArrayList<String>(Arrays.asList((value.get(0)), Integer.toString(Integer.parseInt(value.get(1)) + (quantity - iter)))));
                            finalString = finalString + ("After clearing the waitlist  for the item the remaining " + Integer.toString(quantity - least) + " copies of the item were added to the library. \n");
                            appendStrToFile(finalString);
                        }
                        if(!(waitHolder.isEmpty()))
                        {
                            System.out.println("The waitlist after adding is "+waitHolder);
                            getWaitlistRec().put(itemID, waitHolder);
                        }
                        if(!(lendHolder.isEmpty())){
                            getLibLendingRec().put(itemID, lendHolder);
                            System.out.println("The lend list after adding is "+lendHolder);
                        }


                    }
                    this.syncHeap.remove(itemID);
                } else {
                    if (quantity < 0) {
                        this.syncHeap.put(itemID, managerID);
                        ArrayList<String> lendHolder = (ArrayList<String>) getLibLendingRec().get(itemID);
                        ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
                        getLibLendingRec().remove(itemID);
                        getWaitlistRec().remove(itemID);

                        for (int iter = 0; iter < waitHolder.size(); iter++) {
                            this.updateMessageHash("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list", waitHolder.get(iter));
                            System.out.println("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list");
                            finalString = finalString + ("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list\n");
                            waitHolder.remove(waitHolder.get(iter));
                        }
                        appendStrToFile(finalString);
                        for (int iter2 = 1; iter2 < lendHolder.size(); iter2++) {
                            this.updateMessageHash("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the borrower's list", lendHolder.get(iter2));
                            System.out.println("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the borrowers' list");
                            finalString = finalString + ("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the borrower's list\n");
                            lendHolder.remove(lendHolder.get(iter2));
                        }
                        appendStrToFile(finalString);
                        this.syncHeap.remove(itemID);

                    } else {
                        appendStrToFile("The  value entered is invalid");
                        return ("The  value entered is invalid");
                    }


                }

            } else if (syncHeap.containsKey(itemID)) {
                System.out.println("The record for this item is currently being used by another user please try after sometime\n");
                appendStrToFile("The record for this item is currently being used by another user please try after sometime\n");
                return ("The record for this item is currently being used by another user please try after sometime\n");
            } else {
                getLibBooksRec().put(itemID, new ArrayList<String>(Arrays.asList(itemName, Integer.toString(quantity))));
                System.out.println("This item was not listed in the library a new entry has been made for this with the provided quantity\n");
                appendStrToFile("his item was not listed in the library a new entry has been made for this with the provided q\n");
                return ("This item was not listed in the library a new entry has been made for this with the provided quantity\n");
            }
        } else {
            System.out.println("The user is not authorized for this action");
            appendStrToFile("The user is not authorized for this action");
            return ("The user is not authorized for this action");
        }

        return finalString;
    }

    public String removeItem(String managerId, String itemId, int quantity) {

            String finalString = "";
            if (managerId.substring(3, 4).equals("M")) {

                if (!getLibBooksRec().containsKey(itemId)) {
                    if (syncHeap.containsKey(itemId)) {
                        System.out.println("The record for this item is currently being used by another user please try after sometime\n");
                        appendStrToFile("The record for this item is currently being used by another user please try after sometime\n");
                        return ("The record for this item is currently being used by another user please try after sometime\n");
                    } else {
                        System.out.println("The item does not exist in the library.\n");
                        appendStrToFile("The item does not exist in the library.\n");
                        return ("The item has not been listed in the library.\n");
                    }
                } else if (false) {
                    String msg = "";
                    syncHeap.put(itemId, managerId);
                    getLibBooksRec().remove(itemId);
                    ArrayList<String> tempUsers = getLendingDetail(itemId);
                    getLibLendingRec().remove(itemId);
                    for (int i = 1; i < tempUsers.size(); i++) {
                        msg = "The item " + itemId + " has been completely removed from the library. All the copies are being recalled, please return back the item.\n";
                        updateMessageHash(msg, tempUsers.get(i));
                        appendStrToFile(msg);
                        msg = "";
                    }
                    ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemId);
                    System.out.println(waitHolder);
                    for (int iter = 0; iter < waitHolder.size(); iter++) {
                        this.updateMessageHash("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list", waitHolder.get(iter));
                        appendStrToFile("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list"+ waitHolder.get(iter));
                        System.out.println("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list");
                        appendStrToFile("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list");
                        finalString = finalString + ("Notified the user" + waitHolder.get(iter) + "\n");
                        appendStrToFile(finalString);
                        waitHolder.remove(waitHolder.get(iter));
                    }
                    syncHeap.remove(itemId);
                    System.out.println("Notified all the borrowers to return back");
                    appendStrToFile("Notified all the borrowers to return back");
                    System.out.println("The item has been completely removed from the library.\n");
                    appendStrToFile("The item has been completely removed from the library.\n");
                    return ("The item has been removed from the library availability list.\nAll the borrowers are notified to return back.\n");

                } else {
                    if (quantity < 0) {
                        this.syncHeap.put(itemId, managerId);
                        ArrayList<String> lendHolder = (ArrayList<String>) getLibLendingRec().get(itemId);
                        ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemId);
                        getLibLendingRec().remove(itemId);
                        getWaitlistRec().remove(itemId);

                        for (int iter = 0; iter < waitHolder.size(); iter++) {
                            this.updateMessageHash("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list", waitHolder.get(iter));
                            System.out.println("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list");
                            finalString = finalString + ("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list\n");
                            waitHolder.remove(waitHolder.get(iter));
                        }
                        for (int iter2 = 1; iter2 < lendHolder.size(); iter2++) {
                            this.updateMessageHash("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list", lendHolder.get(iter2));
                            System.out.println("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list");
                            finalString = finalString + ("The manager of the item has removed the item: " + itemId + ". Hence you have been removed from the waiting list\n");
                            lendHolder.remove(lendHolder.get(iter2));
                        }
                        appendStrToFile(finalString);
                        this.syncHeap.remove(itemId);

                    } else {
                        ArrayList<String> bRecHolder = (ArrayList<String>) getLibBooksRec().get(itemId);
                        if (quantity <= Integer.parseInt(bRecHolder.get(1))) {
                            System.out.println(getLibBooksRec().get(itemId));
                            ArrayList<String> value = (ArrayList<String>) getLibBooksRec().get(itemId);
                            syncHeap.put(itemId, managerId);
                            getLibBooksRec().remove(itemId);
                            System.out.println(value.get(0) + " " + value.get(1));
                            if (Integer.parseInt(value.get(1)) > quantity) {
                                getLibBooksRec().put(itemId, new ArrayList<String>(Arrays.asList((value.get(0)), Integer.toString(Integer.parseInt(value.get(1)) - quantity))));

                                System.out.println("The appropriate amount of item have been removed from the unborrowed section");
                                appendStrToFile("The appropriate amount of item have been removed from the unborrowed section");
                                return "The appropriate amount of item have been removed from the unborrowed section";
                            } else {
                                getLibBooksRec().put(itemId, new ArrayList<String>(Arrays.asList((value.get(0)), "0")));
                                syncHeap.remove(itemId, managerId);
                                System.out.println("The item has been removed from the unborrowed section, nothing is left");
                                appendStrToFile("The item has been removed from the unborrowed section, nothing is left");
                                return "The item has been removed from the unborrowed section, nothing is left";
                            }
                        } else {
                            appendStrToFile("The entered value is more than the availablity, cannot remove.");
                            return ("The entered value is more than the availablity, cannot remove.");
                        }
                    }
                }
            } else {
                appendStrToFile("The User is not authorized for this action\n");
                System.out.println("The User is not authorized for this action");
                return ("The User is not authorized for this action");
            }
            return finalString;

    }



//    /**This method helps a manager to remove an item from the library
//     * @param managerID
//     * @param itemID
//     * @param quantity
//     * @param completeRemove
//     * @return
//     */
//    public String removeItem(String managerID, String itemID, int quantity, boolean completeRemove) {
//        String finalString = "";
//        if (managerID.substring(3, 4).equals("M")) {
//
//            if (!getLibBooksRec().containsKey(itemID)) {
//                if (syncHeap.containsKey(itemID)) {
//                    System.out.println("The record for this item is currently being used by another user please try after sometime\n");
//                    appendStrToFile("The record for this item is currently being used by another user please try after sometime\n");
//                    return ("The record for this item is currently being used by another user please try after sometime\n");
//                } else {
//                    System.out.println("The item does not exist in the library.\n");
//                    appendStrToFile("The item does not exist in the library.\n");
//                    return ("The item has not been listed in the library.\n");
//                }
//            } else if (completeRemove == true) {
//                String msg = "";
//                syncHeap.put(itemID, managerID);
//                getLibBooksRec().remove(itemID);
//                ArrayList<String> tempUsers = getLendingDetail(itemID);
//                getLibLendingRec().remove(itemID);
//                for (int i = 1; i < tempUsers.size(); i++) {
//                    msg = "The item " + itemID + " has been completely removed from the library. All the copies are being recalled, please return back the item.\n";
//                    updateMessageHash(msg, tempUsers.get(i));
//                    appendStrToFile(msg);
//                    msg = "";
//                }
//                ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
//                System.out.println(waitHolder);
//                for (int iter = 0; iter < waitHolder.size(); iter++) {
//                    this.updateMessageHash("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list", waitHolder.get(iter));
//                    appendStrToFile("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list"+ waitHolder.get(iter));
//                    System.out.println("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list");
//                    appendStrToFile("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list");
//                    finalString = finalString + ("Notified the user" + waitHolder.get(iter) + "\n");
//                    appendStrToFile(finalString);
//                    waitHolder.remove(waitHolder.get(iter));
//                }
//                syncHeap.remove(itemID);
//                System.out.println("Notified all the borrowers to return back");
//                appendStrToFile("Notified all the borrowers to return back");
//                System.out.println("The item has been completely removed from the library.\n");
//                appendStrToFile("The item has been completely removed from the library.\n");
//                return ("The item has been removed from the library availability list.\nAll the borrowers are notified to return back.\n");
//
//            } else {
//                if (quantity < 0) {
//                    this.syncHeap.put(itemID, managerID);
//                    ArrayList<String> lendHolder = (ArrayList<String>) getLibLendingRec().get(itemID);
//                    ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
//                    getLibLendingRec().remove(itemID);
//                    getWaitlistRec().remove(itemID);
//
//                    for (int iter = 0; iter < waitHolder.size(); iter++) {
//                        this.updateMessageHash("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list", waitHolder.get(iter));
//                        System.out.println("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list");
//                        finalString = finalString + ("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list\n");
//                        waitHolder.remove(waitHolder.get(iter));
//                    }
//                    for (int iter2 = 1; iter2 < lendHolder.size(); iter2++) {
//                        this.updateMessageHash("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list", lendHolder.get(iter2));
//                        System.out.println("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list");
//                        finalString = finalString + ("The manager of the item has removed the item: " + itemID + ". Hence you have been removed from the waiting list\n");
//                        lendHolder.remove(lendHolder.get(iter2));
//                    }
//                    appendStrToFile(finalString);
//                    this.syncHeap.remove(itemID);
//
//                } else {
//                    ArrayList<String> bRecHolder = (ArrayList<String>) getLibBooksRec().get(itemID);
//                    if (quantity <= Integer.parseInt(bRecHolder.get(1))) {
//                        System.out.println(getLibBooksRec().get(itemID));
//                        ArrayList<String> value = (ArrayList<String>) getLibBooksRec().get(itemID);
//                        syncHeap.put(itemID, managerID);
//                        getLibBooksRec().remove(itemID);
//                        System.out.println(value.get(0) + " " + value.get(1));
//                        if (Integer.parseInt(value.get(1)) > quantity) {
//                            getLibBooksRec().put(itemID, new ArrayList<String>(Arrays.asList((value.get(0)), Integer.toString(Integer.parseInt(value.get(1)) - quantity))));
//
//                            System.out.println("The appropriate amount of item have been removed from the unborrowed section");
//                            appendStrToFile("The appropriate amount of item have been removed from the unborrowed section");
//                            return "The appropriate amount of item have been removed from the unborrowed section";
//                        } else {
//                            getLibBooksRec().put(itemID, new ArrayList<String>(Arrays.asList((value.get(0)), "0")));
//                            syncHeap.remove(itemID, managerID);
//                            System.out.println("The item has been removed from the unborrowed section, nothing is left");
//                            appendStrToFile("The item has been removed from the unborrowed section, nothing is left");
//                            return "The item has been removed from the unborrowed section, nothing is left";
//                        }
//                    } else {
//                        appendStrToFile("The entered value is more than the availablity, cannot remove.");
//                        return ("The entered value is more than the availablity, cannot remove.");
//                    }
//                }
//            }
//        } else {
//            appendStrToFile("The User is not authorized for this action\n");
//            System.out.println("The User is not authorized for this action");
//            return ("The User is not authorized for this action");
//        }
//        return finalString;
//    }

    public String listItem(String managerId) {
        String prepString = "[";
        if (managerId.substring(3, 4).equals("M")) {
            Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
            for (Map.Entry<String, ArrayList<String>> entry : tempSet) {

                //System.out.print(entry.getKey());
                ArrayList<String> valueHolder = (ArrayList<String>) entry.getValue();
                //System.out.println(": Name: "+temp_holder[0]+", "+"Availability: "+temp_holder[1]+"\n");
                prepString = prepString +"itemName="   +"'"+ valueHolder.get(0)+"'" +", "+"itemId=" +"'"+ entry.getKey()+"'" +", "+  "quantity=" + valueHolder.get(1)+", ";

            }
            prepString = prepString.substring(0,prepString.length()-2);
            prepString = prepString + "]";
            System.out.println(prepString);
            return prepString;
        } else {
            System.out.println("The User is not authorized for this action");
            appendStrToFile("The User is not authorized for this action. \n");
            return ("The User is not authorized for this action");
        }


    }

    public String addUserInWaitingList(String userId  , String ItemId, int numberOfDays) {
        {
            int sport =0 ;
            if (true) {
                if(this.getServername().substring(0,3).equals(ItemId.substring(0,3))) {
                    if (getWaitlistRec().containsKey(ItemId)) {
                        ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(ItemId);
                        getWaitlistRec().remove(ItemId);
                        if (waitHolder.contains(userId)) {
                            appendStrToFile("You are already in the waitlist for this item");
                            System.out.println("You are already in the waitlist for this item");
                            if(!(waitHolder.isEmpty()))
                            {
                                getWaitlistRec().put(ItemId, waitHolder);
                            }
                            System.out.println("The waitlist for the item "+ItemId + "is"+getWaitlistRec()+ "at server" + getServername());
                            return ("You are already in the waitlist for this item");
                        } else {
                            waitHolder.add(userId);
                            if(!(waitHolder.isEmpty()))
                            {
                                getWaitlistRec().put(ItemId, waitHolder);
                            }

                            appendStrToFile("You have been sucessfully waitlisted for the item " + ItemId);
                            System.out.println("You have been sucessfully waitlisted for the item " + ItemId);
                            System.out.println(this.getServername());
                            System.out.println("The waitlidt for the item" + ItemId + " is " + getWaitlistRec().get(ItemId));
                            System.out.println("The waitlist for the item "+ItemId + "is"+getWaitlistRec()+ "at server" + getServername());
                            return ("You have been sucessfully waitlisted for the item " + ItemId);
                        }
                    } else {
                        ArrayList<String> waitHolder = new ArrayList<String>();
                        waitHolder.add(userId);
                        getWaitlistRec().put(ItemId, waitHolder);
                        appendStrToFile("You have been sucessfully waitlisted for the item " + ItemId);
                        System.out.println("You have been sucessfully waitlisted for the item " + ItemId);
                        System.out.println("I am in here" + this.getServername());
                        System.out.println("The waitlidt for the item" + ItemId + " is " + getWaitlistRec().get(ItemId));
                        System.out.println("The waitlist for the item "+ItemId + "is"+getWaitlistRec()+ "at server" + getServername());
                        return ("You have been sucessfully waitlisted for the item " + ItemId);

                    }
                }

                else{
                    if(ItemId.substring(0,3).equals("CON"))
                    {
                        sport = 8081;
                    }
                    else if(ItemId.substring(0,3).equals("MCG"))
                    {
                        sport = 8082;
                    }
                    else if(ItemId.substring(0,3).equals("MON"))
                    {
                        sport = 8083;
                    }
                    System.out.println("I want to go to"+ItemId.substring(0,3)+ "to borrow my book");
                    appendStrToFile("I want to go go to"+ItemId.substring(0,3)+ "to borrow my book\n");
                    System.out.println("UDP for calling the correct server on the client's behalf");
                    appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                    String i = "W" + ";" + userId + "#" + ItemId + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(sport);
                    byte[] b = (i + "").getBytes();
                    System.out.println(i);
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.dps = new DatagramPacket(b, b.length, ia, 9999);
                    try {
                        System.out.println("I am trying to send the request");
                        appendStrToFile("I am trying to send a request\n");
                        this.ds.send(dps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println("The call to the remote server has been made \n");
                    appendStrToFile("The call to the remote server has been made \n");
                    synchronized (lock) {
                        try {
                            lock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                    System.out.println("");
                    System.out.println("I am in " + getServername());
                    appendStrToFile("I am in \" + getServername()\n");
                    System.out.println(this.globalString);
                    String finalString = "";
                    finalString  = this.globalString;
                    //appendStrToFile(finalString);
                    this.globalString = "";
                    return finalString;
                }
            } else {
                appendStrToFile ("Invalid response");
                return ("Invalid response");
            }
        }
    }

//    public String listItemAvailability(String managerID) {
//        String prepString = "";
//        if (managerID.substring(3, 4).equals("M")) {
//            Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
//            for (Map.Entry<String, ArrayList<String>> entry : tempSet) {
//
//                //System.out.print(entry.getKey());
//                ArrayList<String> temp_holder = (ArrayList<String>) entry.getValue();
//                //System.out.println(": Name: "+temp_holder[0]+", "+"Availability: "+temp_holder[1]+"\n");
//                prepString = prepString + entry.getKey() + ": Name: " + temp_holder.get(0) + ", " + "Availability: " + temp_holder.get(1) + "\n";
//            }
//            System.out.println(prepString);
//            return prepString;
//        } else {
//            System.out.println("The User is not authorized for this action");
//            appendStrToFile("The User is not authorized for this action. \n");
//            return ("The User is not authorized for this action");
//        }
//
//    }

    /**This method removed the item from a library after checking the mentioned validation rules
     * @param userID
     * @param itemID
     * @param numberOfDays
     * @return
     * @throws UnknownHostException
     */
    public String borrowItem(String userID, String itemID, int numberOfDays) {
        String finalString = "";
        if (userID.substring(3, 4).equals("U")) {
            if (itemID.substring(0, 3).equals(getServername().substring(0, 3))) {
                if (getLibBooksRec().containsKey(itemID)) {
                    ArrayList<String> bRecHolder = (ArrayList<String>) getLibBooksRec().get(itemID);
                    getLibBooksRec().remove(itemID);
                    System.out.println(getSyncHeap());
                    System.out.println(itemID);
                    System.out.println(userID);
                    this.syncHeap.put(itemID, userID);
                    if (getLibLendingRec().containsKey(itemID)) {
                        System.out.println("I am inside the lending condition");
                        appendStrToFile("I am inside the lending condition\n");
                        ArrayList<String> lendHolder = (ArrayList<String>) getLendingDetail(itemID);
                        getLibLendingRec().remove(itemID);
                        if (lendHolder.contains(userID)) {
                            getLibLendingRec().put(itemID, lendHolder);
                            getLibBooksRec().put(itemID, bRecHolder);
                            getSyncHeap().remove(itemID);
                            appendStrToFile("You already have a copy of this item");
                            return "You already have a copy of this item";
                        }
                        if (Integer.parseInt(bRecHolder.get(1)) > 0) {
                            if (lendHolder.contains(userID)) {

                            } else {
                                lendHolder.add(userID);
                                getLibLendingRec().put(itemID, lendHolder);
                                System.out.println("Lending history has been updated");
                                finalString = finalString + "Lending history has been updated you have successfully borrowed. \n";
                                bRecHolder.set(1, Integer.toString(Integer.parseInt(bRecHolder.get(1)) - 1));
                                getLibBooksRec().put(itemID, bRecHolder);
                                System.out.println("The book record has been updated");
                                finalString = finalString + "The book record has been updated. \n";
                                appendStrToFile(finalString);
                                getSyncHeap().remove(itemID, userID);
                            }
                        } else {
                            if (Integer.parseInt(bRecHolder.get(1)) <= 0) {
                                getLibLendingRec().put(itemID, lendHolder);
                                getLibBooksRec().put(itemID, bRecHolder);
                                getSyncHeap().remove(itemID, userID);
                                finalString = finalString + ("The item is currently not available\n");
                                finalString = finalString + ("Would you like to be added to the waitlist? Enter Y for Yes and N for No.\n");
                                finalString = finalString + ("Enter Y for Yes and N for No");
                                appendStrToFile(finalString);
                                return finalString;
                            } else {
                                getLibLendingRec().put(itemID, lendHolder);
                                getLibBooksRec().put(itemID, bRecHolder);
                                getSyncHeap().remove(itemID, userID);
                                return ("Internal data error!");
                            }
                        }
                    } else {
                        if (Integer.parseInt(bRecHolder.get(1)) > 0) {
                            ArrayList<String> lendHolder = new ArrayList<String>();
                            System.out.println(bRecHolder);
                            lendHolder.add(bRecHolder.get(0));
                            lendHolder.add(userID);
                            getLibLendingRec().put(itemID, lendHolder);
                            System.out.println("Lending history has been updated");
                            finalString = finalString + "Lending history has been updated. \n";
                            bRecHolder.set(1, Integer.toString(Integer.parseInt(bRecHolder.get(1)) - 1));
                            getLibBooksRec().put(itemID, bRecHolder);
                            System.out.println("The book record has been updated");
                            finalString = finalString + "The book record is updated. The item has been successfully borrowed by you.\n";
                            appendStrToFile(finalString);
                            getSyncHeap().remove(itemID, userID);
                        } else {
                            getLibBooksRec().put(itemID, bRecHolder);
                            getSyncHeap().remove(itemID, userID);
                            if (Integer.parseInt(bRecHolder.get(1)) <= 0) {
                                finalString = finalString + ("The item is currently not available\n");
                                finalString = finalString + ("Would you like to be added to the waitlist?");
                                finalString = finalString + ("Enter Y for Yes and N for No");
                                appendStrToFile(finalString);
                                return finalString;
                            } else {
                                return ("Internal data error!");
                            }

                        }
                    }
                } else {
                    if (getSyncHeap().containsKey(itemID)) {
                        return "The record for this item is currently being used by another user\n";
                    } else {
                        System.out.println("The item does not exist in the library " + getServername());
                        finalString = finalString + "The item does not exist in the library " + getServername() + "\n";
                        appendStrToFile(finalString);
                    }
                }

            } else {
                if (itemID.substring(0, 3).equals("CON") && !(this.interLibraryBlockUsers.contains(userID))) {
                    System.out.println("I want to go to Concordia to borrow my book");
                    appendStrToFile("I want to go to Concordia to borrow my book\n");
                    System.out.println("UDP for calling the correct server on the client's behalf");
                    appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                    String i = "F" + ";" + userID + "#" + itemID + "$" + Integer.toString(numberOfDays) + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8081);
                    byte[] b = (i + "").getBytes();
                    System.out.println(i);
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.dps = new DatagramPacket(b, b.length, ia, 9999);
                    try {
                        System.out.println("I am trying to send the request");
                        appendStrToFile("I am trying to send the request\n");
                        this.ds.send(dps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalString = finalString + "The call to the remote server has been made \n";
                    appendStrToFile(finalString);
                    synchronized (lock) {
                        try {
                            lock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("");
                        System.out.println("I am in " + getServername());
                        System.out.println("This is inside the method:" + this.globalString);
                        finalString = finalString + this.globalString;
                        appendStrToFile(finalString);
                        this.globalString = "";

                    }
                    //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                } else if (itemID.substring(0, 3).equals("MCG") && !(this.interLibraryBlockUsers.contains(userID))) {
                    System.out.println("I want to go to Mcgill to borrow my book");
                    appendStrToFile("I want to go to Mcgill to borrow my book\n");
                    System.out.println("UDP for calling the correct server on the client's behalf");
                    appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                    System.out.println("I am in "+ this.servername);
                    String i = "F" + ";" + userID + "#" + itemID + "$" + Integer.toString(numberOfDays) + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8082);
                    byte[] b = (i + "").getBytes();
                    System.out.println(i);
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.dps = new DatagramPacket(b, b.length, ia, 9999);

                    try {
                        this.ds.send(dps);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finalString = finalString + "The call to the remote server has been made \n";
                    appendStrToFile(finalString);
                    synchronized (lock) {
                        try {
                            lock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("");
                        System.out.println("I am in " + getServername());
                        appendStrToFile("I am in " + getServername()+"\n");
                        System.out.println("This is inside the method:" + this.globalString);
                        finalString = finalString + this.globalString;
                        this.globalString = "";


                    }
                    //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                }else if(itemID.substring(0, 3).equals("MON") && !(this.interLibraryBlockUsers.contains(userID)))
                {
                    {
                        System.out.println("I want to go to University of Montreal to borrow my book");
                        appendStrToFile("I want to go to University of Montreal to borrow my book\n");
                        System.out.println("UDP for calling the correct server on the client's behalf");
                        appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                        String i = "F" + ";" + userID + "#" + itemID + "$" + Integer.toString(numberOfDays) + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8083);
                        byte[] b = (i + "").getBytes();
                        System.out.println(i);
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        this.dps = new DatagramPacket(b, b.length, ia, 9999);
                        try {
                            System.out.println("I am trying to send the request");
                            appendStrToFile("I am trying to send a request\n");
                            this.ds.send(dps);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalString = finalString + "The call to the remote server has been made \n";
                        appendStrToFile(finalString);
                        synchronized (lock) {
                            try {
                                lock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("");
                            System.out.println("I am in " + getServername());
                            appendStrToFile("I am in \" + getServername()\n");
                            System.out.println("This is inside the method:" + this.globalString);
                            finalString = finalString + this.globalString;
                            appendStrToFile(finalString);
                            this.globalString = "";

                        }
                        //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                    }

                }else if(this.interLibraryBlockUsers.contains(userID)) {
                    appendStrToFile("You have already borrowed an item from an outside library you cannot borrow another one.\n");
                    return "You have already borrowed an item from an outside library you cannot borrow another one.";
                }
                else{
                    appendStrToFile("There is no suitable server for this item.\n");
                    return "There is no suitable server for this item.";
                }
            }
        } else {
            appendStrToFile("The User is not authorized for this action");
            System.out.println("The User is not authorized for this action");
            return ("The User is not authorized for this action");
        }
        return finalString;

    }

    /**This method helps a user to find a item in his/her own library and other respective libraries.
     * @param userID
     * @param itemName
     * @return
     * @throws UnknownHostException
     */
    public String findItem(String userID, String itemName) {
        String personal ="";
        String finalString = "";
        appendStrToFile(finalString);
        if (userID.substring(3, 4).equals("U")) {
            Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
            for (Map.Entry<String, ArrayList<String>> entry : tempSet) {
                ArrayList<String> valueHolder = entry.getValue();
                System.out.println(valueHolder.get(0));
                System.out.println(valueHolder.get(1));
                if (valueHolder.get(0).startsWith(itemName) ) {
                    //&& (valueHolder.get(1) != "0")
                    finalString = "ITEMNAME="   +"'"+ valueHolder.get(0)+"'" +", "+"ITEMID=" +"'"+ entry.getKey()+"'" +", "+  "QUANTITY= " + valueHolder.get(1);
                    System.out.println(finalString);
                }
            }
            appendStrToFile(finalString);
            personal =  finalString;
            System.out.println("Send a UDP call to other servers for this and list down their items");
            appendStrToFile("Send a UDP call to other servers for this and list down their items\n");
            {
                if (this.universalPort != 8081) {
                    System.out.println("UDP for calling the correct server on the client's behalf to 8081");
                    appendStrToFile("UDP for calling the correct server on the client's behalf to 8081\n");
                    String i = "X" + ";" + userID + "#" + itemName + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8081);
                    byte[] b = (i + "").getBytes();
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.dps = new DatagramPacket(b, b.length, ia, 9999);
                    try {
                        this.ds.send(dps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appendStrToFile(finalString);
                    synchronized (lock) {
                        try {
                            lock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("");
                        System.out.println("I am in " + getServername());
                        System.out.println("This is inside the method:" + this.globalString);
                        appendStrToFile("This is inside the method:" + this.globalString+"\n");
                        finalString = finalString + this.globalString;
                        System.out.println("Here inside the end of the first call: " + finalString);
                        appendStrToFile("Here inside the end of the first call: " + finalString);

                    }
                }
                if (this.universalPort != 8082) {
                    //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                    System.out.println("UDP for calling the correct server on the client's behalf to 8082");
                    appendStrToFile("UDP for calling the correct server on the client's behalf to 8082\n");
                    String i1 = "Y" + ";" + userID + "#" + itemName + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8082);
                    System.out.println(i1);
                    appendStrToFile(i1);
                    byte[] b1 = (i1 + "").getBytes();
                    InetAddress ia1 = null;
                    try {
                        ia1 = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    this.dps = new DatagramPacket(b1, b1.length, ia1, 9999);
                    try {
                        this.ds.send(dps);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appendStrToFile(finalString);
                    synchronized (lock) {
                        try {
                            lock.wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("");
                        System.out.println("I am in " + getServername());
                        appendStrToFile("I am in " + getServername());
                        System.out.println("This is inside the method:" + this.globalString);
                        appendStrToFile("This is inside the method:" + this.globalString);
                        finalString = finalString + this.globalString;
                        appendStrToFile(finalString);
                        System.out.println("Here inside the end of the second call: " + finalString);


                    }
                }
                if(this.universalPort != 8083)
                {
                    {
                        //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                        System.out.println("UDP for calling the correct server on the client's behalf to 8083");
                        appendStrToFile("UDP for calling the correct server on the client's behalf to 8083\n");
                        String i1 = "Y" + ";" + userID + "#" + itemName + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8083);
                        System.out.println(i1);
                        appendStrToFile(i1);
                        byte[] b1 = (i1 + "").getBytes();
                        InetAddress ia1 = null;
                        try {
                            ia1 = InetAddress.getLocalHost();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        this.dps = new DatagramPacket(b1, b1.length, ia1, 9999);
                        try {
                            this.ds.send(dps);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        appendStrToFile(finalString);
                        synchronized (lock) {
                            try {
                                lock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("");
                            System.out.println("I am in " + getServername());
                            appendStrToFile("I am in " + getServername());
                            System.out.println("This is inside the method:" + this.globalString);
                            appendStrToFile("This is inside the method:" + this.globalString);
                            finalString = finalString + this.globalString;
                            System.out.println("Here inside the end of the second call: " + finalString);
                            appendStrToFile("Here inside the end of the second call: " + finalString);


                        }
                    }

                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }

        } else {
            System.out.println("The User is not authorized for this action");
            appendStrToFile("The User is not authorized for this action");
            return ("The User is not authorized for this action");

        }
        System.out.println(finalString);
        return (finalString);

    }

    /** This method helps a user to validate and return their issued books
     * @param userID
     * @param itemID
     * @return
     */
    public String returnItem(String userID, String itemID) {
        String finalString = "";
        if (userID.substring(3, 4).equals("U")) {
            if (itemID.substring(0, 3).equals(getServername().substring(0, 3))) {
                if (getLibBooksRec().containsKey(itemID)) {
                    ArrayList<String> bRecHolder = (ArrayList<String>) getLibBooksRec().get(itemID);
                    getLibBooksRec().remove(itemID);
                    getSyncHeap().put(itemID, userID);
                    System.out.println(getLibLendingRec().get(itemID));
                    if (getLibLendingRec().containsKey(itemID)) {
                        ArrayList<String> lendHolder = (ArrayList<String>) getLibLendingRec().get(itemID);
                        getLibLendingRec().remove(itemID);
                        if (!(lendHolder.contains(userID))) {
                            getLibLendingRec().put(itemID, lendHolder);
                            getLibBooksRec().put(itemID, bRecHolder);
                            getSyncHeap().remove(itemID);
                            System.out.println("I am inside the lendholder check");
                            appendStrToFile("I am inside the lendholder check\n");
                            finalString = finalString + "Officially, you don't have a copy of this item. So you cannot return. Please contact your manager";
                            appendStrToFile(finalString);
                        } else {
                            System.out.println("I am after the lendholder check");
                            appendStrToFile("I am after the lendholder check");
                            if (getWaitlistRec().containsKey(itemID)) {
                                ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
                                getWaitlistRec().remove(itemID);
                                lendHolder.remove(userID);
                                lendHolder.add(waitHolder.get(0));
                                System.out.println(lendHolder);
                                //call UDP for foreign library users.
//                                if(waitHolder.get(0) != this.getServername().substring(0,3))
//                                {
//                                    System.out.println("I got inside the right placeZZZZZZZZZZZZZZZZZZZZZZZZZZ.");
//                                }
                                updateMessageHash("Your waitlist for the item " + itemID + " is clear + the item has been issued to you", waitHolder.get(0));
                                appendStrToFile("Your waitlist for the item " + itemID + " is clear + the item has been issued to you"+ waitHolder.get(0));
                                System.out.println("The book has been issued to the first waitlisted person.");
                                appendStrToFile("The book has been issued to the first waitlisted person.");
                                waitHolder.remove(waitHolder.get(0));
                                System.out.println(waitHolder);
                                finalString = finalString + "The book has been successfully returned\n";
                                getLibLendingRec().put(itemID, lendHolder);
                                getLibBooksRec().put(itemID, bRecHolder);
                                if(!(waitHolder.size() == 0))
                                    getWaitlistRec().put(itemID, waitHolder);
                                getSyncHeap().remove(itemID);
                            } else {
                                bRecHolder.set(1, Integer.toString(Integer.parseInt(bRecHolder.get(1)) + 1));
                                lendHolder.remove(userID);
                                getLibBooksRec().put(itemID, bRecHolder);
                                getLibLendingRec().put(itemID, lendHolder);
                                getSyncHeap().remove(itemID);
                                finalString = finalString + "The item has been added to the library.\n";
                            }
                        }

                    } else {
                        getLibBooksRec().put(itemID, bRecHolder);
                        getSyncHeap().remove(itemID);
                        System.out.println("I am outside the lend holder check");
                        finalString = finalString + "Officially, you don't have a copy of this item. So you cannot return. Please contact your manager";
                    }
                } else {
                    if (getSyncHeap().containsKey(itemID)) {
                        return "The record for this item is currently being used by another user\n";
                    } else {
                        return "This book ID does not exist in the library " + getServername();
                    }
                }
            } else {
                {
                    if (itemID.substring(0, 3).equals("CON") ) {
                        System.out.println("I want to go to Concordia to return my book");
                        appendStrToFile("I want to go to Concordia to return my book\n");
                        System.out.println("UDP for calling the correct server on the client's behalf");
                        appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                        String i = "L" + ";" + userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8081);
                        byte[] b = (i + "").getBytes();
                        System.out.println(i);
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        this.dps = new DatagramPacket(b, b.length, ia, 9999);
                        try {
                            System.out.println("I am trying to send return the request");
                            appendStrToFile("I am trying to send the return request\n");
                            this.ds.send(dps);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalString = finalString + "The call to the remote server has been made \n";
                        appendStrToFile(finalString);
                        synchronized (lock) {
                            try {
                                lock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("");
                            System.out.println("I am in " + getServername());
                            System.out.println("This is inside the method:" + this.globalString);
                            finalString = finalString + this.globalString;
                            appendStrToFile(finalString);
                            this.globalString = "";

                        }
                        //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                    } else if (itemID.substring(0, 3).equals("MCG")) {
                        System.out.println("I want to go to Mcgill to return my book");
                        appendStrToFile("I want to go to Mcgill to return my book\n");
                        System.out.println("UDP for calling the correct server on the client's behalf");
                        appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                        String i = "M" + ";" + userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8082);
                        byte[] b = (i + "").getBytes();
                        System.out.println(i);
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        this.dps = new DatagramPacket(b, b.length, ia, 9999);

                        try {
                            this.ds.send(dps);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        finalString = finalString + "The call to the remote server has been made \n";
                        appendStrToFile(finalString);
                        synchronized (lock) {
                            try {
                                lock.wait(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("");
                            System.out.println("I am in " + getServername());
                            appendStrToFile("I am in " + getServername()+"\n");
                            System.out.println("This is inside the method:" + this.globalString);
                            finalString = finalString + this.globalString;
                            this.globalString = "";


                        }
                        //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                    }else if(itemID.substring(0, 3).equals("MON"))
                    {
                        {
                            System.out.println("I want to go to University of Montreal to return my book");
                            appendStrToFile("I want to go to University of Montreal to return my book\n");
                            System.out.println("UDP for calling the correct server on the client's behalf");
                            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                            String i = "N" + ";" + userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8083);
                            byte[] b = (i + "").getBytes();
                            System.out.println(i);
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            try {
                                System.out.println("I am trying to send the request");
                                appendStrToFile("I am trying to send a request\n");
                                this.ds.send(dps);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finalString = finalString + "The call to the remote server has been made \n";
                            appendStrToFile(finalString);
                            synchronized (lock) {
                                try {
                                    lock.wait(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("");
                                System.out.println("I am in " + getServername());
                                appendStrToFile("I am in \" + getServername()\n");
                                System.out.println("This is inside the method:" + this.globalString);
                                finalString = finalString + this.globalString;
                                appendStrToFile(finalString);
                                this.globalString = "";

                            }
                            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                        }

                    }else if(!(this.interLibraryBlockUsers.contains(userID))) {
                        appendStrToFile("You have never borrowed an item from an outside your own library you cannot return.\n");
                        return "You have never borrowed an item from an outside your own library you cannot return.";
                    }
                    else{
                        appendStrToFile("There is no suitable server for this item.\n");
                        return "There is no suitable server for this item.";
                    }
                }            }
        } else {
            finalString = finalString + "The User is not authorized for this action\n";
        }
        System.out.println("I am in the end");
        System.out.println(finalString);
        appendStrToFile(finalString);
        return finalString;

    }

    public String exchangeItem(String userID, String oldItemID, String newItemID)
    {
        System.out.println("The old item is  = " +oldItemID);
        System.out.println("The new item is  = " +newItemID);
        String finalString = null;
        boolean parm1 = itemAvailabilityCheck(newItemID);
        boolean parm2 = itemBorrowedCheck(userID,oldItemID);
        if(parm1 == false)
        {
            System.out.println("The value of the parm2 to check the availability validation is :false");
            return "The new book you want to borrow is currently not available we cannot process the exchange";
        }
        else if(parm2 == false)
        {
            System.out.println("The value of the parm2 to check the boroow validation is :false");
            return "The book you want to return in the exchange was never officially take under your ID, hence we cannot process the exchange "+":"+ServerConstants.FAILURE;
        }
        else if(parm1 == true && parm2 == true)
        {
            if(userID.substring(0,3).equals(oldItemID.substring(0,3)) && !(this.interLibraryBlockUsers.contains(userID))) {
                System.out.println("both parm1 and parm 2 are true");
                finalString = this.returnItem(userID, oldItemID);
                try {
                    finalString = finalString + this.borrowItem(userID, newItemID, 1);
                    finalString = finalString +" Exchange Successful ";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(userID.substring(0,3).equals(oldItemID.substring(0,3)) && (this.interLibraryBlockUsers.contains(userID)))
            {
                if(newItemID.substring(0,3).equals(userID.substring(0,3)))
                {
                    System.out.println("both parm1 and parm 2 are true");
                    finalString = this.returnItem(userID, oldItemID);
                    try {
                        finalString = finalString + this.borrowItem(userID, newItemID, 1);
                        finalString = finalString +" Exchange Successful";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                else
                {
                    return "You already have a borrowed book outside your own library, if you want to get another one from outside, please return the foreign library's book first, or get it exchanged with that one. "+":"+ServerConstants.FAILURE;
                }

            }
            else
            {
                System.out.println("both parm1 and parm 2 are true");
                finalString = this.returnItem(userID, oldItemID);
                try {
                    finalString = finalString + this.borrowItem(userID, newItemID, 1);
                    finalString = finalString +" Exchange Successful";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("+++++++++++++++++++"+finalString+"++++++++++++++++++++");
        return finalString;
    }
    public String validateUser(String userId) {
        String returnString;
        if(this.userlist.contains(userId))
        {
            return("Its a validTrue User"+ServerConstants.SUCCESS);
        }
        else {
            return ("Its a invalidFalse User"+ServerConstants.FAILURE);
        }
    }

    /**This method verifies the ID and the connection
     * @param ID
     * @return
     */
    public String verify(String ID) {
        String finalString = "";
        System.out.println("*************************************************************************");
        System.out.println(ID);
        System.out.println("Verifying the connection at " + getServername() + " Library.");
        appendStrToFile("Verifying the connection at " + getServername() + " Library.");
        if (getServername().substring(0, 3).equals(ID.substring(0, 3))) {
            System.out.println("The ID is connected to right server");
            appendStrToFile("The ID is connected to right server");
            finalString = finalString + "The ID is connected to right server.\n";
        } else {
            System.out.println("The ID is not present on this server");
            appendStrToFile("The ID is not present on this server");
            finalString = finalString + "The ID has vaild user type.\n";
            return ("false");
        }
        if (ID.substring(3, 4).equals("M") || ID.substring(3, 4).equals("U")) {
            System.out.println("The ID has correct user type");
            appendStrToFile("The ID has correct user type");
            finalString = finalString + "The ID has vaild user type.\n";
        } else {
            System.out.println("The user type is incorrect");
            return ("false");
        }
        try {
            if (userUpdateMessages.containsKey(ID)) {
                System.out.println("I am inside here");
                finalString = finalString + " You have new messages from the server.\n" + this.getUserMessageStr((ID)) + "\n";
                System.out.println("I am after the call to the message method");
                appendStrToFile("I am after the call to the message method");
            } else {
                finalString = finalString + "The ID has vaild user type.\n";
                finalString = finalString + "No new Messages for you. \n";
            }
        } catch (NullPointerException e) {
            finalString = finalString + "No new Messages for you. \n";
            System.out.println("No new messages for you");
            appendStrToFile("No new messages for you");
        }
        if (ID.substring(3, 4).equals("M")) {
            System.out.println("I am inside here in the options panel");
            finalString =  "true";

        }
        if (ID.substring(3, 4).equals("U")) {

            finalString = "true";
        }

        appendStrToFile(finalString);
        return finalString;
    }

    public boolean load_server(String server_name) {
        return false;
    }

    /**getter  method for a book
     * @param ID
     * @return
     */
    public ArrayList<String> getBookDetail(String ID) {
        try {
            if (getLibBooksRec().containsKey(ID)) {
                return (ArrayList<String>) getLibBooksRec().get(ID);
            } else {
                System.out.println("The ID is incorrect, it does not exist in the library " + getServername());
                appendStrToFile("The ID is incorrect, it does not exist in the library " + getServername());
                return null;
            }

        } catch (NullPointerException E) {
            return null;
        }
    }

    /**getter method for lendnig records
     * @param ID
     * @return
     */
    public ArrayList<String> getLendingDetail(String ID) {
        try {
            if (getLibLendingRec().containsKey(ID)) {
                return (ArrayList<String>) getLibLendingRec().get(ID);
            } else {
                System.out.println("The action is incorrect, item has never been borrowed yet at " + getServername());
                appendStrToFile("The action is incorrect, item has never been borrowed yet at " + getServername());
                ArrayList<String> returnParm = new ArrayList<String>();
                returnParm.add("null");
                return returnParm;
            }

        } catch (NullPointerException E) {
            System.out.println("The action is incorrect, item has never been borrowed yet at " + getServername());
            appendStrToFile("The action is incorrect, item has never been borrowhaned yet at " + getServername());
            ArrayList<String> returnParm = new ArrayList<String>();
            returnParm.add("null");
            return returnParm;
        }

    }

    public HashMap<String, String> getSyncHeap() {
        return this.syncHeap;
    }

    /**synchoronization heap setter
     * @param syncHeap
     */
    public void setSyncHeap(HashMap<String, String> syncHeap) {
        this.syncHeap = syncHeap;
    }

    /**getter for wait detail
     * @param ID
     * @return
     */
    public ArrayList<String> getWaitdetail(String ID) {
        try {
            if (getWaitlistRec().containsKey(ID)) {
                return (ArrayList<String>) getWaitlistRec().get(ID);
            } else {
                System.out.println("The action is incorrect, item does not have a waitlist at " + getServername());
                appendStrToFile("The action is incorrect, item does not have a waitlist at " + getServername());
                ArrayList<String> returnParm = new ArrayList<String>();
                returnParm.add("null");
                return returnParm;
            }

        } catch (NullPointerException E) {
            System.out.println("The action is incorrect, item does not have a waitlist at " + getServername());
            appendStrToFile("The action is incorrect, item does not have a waitlist at " + getServername());
            ArrayList<String> returnParm = new ArrayList<String>();
            returnParm.add("null");
            return returnParm;
        }

    }

    /**update messages in the inbox of a user
     * @param msg
     * @param userID
     */
    public void updateMessageHash(String msg, String userID) {
        HashMap<String, ArrayList<String>> localMsgCopy = (HashMap<String, ArrayList<String>>) getUserUpdateMessages();
        ArrayList<String> tempStore = new ArrayList<String>();

        if (getUserUpdateMessages().containsKey(userID)) {
            tempStore = localMsgCopy.get(userID);
            getUserUpdateMessages().remove(userID);
            tempStore.add(msg);
            getUserUpdateMessages().put(userID, tempStore);
            System.out.println("Message has been added to the profile of " + userID + "\n");
            appendStrToFile("Message has been added to the profile of " + userID + "\n");
        } else {
            tempStore.add(msg);
            getUserUpdateMessages().put(userID, tempStore);
            System.out.println("Message has been added to the profile of " + userID + "\n");
            appendStrToFile("Message has been added to the profile of " + userID + "\n");
        }
    }

    /**Waiting list handling method
     * @param parm
     * @param itemID
     * @param userID
     * @return
     */
    public String addToWait(String parm, String itemID, String userID) {
        int sport =0 ;
        if (parm.equals("Y")) {
            if(this.getServername().substring(0,3).equals(itemID.substring(0,3))) {
                if (getWaitlistRec().containsKey(itemID)) {
                    ArrayList<String> waitHolder = (ArrayList<String>) getWaitlistRec().get(itemID);
                    if (waitHolder.contains(userID)) {
                        appendStrToFile("You are already in the waitlist for this item");
                        System.out.println("You are already in the waitlist for this item");
                        return ("You are already in the waitlist for this item");
                    } else {
                        getWaitlistRec().remove(itemID);
                        waitHolder.add(userID);
                        getWaitlistRec().put(itemID, waitHolder);
                        appendStrToFile("You have been sucessfully waitlisted for the item " + itemID);
                        System.out.println("You have been sucessfully waitlisted for the item " + itemID);
                        System.out.println(this.getServername());
                        System.out.println("The waitlidt for the item" + itemID + " is " + getWaitlistRec().get(itemID));
                        return ("You have been sucessfully waitlisted for the item " + itemID);
                    }
                } else {
                    ArrayList<String> waitHolder = new ArrayList<String>();
                    ArrayList<String> bRecHolder = (ArrayList<String>) getLibBooksRec().get(itemID);
                    waitHolder.add(userID);
                    getWaitlistRec().put(itemID, waitHolder);
                    appendStrToFile("You have been sucessfully waitlisted for the item " + itemID);
                    System.out.println("You have been sucessfully waitlisted for the item " + itemID);
                    System.out.println("I am in here" + this.getServername());
                    System.out.println("The waitlidt for the item" + itemID + " is " + getWaitlistRec().get(itemID));
                    return ("You have been sucessfully waitlisted for the item " + itemID);

                }
            }

            else{
                if(itemID.substring(0,3).equals("CON"))
                {
                    sport = 8081;
                }
                else if(itemID.substring(0,3).equals("MCG"))
                {
                    sport = 8082;
                }
                else if(itemID.substring(0,3).equals("MON"))
                {
                    sport = 8083;
                }
                System.out.println("I want to go to"+itemID.substring(0,3)+ "to return my book");
                appendStrToFile("I want to go go to"+itemID.substring(0,3)+ "to return my book\n");
                System.out.println("UDP for calling the correct server on the client's behalf");
                appendStrToFile("UDP for calling the correct server on the client's behalf\n");
                String i = "W" + ";" + userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(sport);
                byte[] b = (i + "").getBytes();
                System.out.println(i);
                InetAddress ia = null;
                try {
                    ia = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                try {
                    System.out.println("I am trying to send the request");
                    appendStrToFile("I am trying to send a request\n");
                    this.ds.send(dps);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("The call to the remote server has been made \n");
                appendStrToFile("The call to the remote server has been made \n");
                synchronized (lock) {
                    try {
                        lock.wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in \" + getServername()\n");
                System.out.println("This is inside the method:" + this.globalString);
                String finalString = "";
                finalString  = finalString + this.globalString;
                appendStrToFile(finalString);
                this.globalString = "";
                return finalString;
            }
        } else {
            appendStrToFile ("Invalid response");
            return ("Invalid response");
        }
    }

    /**Method which managers all the interserver communications
     * @throws IOException
     */
    public void interServerInteractor() throws IOException {
//        try {
//            System.out.println("I am testing" +this.getServername());
//            System.out.println(this.universalPort + this.getServername());
                setDs1();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
        while(true) {
            if (this.getServername().equals("CONCORDIA")) {
                byte[] b1 = null;
                b1 = new byte[1024];
                this.dpr = new DatagramPacket(b1, b1.length);
                System.out.println("I am open for listen");
                appendStrToFile("I am open for listen");
                //InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 8081);
                //ds1.bind(address);
                //this.ds1 = new DatagramSocket(8081);

                getDs1().receive(dpr);
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername());
                String result = null;
                result = new String(dpr.getData());
                System.out.println(result);
                System.out.println(result.charAt(0));
                if (result.startsWith("F")) {
                    appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                    appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                    result = result.trim();
                    String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                    String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                    int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                    String finalResult = null;
                    if(this.interLibraryBlockUsers.contains(vuserID))
                    {
                        finalResult = " You alrrady have a book from "+this.servername+ " this transaction cannot be made.";
                    }
                    else {
                        finalResult = this.borrowItem(vuserID, vitemID, vnumberOfDays);
                    }
                    if(finalResult.contains("successfully borrowed"))
                    {
                        this.interLibraryBlockUsers.add(vuserID);
                        System.out.println("These are the blockec users in "+this.servername+ this.interLibraryBlockUsers);
                    }
                    String rece = "B" + ";" + finalResult + result.substring(result.indexOf("@"));
                    byte[] b = (rece + "").getBytes();
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                        this.dps = new DatagramPacket(b, b.length, ia, 9999);
                        //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                        appendStrToFile("I am sending the packet");
                        this.ds.send(this.dps);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (result.startsWith("B")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);

                    } else if ((result.startsWith("X")) || (result.startsWith("Y")) || (result.startsWith("Z"))) {
                        System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                        appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                        System.out.println(result);
                        System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                        appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("@"));
                        String finalString = "No item available with entered name in "+this.getServername();
                        if (vuserID.substring(3, 4).equals("U")) {
                            Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
                            for (Map.Entry<String, ArrayList<String>> entry : tempSet) {
                                ArrayList<String> valueHolder = entry.getValue();
                                if (valueHolder.get(0).matches(vitemName) ) {
                                    //&& (valueHolder.get(1) != "0")
                                    //finalString = "ITEMNAME: " + valueHolder.get(0) +"ITEMID: " + entry.getKey() +  ", QUANTITY: " + valueHolder.get(1);
                                    finalString = "ITEMNAME="   +"'"+ valueHolder.get(0)+"'" +", "+"ITEMID=" +"'"+ entry.getKey()+"'" +", "+  "QUANTITY= " + valueHolder.get(1);
                                }
                            }

                            String rece = "P" + ";" + finalString + result.substring(result.indexOf("@"));
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                appendStrToFile("I am sending the packet");
                                System.out.println("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    } else if (result.startsWith("P") || result.startsWith("Q") || result.startsWith("R")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;

                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if(result.startsWith("L")|| result.startsWith("M") || result.startsWith("N")) {
                        System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                        appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                        System.out.println(result);
                        System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                        appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        String finalString = "Items matching your entry at " + getServername() + " are:\n";
                        finalString = finalString + returnItem( vuserID,  vitemName);
                        if(finalString.contains("The item has been added to the library"))
                        {
                            this.interLibraryBlockUsers.remove(vuserID);
                            System.out.println("These are the blocked users"+ this.interLibraryBlockUsers);

                        }
                        String rece = "S" + ";" + finalString + result.substring(result.indexOf("@"));
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else if (result.startsWith("S") || result.startsWith("T") || result.startsWith("U")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if(result.startsWith("E"))
                    {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                        boolean parm = this.itemAvailabilityCheck(vitemID);
                        String rece = null;
                        if(parm == true) {
                            System.out.println(" The item is available");
                            appendStrToFile(" The item is available \n");
                            rece = "G" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                        }
                        else
                        {
                            System.out.println(" The item is not available");
                            appendStrToFile(" The item is not available \n");
                            rece = "G" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                        }
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("G")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if(result.startsWith("J"))
                    {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                        boolean parm = this.itemBorrowedCheck(vuserID, vitemID);
                        String rece = null;
                        if(parm == true) {
                            System.out.println(" The item is available");
                            appendStrToFile(" The item is available \n");
                            rece = "K" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                        }
                        else
                        {
                            System.out.println(" The item is not available");
                            appendStrToFile(" The item is not available \n");
                            rece = "K" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                        }
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("K")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if (result.startsWith("W")) {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        String finalResult = null;
                        System.out.println("I have reached "+getServername());
                        finalResult = this.addUserInWaitingList(vuserID,vitemID,1);
                        System.out.println(finalResult);
                        String rece = "V" + ";" + finalResult + result.substring(result.indexOf("@"));
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("V")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);

                    }


                }
            }
            if (this.getServername().equals("MCGILL")) {
                byte[] b1 = null;
                b1 = new byte[1024];
                this.dpr = new DatagramPacket(b1, b1.length);
                System.out.println("I am open for listen");
                appendStrToFile("I am open for listen");
                //InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 8082);
                //ds1.bind(address);
                //this.ds1 = new DatagramSocket(8082);
//                try {
//                    System.out.println("I am testing" +this.getServername());
//                    setDs1(new DatagramSocket(this.universalPort));
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                }
                getDs1().receive(dpr);
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername());
                String result = null;
                result = new String(dpr.getData());
                result = result.trim();
                System.out.println(result);
                System.out.println(result.charAt(0));
                if (result.startsWith("F")) {
                    System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                    appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                    System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                    appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                    result = result.trim();
                    String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                    String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                    int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                    String finalResult = null;
                    if(this.interLibraryBlockUsers.contains(vuserID))
                    {
                        finalResult = " You alrrady have a book from "+this.servername+ " this transaction cannot be made.";
                    }
                    else {
                        finalResult = this.borrowItem(vuserID, vitemID, vnumberOfDays);
                    }
                    if(finalResult.contains("successfully borrowed"))
                    {
                        this.interLibraryBlockUsers.add(vuserID);
                        System.out.println("These are the blockec users"+this.servername + this.interLibraryBlockUsers);
                    }
                    String rece = "B" + ";" + finalResult + result.substring(result.indexOf("@"));
                    System.out.println("The found matches on this server " + rece);
                    appendStrToFile("The found matches on this server " + rece);
                    byte[] b = (rece + "").getBytes();
                    InetAddress ia = null;
                    try {
                        ia = InetAddress.getLocalHost();
                        this.dps = new DatagramPacket(b, b.length, ia, 9999);
                        //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                        System.out.println("I am sending the packet");
                        appendStrToFile("I am sending the packet");
                        this.ds.send(this.dps);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (result.startsWith("B")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        appendStrToFile("At the end " + this.globalString);
                        System.out.println("At the end " + this.globalString);

                    } else if ((result.startsWith("X")) || (result.startsWith("Y")) || (result.startsWith("Z"))) {
                        System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                        appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                        System.out.println(result);
                        System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                        appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("@"));
                        String finalString = "No item available with entered name in "+this.getServername();
                        if (vuserID.substring(3, 4).equals("U")) {
                            Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
                            for (Map.Entry<String, ArrayList<String>> entry : tempSet) {
                                ArrayList<String> valueHolder = entry.getValue();
                                if (valueHolder.get(0).matches(vitemName) ) {
                                    //&& (valueHolder.get(1) != "0")
                                    //finalString = finalString + "Code: " + entry.getKey() + ", Name: " + valueHolder.get(0) + ", Availability: " + valueHolder.get(1) + "\n";
                                    //finalString = "ITEMNAME: " + valueHolder.get(0) +"ITEMID: " + entry.getKey() +  ", QUANTITY: " + valueHolder.get(1);
                                    finalString = "ITEMNAME="   +"'"+ valueHolder.get(0)+"'" +", "+"ITEMID=" +"'"+ entry.getKey()+"'" +", "+  "QUANTITY= " + valueHolder.get(1);
                                }
                            }
                            System.out.println(finalString);
                            appendStrToFile(finalString);
                            String rece = "P" + ";" + finalString + result.substring(result.indexOf("@"));
                            System.out.println(rece);
                            appendStrToFile(rece);
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                System.out.println("I am sending the packet");
                                appendStrToFile("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    } else if (result.startsWith("P") || result.startsWith("Q") || result.startsWith("R")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if(result.startsWith("L")|| result.startsWith("M") || result.startsWith("N")) {
                        System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                        appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                        System.out.println(result);
                        System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                        appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        String finalString = "Items matching your entry at " + getServername() + " are:\n";
                        finalString = finalString + returnItem( vuserID,  vitemName);
                        if(finalString.contains("The item has been added to the library"))
                        {
                            this.interLibraryBlockUsers.remove(vuserID);
                            System.out.println("These are the blocked users"+ this.interLibraryBlockUsers);

                        }
                        String rece = "T" + ";" + finalString + result.substring(result.indexOf("@"));
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else if (result.startsWith("S") || result.startsWith("T") || result.startsWith("U")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if(result.startsWith("E"))
                    {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                        boolean parm = this.itemAvailabilityCheck(vitemID);
                        String rece = null;
                        if(parm == true) {
                            System.out.println(" The item is available");
                            appendStrToFile(" The item is available \n");
                            rece = "G" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                        }
                        else
                        {
                            System.out.println(" The item is not available");
                            appendStrToFile(" The item is not available \n");
                            rece = "G" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                        }
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("G")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }else if(result.startsWith("J"))
                    {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                        boolean parm = this.itemBorrowedCheck(vuserID, vitemID);
                        String rece = null;
                        if(parm == true) {
                            System.out.println(" The item is available");
                            appendStrToFile(" The item is available \n");
                            rece = "K" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                        }
                        else
                        {
                            System.out.println(" The item is not available");
                            appendStrToFile(" The item is not available \n");
                            rece = "K" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                        }
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("K")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);
                    }
                    else if (result.startsWith("W")) {
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        String finalResult = null;
                        System.out.println(vitemID);
                        System.out.println(vuserID);
                        finalResult = this.addUserInWaitingList(vuserID,vitemID,1);
                        System.out.println("Zzzzzzzzzzzzzzzzz" + finalResult);
                        String rece = "V" + ";" + finalResult + result.substring(result.indexOf("@"));
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else if (result.startsWith("V")) {
                        result = result.trim();
                        String temp1 = result.substring(2, result.indexOf('@'));
                        this.globalString = temp1;
                        System.out.println("At the end " + this.globalString);
                        appendStrToFile("At the end " + this.globalString);

                    }

                }
            }
            if (this.getServername().equals("MONTREALU")) {
                {
                    byte[] b1 = null;
                    b1 = new byte[1024];
                    this.dpr = new DatagramPacket(b1, b1.length);
                    System.out.println("I am open for listen");
                    appendStrToFile("I am open for listen");
                    //InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 8083);
                    //ds1.bind(address);
                    //this.ds1 = new DatagramSocket(8083);
//                    try {
//                        System.out.println("I am testing" +this.getServername());
//                        setDs1(new DatagramSocket(this.universalPort));
//                    } catch (SocketException e) {
//                        e.printStackTrace();
//                    }
                    getDs1().receive(dpr);
                    System.out.println("I am in " + getServername());
                    appendStrToFile("I am in " + getServername());
                    String result = null;
                    result = new String(dpr.getData());
                    result = result.trim();
                    System.out.println(result);
                    System.out.println(result.charAt(0));
                    if (result.startsWith("F")) {
                        System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                        appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                        System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                        appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                        result = result.trim();
                        String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                        String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                        int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                        String finalResult = null;
                        if(this.interLibraryBlockUsers.contains(vuserID))
                        {
                            finalResult = " You alrrady have a book from "+this.servername+ " this transaction cannot be made.";
                        }
                        else {
                            finalResult = this.borrowItem(vuserID, vitemID, vnumberOfDays);
                        }
                        if(finalResult.contains("successfully borrowed"))
                        {
                            this.interLibraryBlockUsers.add(vuserID);
                            System.out.println("These are the blockec users"+this.servername + this.interLibraryBlockUsers);
                        }

                        String rece = "B" + ";" + finalResult + result.substring(result.indexOf("@"));
                        System.out.println("The found matches on this server " + rece);
                        appendStrToFile("The found matches on this server " + rece);
                        byte[] b = (rece + "").getBytes();
                        InetAddress ia = null;
                        try {
                            ia = InetAddress.getLocalHost();
                            this.dps = new DatagramPacket(b, b.length, ia, 9999);
                            //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                            System.out.println("I am sending the packet");
                            appendStrToFile("I am sending the packet");
                            this.ds.send(this.dps);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (result.startsWith("B")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);

                        } else if ((result.startsWith("X")) || (result.startsWith("Y")) || (result.startsWith("Z"))) {
                            System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                            appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                            System.out.println(result);
                            appendStrToFile(result);
                            System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                            appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                            result = result.trim();
                            String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                            String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("@"));
                            String finalString = "No item available with entered name in "+this.getServername();
                            if (vuserID.substring(3, 4).equals("U")) {
                                Set<Map.Entry<String, ArrayList<String>>> tempSet = getLibBooksRec().entrySet();
                                for (Map.Entry<String, ArrayList<String>> entry : tempSet) {
                                    ArrayList<String> valueHolder = entry.getValue();
                                    if (valueHolder.get(0).matches(vitemName) ) {
                                        //&& (valueHolder.get(1) != "0")
                                        //finalString = finalString + "Code: " + entry.getKey() + ", Name: " + valueHolder.get(0) + ", Availability: " + valueHolder.get(1) + "\n";
                                        //finalString = "ITEMNAME: " + valueHolder.get(0) +"ITEMID: " + entry.getKey() +  ", QUANTITY: " + valueHolder.get(1);
                                        finalString = "ITEMNAME="   +"'"+ valueHolder.get(0)+"'" +", "+"ITEMID=" +"'"+ entry.getKey()+"'" +", "+  "QUANTITY= " + valueHolder.get(1);
                                    }
                                }
                                System.out.println(finalString);
                                appendStrToFile(finalString);
                                String rece = "P" + ";" + finalString + result.substring(result.indexOf("@"));
                                System.out.println(rece);
                                appendStrToFile(rece);
                                byte[] b = (rece + "").getBytes();
                                InetAddress ia = null;
                                try {
                                    ia = InetAddress.getLocalHost();
                                    this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                    //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                    System.out.println("I am sending the packet");
                                    appendStrToFile("I am sending the packet");
                                    this.ds.send(this.dps);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        } else if (result.startsWith("P") || result.startsWith("Q") || result.startsWith("R")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);
                        }
                        else if(result.startsWith("L")|| result.startsWith("M") || result.startsWith("N")) {
                            System.out.println("Find the appropriate method to be called and call that using a dummy variable");
                            appendStrToFile("Find the appropriate method to be called and call that using a dummy variable");
                            System.out.println(result);
                            System.out.println("Get back the return string convert it to byte and send it back making prefix as B");
                            appendStrToFile("Get back the return string convert it to byte and send it back making prefix as B");
                            result = result.trim();
                            String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                            String vitemName = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                            String finalString = "Items matching your entry at " + getServername() + " are:\n";
                            finalString = finalString + returnItem( vuserID,  vitemName);
                            if(finalString.contains("The item has been added to the library"))
                            {
                                this.interLibraryBlockUsers.remove(vuserID);
                                System.out.println("These are the blocked users"+ this.interLibraryBlockUsers);

                            }
                            String rece = "U" + ";" + finalString + result.substring(result.indexOf("@"));
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                System.out.println("I am sending the packet");
                                appendStrToFile("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        else if (result.startsWith("S") || result.startsWith("T") || result.startsWith("U")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);
                        }
                        else if(result.startsWith("E"))
                        {
                            result = result.trim();
                            String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                            String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                            //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                            boolean parm = this.itemAvailabilityCheck(vitemID);
                            String rece = null;
                            if(parm == true) {
                                System.out.println(" The item is available");
                                appendStrToFile(" The item is available \n");
                                rece = "G" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                            }
                            else
                            {
                                System.out.println(" The item is not available");
                                appendStrToFile(" The item is not available \n");
                                rece = "G" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                            }
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                System.out.println("I am sending the packet");
                                appendStrToFile("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (result.startsWith("G")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);
                        }
                        else if(result.startsWith("J"))
                        {
                            result = result.trim();
                            String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                            String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                            //int vnumberOfDays = Integer.parseInt(result.substring(result.indexOf("$") + 1, result.indexOf("@")));
                            boolean parm = this.itemBorrowedCheck(vuserID, vitemID);
                            String rece = null;
                            if(parm == true) {
                                System.out.println(" The item is available");
                                appendStrToFile(" The item is available \n");
                                rece = "K" + ";" + "TRUE" + result.substring(result.indexOf("@"));
                            }
                            else
                            {
                                System.out.println(" The item is not available");
                                appendStrToFile(" The item is not available \n");
                                rece = "K" + ";" + "FALSE" + result.substring(result.indexOf("@"));

                            }
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                System.out.println("I am sending the packet");
                                appendStrToFile("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (result.startsWith("K")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);
                        }
                        else if (result.startsWith("W")) {
                            result = result.trim();
                            String vuserID = result.substring(result.indexOf(";") + 1, result.indexOf("#"));
                            String vitemID = result.substring(result.indexOf("#") + 1, result.indexOf("$"));
                            String finalResult = null;
                            finalResult = this.addUserInWaitingList(vuserID,vitemID,1);
                            System.out.println("zzzzzzzzzzzzzzzzzzzz"+finalResult);
                            String rece = "V" + ";" + finalResult + result.substring(result.indexOf("@"));
                            byte[] b = (rece + "").getBytes();
                            InetAddress ia = null;
                            try {
                                ia = InetAddress.getLocalHost();
                                this.dps = new DatagramPacket(b, b.length, ia, 9999);
                                //this.dps = new DatagramPacket(b, b.length, address, Integer.parseInt(result.substring(result.indexOf('|') + 1)));
                                System.out.println("I am sending the packet");
                                appendStrToFile("I am sending the packet");
                                this.ds.send(this.dps);
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (result.startsWith("V")) {
                            result = result.trim();
                            String temp1 = result.substring(2, result.indexOf('@'));
                            this.globalString = temp1;
                            System.out.println("At the end " + this.globalString);
                            appendStrToFile("At the end " + this.globalString);

                        }

                    }
                }
            }
//            this.ds1.close();
//            this.ds1 = new DatagramSocket(null);
        }

    }

    public boolean itemBorrowedCheck(String userID, String itemID)
    {
        if (itemID.substring(0, 3).equals(getServername().substring(0, 3))) {
            ArrayList tempList = new ArrayList<String>();
            tempList = getLendingDetail(itemID);
            System.out.println(tempList);
            if (tempList.contains(userID)) {
                return true;
            } else {
                return false;
            }
        }
        else if (itemID.substring(0, 3).equals("CON"))
        {
            System.out.println("I want to go to Concordia to verify the borrow of the book for the requested user");
            appendStrToFile("I want to go to Concordia to verify the borrow of the book for the requested user\n");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "J" + ";"+ userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8081);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }
        else if (itemID.substring(0, 3).equals("MCG"))
        {
            System.out.println("I want to go to Mcgill to verify the borrow of the book for the requested user");
            appendStrToFile("I want to go to Mcgill to verify the borrow of the book for the requested user\n");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "J" + ";"+ userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8082);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }
        else if (itemID.substring(0, 3).equals("MON"))
        {
            System.out.println("I want to go to MonrealU to verify the borrow of the book for the requested user");
            appendStrToFile("I want to go to MontrealU to verify the borrow of the book for the requested user");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "J" + ";"+ userID + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8083);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }
        return false;

    }
    public boolean itemAvailabilityCheck(String itemID)
    {
        if (itemID.substring(0, 3).equals(getServername().substring(0, 3))) {
            ArrayList tempList = new ArrayList<String>();
            tempList = getBookDetail(itemID);
            if (Integer.parseInt((String) tempList.get(1)) > 0) {
                return true;
            } else {
                return false;
            }
        }
        else if (itemID.substring(0, 3).equals("CON"))
        {
            System.out.println("I want to go to Concordia to check the availability of the book");
            appendStrToFile("I want to go to Concordia to check the availability of the book\n");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "E" + ";" + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8081);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    System.out.println("I am in the availability true check of Concordia");
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    System.out.println("I am in the availability true check of Concordia");
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }


        else if (itemID.substring(0, 3).equals("MCG"))
        {System.out.println("I want to go to Mcgill to check the availability of the book");
            appendStrToFile("I want to go to Mcgill to check the availability of the book\n");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "E" + ";" + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8082);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    System.out.println("I am in the availability true check of Mcgill");
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    System.out.println("I am in the availability false check of Mcgill");
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }

        else if (itemID.substring(0, 3).equals("MON"))
        {
            System.out.println("I want to go to MontrealU to check the availability of the book");
            appendStrToFile("I want to go to MontrealU to check the availability of the book\n");
            System.out.println("UDP for calling the correct server on the client's behalf");
            appendStrToFile("UDP for calling the correct server on the client's behalf\n");
            String i = "E" + ";" + "#" + itemID + "$" + "@" + Integer.toString(this.universalPort) + "|" + Integer.toString(8083);
            byte[] b = (i + "").getBytes();
            System.out.println(i);
            InetAddress ia = null;
            try {
                ia = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            this.dps = new DatagramPacket(b, b.length, ia, 9999);
            try {
                System.out.println("I am trying to send return the request");
                appendStrToFile("I am trying to send the return request\n");
                this.ds.send(dps);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( "The call to the remote server has been made \n");
            appendStrToFile("The call to the remote server has been made \n");
            synchronized (lock) {
                try {
                    lock.wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("");
                System.out.println("I am in " + getServername());
                appendStrToFile("I am in " + getServername()+"\n");
                System.out.println("This is inside the method:" + this.globalString);
                if(this.globalString.contains("TRUE")) {
                    System.out.println("I am in the availability true check of Montreal");
                    this.globalString = null;
                    return true;
                }
                else if (this.globalString.contains("FALSE")) {
                    System.out.println("I am in the availability true check of Montreal");
                    this.globalString = null;
                    return false;
                }
                //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
            }
            //finalString = finalString + "This item does not exist in " + getServername() + ".\n";
        }


        return false;


    }
    @Override
    public void run() {
        try {


            this.interServerInteractor();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**Log generator method
     * @param str
     */
    public static void appendStrToFile(String str)
    {
        try {
            FileWriter fw = new FileWriter("./Server_BaseLog.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(str);
            //System.out.println("Write was successful");
            //bw.newLine();
            bw.close();

        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    public static boolean simulateSoftwareBug = true;
    public String simulateSoftwareBug(String username) {
        if (simulateSoftwareBug) {
            return RequestHandlerConstants.CORRECT;
        } else {
            //alternative implementation in case of software bug
            return RequestHandlerConstants.BUGGY;
        }
    }

    public String simulateCrash(String username,String replicaName) {
        if(replicaName.equalsIgnoreCase("rohit")){
            if (!(RequestHandlerMain.isSimulateCrash("Rohit"))) {
                RequestHandlerMain.setSimulateCrash((!RequestHandlerMain.isSimulateCrash("rohit")),"Rohit");
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

}
