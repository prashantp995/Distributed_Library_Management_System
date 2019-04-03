import javafx.scene.chart.PieChart;

import javax.xml.ws.Response;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private int failCountPras =0;
    private int failCountShivam =0;
    private int failCountSarvesh =0;
    private int failCountRohit =0;

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
            ArrayList<ResponseModel> replies = new ArrayList<>();
            String reply="";
            for (int i = 0 ; i < 4 ; i++){
                byte[] buffer = new byte[1024];
                DatagramPacket messageFromRH = new DatagramPacket(buffer,buffer.length);
                mySocket.receive(messageFromRH);
                ResponseModel responseFromRH;
                ObjectInputStream iStream ;
                iStream = new ObjectInputStream(new ByteArrayInputStream(messageFromRH.getData()));
                responseFromRH =(ResponseModel) iStream.readObject();
              /*  String reply = new String(messageFromRH.getData());
                reply = reply.trim();
                System.out.println(reply + " " + i);*/
                replies.add(responseFromRH);
                if(i==2){
                    reply = getMajority(replies);
                    DatagramPacket response = new DatagramPacket(reply.getBytes(),reply.length(),receiver.getAddress(),receiver.getPort());
                    frontEndSocket.send(response);
                    notifySoftwareBug();

                    // logic for crash check and check delay of 4th response
                }
            }
            /*DatagramPacket response = new DatagramPacket(reply.getBytes(),reply.length(),receiver.getAddress(),receiver.getPort());
            frontEndSocket.send(response);*/


        }catch (IOException | ClassNotFoundException c){
            c.printStackTrace();
        }
    }

    private void notifySoftwareBug() {
        if(failCountRohit==1){

        }
        if(failCountShivam==1){

        }
        if(failCountPras==1){

        }
        if(failCountSarvesh==1){

        }
    }

    private String getMajority(ArrayList<ResponseModel> replies) {
        String result="";
        Map<String, Integer> stringsCount = new HashMap<>();
        for(ResponseModel rm: replies)
        {
            String s = rm.getResponse();
            Integer c = stringsCount.get(s);
            if(c == null) c = new Integer(0);
            c++;
            stringsCount.put(s,c);
        }
        Map.Entry<String,Integer> mostRepeated = null;
        for(Map.Entry<String, Integer> e: stringsCount.entrySet())
        {
            if(mostRepeated == null || mostRepeated.getValue()<e.getValue())
                mostRepeated = e;
        }
        if(mostRepeated != null)
            result = mostRepeated.getKey();
/*
            System.out.println("Most common string: " + mostRepeated.getKey());
*/
        for(ResponseModel rm : replies){
            if(!rm.getResponse().equalsIgnoreCase(result)){
                if(rm.getReplicaName().equalsIgnoreCase("Pras"))
                    failCountPras+=1;
                if(rm.getReplicaName().equalsIgnoreCase("Shivam"))
                    failCountShivam+=1;
                if(rm.getReplicaName().equalsIgnoreCase("Sarvesh"))
                    failCountSarvesh+=1;
                if(rm.getReplicaName().equalsIgnoreCase("Rohit"))
                   failCountRohit+=1;
            }

        }
        return result;
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
