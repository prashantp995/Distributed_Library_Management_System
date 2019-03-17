import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class ReplicaManager implements Runnable {

    private int port;

    public ReplicaManager(int port){
        this.port = port;
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

