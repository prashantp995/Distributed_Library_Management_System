import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class ReplicaManager extends Thread {

    private int port;
    public static int failCountPras =0;
    public static int failCountShivam =0;
    public static int failCountSarvesh =0;
    public static int failCountRohit =0;
    public ReplicaManager(int port){
        this.port = port;
    }

    public static void main(String[] args){
        ReplicaManager replicaManager = new ReplicaManager(Integer.parseInt(args[0]));
        replicaManager.start();
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
                Thread newThread = new Thread(new FailureHandler(mySocket,receiver));
                newThread.start();
            }catch(IOException e){
                System.out.println("Input/Output exception");
                e.printStackTrace();
            }
        }
    }
}
class FailureHandler implements Runnable {


    private DatagramSocket mySocket = null;
    private DatagramPacket receiver = null;
    private final Object lock;

    public FailureHandler(DatagramSocket mySocket,DatagramPacket receiver){
        this.mySocket = mySocket;
        this.receiver = receiver;
        lock = new Object();
    }

    @Override
    public void run() {

    }

}

