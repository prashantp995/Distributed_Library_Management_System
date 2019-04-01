import javafx.scene.chart.PieChart;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class FrontEndUDP implements Runnable {

    @Override
    public void run() {
        DatagramSocket mySocket = null;
        try {
            int port = 9006;
            mySocket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                byte[] collector = new byte[1024];
                DatagramPacket receiver = new DatagramPacket(collector,collector.length);
                mySocket.receive(receiver);
                Thread messageHandler = new Thread(new MessageHandler(mySocket,receiver));
                messageHandler.start();
            }catch(IOException e){
                System.out.println("Input/Output exception");
                e.printStackTrace();
            }
        }
    }
}

class MessageHandler implements Runnable{

    private DatagramSocket mySocket = null;
    private DatagramPacket receiver = null;
    private ByteArrayInputStream byteArrayInputStream;
    private ObjectInputStream ois;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ObjectOutputStream oos;
    private DatagramSocket frontEndSocket;

    InetSocketAddress sequencerAddress = new InetSocketAddress(9090);

    public MessageHandler(DatagramSocket frontEndSocket, DatagramPacket receiver){
        try {
            this.mySocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.receiver = receiver;
        this.frontEndSocket = frontEndSocket;
    }

    @Override
    public void run() {
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteArrayOutputStream);
            byteArrayInputStream = new ByteArrayInputStream(receiver.getData());
            ois = new ObjectInputStream(byteArrayInputStream);
            ClientRequestModel requestModel = (ClientRequestModel) ois.readObject();
            sendRequest(mySocket,requestModel);
            ArrayList<String> replies = new ArrayList<>();
            for (int i = 0 ; i < 3 ; i++){
                byte[] buffer = new byte[1024];
                DatagramPacket messageFromRH = new DatagramPacket(buffer,buffer.length);
                mySocket.receive(messageFromRH);
                String reply = new String(messageFromRH.getData());
                reply = reply.trim();
                System.out.println(reply + " " + i);
                replies.add(reply);
            }
            DatagramPacket response = new DatagramPacket(replies.get(0).getBytes(),replies.get(0).length(),receiver.getAddress(),receiver.getPort());
            frontEndSocket.send(response);
        }catch (IOException | ClassNotFoundException c){
            c.printStackTrace();
        }
    }

    private void sendRequest(DatagramSocket socket, ClientRequestModel call) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(call);
        os.close();
        bs.close();
        byte[] sendBuffer = bs.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length,
                InetAddress.getByName("localhost"), sequencerAddress.getPort());
        socket.send(sendPacket);
    }
}
