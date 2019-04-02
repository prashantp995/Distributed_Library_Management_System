import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RequestHandlerMain extends Thread {

    MulticastSocket requestHandlerSocket = null;
    int requestHandlerPort; //change this based on your implementation
    static Logger logger = null;
    private static ArrayList<Integer> requestIds = new ArrayList<>();
    ObjectInputStream ois; //To get the clientRequestModel from the packed received.
    ClientRequestModel requestObject;//to get the object in the request received(To check the duplicate request)
    static String replicaName = null;

    public static void main(String[] args) {
        //RequestHandlerMain requestHandlerMain = new RequestHandlerMain(9003);
        //  RequestHandlerMain requestHandlerMain1 = new RequestHandlerMain(9001);
        replicaName = args[0];
        RequestHandlerMain requestHandlerMain2 = new RequestHandlerMain(9001);
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
                logger.info("Sequencer is listening at " + requestHandlerPort);
                requestHandlerSocket.receive(requestReceived);
                ois = new ObjectInputStream(new ByteArrayInputStream(requestReceived.getData()));
                requestObject = (ClientRequestModel) ois.readObject();
                if (requestIds.size() == 0) {
                    requestIds.add(requestObject.getRequestId());
                } else {
                    if (!requestIds.contains(requestObject.getRequestId())) {
                        requestIds.add(requestObject.getRequestId());
                        System.out.println("Request received");
                        String requestReceivedFromSeq = new String(requestReceived.getData());
                        System.out.println(requestReceivedFromSeq.trim());
                        //once request received , there should be new unique thread to handle the request.
                        //Concurrent Sequencer will handle the request
                        ConcurrentRequestHandler concurrentSequencer = new ConcurrentRequestHandler(this,
                                requestReceived);
                        concurrentSequencer.start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
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