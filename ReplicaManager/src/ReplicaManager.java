import java.io.*;
import java.net.*;


public class ReplicaManager extends Thread {

    private int port;
    private String replicaName;
    public static int failCountPras =0;
    public static int failCountShivam =0;
    public static int failCountSarvesh =0;
    public static int failCountRohit =0;
    public ReplicaManager(int port, String replicaName){
        this.port = port;
        this.replicaName = replicaName;
    }

    public static void main(String[] args){
        ReplicaManager replicaManager = new ReplicaManager(10001,args[0]);
        replicaManager.start();
    }
    @Override
    public void run() {
        MulticastSocket mySocket = null;
        try {
            mySocket = new MulticastSocket(this.port);
            /*if(RequestHandlerMain.replicaName.equalsIgnoreCase("Rohit")){
                mySocket.setNetworkInterface(NetworkInterface.getByName("en0"));
            }*/
            InetAddress ip = InetAddress.getByName("230.1.1.6");
            mySocket.joinGroup(ip);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                byte collector[] = new byte[1024];
                DatagramPacket receiver = new DatagramPacket(collector,collector.length);
                mySocket.receive(receiver);
                Thread newThread = new Thread(new FailureHandler(mySocket,receiver,this.replicaName));
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
    private String replicaName;
    public FailureHandler(DatagramSocket mySocket,DatagramPacket receiver,String replicaName){
        this.mySocket = mySocket;
        this.receiver = receiver;
        this.replicaName = replicaName;
        lock = new Object();
    }

    @Override
    public void run() {
        String rep = new String(receiver.getData());
        System.out.println("Need to handle software bug"+ rep);
        rep = rep.trim();
        String replica = rep.split(":")[1];
        String failureType = rep.split(":")[0];
        if(failureType.equalsIgnoreCase("software"))
            hadleSoftwareBug(replica);
    }

    private void hadleSoftwareBug(String replica) {
        if(replica.equalsIgnoreCase("pras")){
            ReplicaManager.failCountPras++;
            if(ReplicaManager.failCountPras>=3){
                RequestHandlerMain.setSimulateSoftwareBug(false);
                ReplicaManager.failCountPras = 0;
            }
        }
        if(replica.equalsIgnoreCase("shivam") ){
            ReplicaManager.failCountShivam++;
            if(ReplicaManager.failCountShivam>=2 && this.replicaName.equalsIgnoreCase("shivam")){
                RequestHandlerMain.setSimulateSoftwareBug(false);
                ReplicaManager.failCountShivam=0;
            }

        }
        if(replica.equalsIgnoreCase("rohit")){
            ReplicaManager.failCountRohit++;
        }
        if(replica.equalsIgnoreCase("sarvesh")){
            ReplicaManager.failCountSarvesh++;
        }
    }

}

