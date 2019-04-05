import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RequestHandlerMain extends Thread {

    MulticastSocket requestHandlerSocket = null;
    int requestHandlerPort; //change this based on your implementation
    static Logger logger = null;
    private final static Stack<Integer> requestIds = new Stack<>();
    ObjectInputStream ois; //To get the clientRequestModel from the packed received.
    ClientRequestModel requestObject;//to get the object in the request received(To check the duplicate request)
    public static String replicaName = null;
    public static boolean simulateSoftwareBug = true;
    public static boolean simulateCrash = true;
    public ArrayList<ClientRequestModel> successfullyExecutedReq = new ArrayList<>();


    public static boolean isSimulateSoftwareBug() {
        return simulateSoftwareBug;
    }

    public static void setSimulateSoftwareBug(boolean simulateSoftwareBug) {
        RequestHandlerMain.simulateSoftwareBug = simulateSoftwareBug;
    }

    public static void setSimulateCrash(boolean simulateCrash){
        RequestHandlerMain.simulateCrash = simulateCrash;
    }

    public static boolean isSimulateCrash() {
        return simulateCrash;
    }


    public static void main(String[] args) {
        //RequestHandlerMain requestHandlerMain = new RequestHandlerMain(9003);
        //  RequestHandlerMain requestHandlerMain1 = new RequestHandlerMain(9001);
        replicaName = args[0];
        RequestHandlerMain requestHandlerMain2 = new RequestHandlerMain(9001);
        ReplicaManager replicaManager = new ReplicaManager(10001,replicaName, requestHandlerMain2);
        replicaManager.start();
        // RequestHandlerMain requestHandlerMain3 = new RequestHandlerMain(9004);
        // requestHandlerMain.start();
        // requestHandlerMain1.start();
        requestHandlerMain2.start();
        // requestHandlerMain3.start();

    }

    public RequestHandlerMain(int requestHandlerPort) {
        this.requestHandlerPort = requestHandlerPort;
    }

    @Override
    public void run() {
        byte requestBuffer[] = new byte[1000];
        try {
            requestHandlerSocket = new MulticastSocket(requestHandlerPort);
            if(replicaName.equalsIgnoreCase("Rohit")){
                requestHandlerSocket.setNetworkInterface(NetworkInterface.getByName("en0"));
            }
            InetAddress ip = InetAddress.getByName("230.1.1.5");
            requestHandlerSocket.joinGroup(ip);
            System.out.println(ip.isMulticastAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            logger = setupLogger(Logger.getLogger("requestHandler"), "requestHandler.log", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
            try {
                System.out.println("RequestHandler is listening at " + requestHandlerPort);
                logger.info("RequestHandler is listening at " + requestHandlerPort);
                requestHandlerSocket.receive(requestReceived);
                ois = new ObjectInputStream(new ByteArrayInputStream(requestReceived.getData()));
                requestObject = (ClientRequestModel) ois.readObject();
                if (requestIds.size() == 0) {
                    requestIds.push(requestObject.getRequestId());
                    System.out.println("First Request received.");
                    String requestReceivedFromSeq = new String(requestReceived.getData());
                    System.out.println(requestReceivedFromSeq.trim());
                    ConcurrentRequestHandler concurrentSequencer = new ConcurrentRequestHandler(this,
                            requestReceived);
                    concurrentSequencer.start();
                } else {
                    System.out.println(requestObject.getRequestId());
                    System.out.println(requestIds.toString());
                    if (!requestIds.contains(requestObject.getRequestId())) { // if request already processed
                        int nextExpectedRequest = requestIds.peek();
                        nextExpectedRequest++;
                        if(nextExpectedRequest == requestObject.getRequestId()){ // if req is equal to expected req
                        requestIds.push(requestObject.getRequestId());
                        System.out.println("Request received");
                        String requestReceivedFromSeq = new String(requestReceived.getData());
                        System.out.println(requestReceivedFromSeq.trim());
                        //once request received , there should be new unique thread to handle the request.
                        //Concurrent Sequencer will handle the request
                        ConcurrentRequestHandler concurrentSequencer = new ConcurrentRequestHandler(this,
                                requestReceived);
                        concurrentSequencer.start();
                        }else{
                            LostPacketModel lostPacketModel = new LostPacketModel(requestObject.getRequestId());
                            byte[] lostPacketArray = lostPacketModel.getByteArrayOfObj();
                            DatagramPacket rm1packet = new DatagramPacket(lostPacketArray, lostPacketArray.length, requestReceived.getAddress(), 9090);
                            DatagramSocket socket = new DatagramSocket();
                            socket.send(rm1packet);
                        }

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void resolveCrashFailure(){
        ConcurrentRequestHandler concurrentSequencer = new ConcurrentRequestHandler(this);
        concurrentSequencer.performOperationsToRecoverFromCrash();
    }
    public static Logger setupLogger(Logger logger, String fileName, boolean showlogsInConsole)
            throws IOException {

        FileHandler fh;
        String LOG_DIR =
                FileSystems.getDefault().getPath(".") + "\\RequestHandler\\logs\\";

        try {
            if (!showlogsInConsole) {
                logger.setUseParentHandlers(false);
            }
            System.out.println(LOG_DIR);
            fh = new FileHandler(LOG_DIR + fileName, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

}