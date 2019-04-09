import javafx.scene.chart.PieChart;

import javax.xml.ws.Response;
import java.io.*;
import java.net.*;
import java.sql.Time;
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
        long timeSarvesh, timePras, timeRohit,timeShivam;
        timeSarvesh=timePras=timeRohit=timeShivam=1;
        long lastReceived=0;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(byteArrayOutputStream);
            byteArrayInputStream = new ByteArrayInputStream(receiver.getData());
            ois = new ObjectInputStream(byteArrayInputStream);
            ClientRequestModel requestModel = (ClientRequestModel) ois.readObject();

            long startTime = System.currentTimeMillis();
            sendRequest(mySocket,requestModel);
            ArrayList<ResponseModel> replies = new ArrayList<>();
            String reply="";
            for (int i = 0 ; i < 4 ; i++){
                byte[] buffer = new byte[1024];
                DatagramPacket messageFromRH = new DatagramPacket(buffer,buffer.length);
               // mySocket.setSoTimeout(10000*(i+1));
                mySocket.receive(messageFromRH);
                ResponseModel responseFromRH;
                ObjectInputStream iStream ;
                iStream = new ObjectInputStream(new ByteArrayInputStream(messageFromRH.getData()));
                responseFromRH =(ResponseModel) iStream.readObject();
                System.out.println(responseFromRH);
                if(responseFromRH.getReplicaName().equalsIgnoreCase("Sarvesh")){
                    timeSarvesh =   System.currentTimeMillis()-startTime;
                    lastReceived = timeSarvesh;
                }if(responseFromRH.getReplicaName().equalsIgnoreCase("Pras")){
                    timePras =   System.currentTimeMillis()-startTime;
                    lastReceived = timePras;
                }if(responseFromRH.getReplicaName().equalsIgnoreCase("Shivam")){
                    timeShivam =   System.currentTimeMillis()-startTime;
                    lastReceived = timeShivam;
                }if(responseFromRH.getReplicaName().equalsIgnoreCase("Rohit")){
                    timeRohit =  System.currentTimeMillis()-startTime;
                    lastReceived = timeRohit;
                }
                replies.add(responseFromRH);
                if(i==2){
                    i++;
                    GetMajority getMajority = new GetMajority(replies,frontEndSocket,receiver);
                    getMajority.start();
                   // reply = getMajority(replies);
                    //DatagramPacket response = new DatagramPacket(reply.getBytes(),reply.length(),receiver.getAddress(),receiver.getPort());
                    //frontEndSocket.send(response);
                    //notifySoftwareBug();
                    System.out.println(lastReceived);
                    try{
                   mySocket.setSoTimeout(new Integer(Long.toString(lastReceived*3)));
                    mySocket.receive(messageFromRH);
                    }catch(Exception e){
                        String replica="";
                        if (timeSarvesh==1)
                            replica += "Sarvesh";
                        else if(timeRohit==1)
                            replica += "Rohit";
                        else if(timePras==1)
                            replica += "Pras";
                        else if(timeShivam==1)
                            replica += "Shivam";
                        notifyRMAboutHardwareBug("crash"+" "+replica);
                    }finally {
/*
                        System.out.println("need to close socket for "+responseFromRH.getReplicaName());
*/
                    }
                }
            }

        }catch (IOException c){
            c.printStackTrace();
        }catch( ClassNotFoundException c){
            c.printStackTrace();
        }
    }

    private void notifySoftwareBug() {
        String reply;

        if(failCountRohit==1){
            reply = FrontEndConstants.SOFTWARE_FAIL+" Rohit";
            notifyRMAboutSoftwareBug(reply);
        }
        if(failCountShivam==1){
            reply = FrontEndConstants.SOFTWARE_FAIL+" Shivam";
            notifyRMAboutSoftwareBug(reply);
        }
        if(failCountPras==1){
            reply = FrontEndConstants.SOFTWARE_FAIL+" Pras";
            notifyRMAboutSoftwareBug(reply);
        }
        if(failCountSarvesh==1){
            reply = FrontEndConstants.SOFTWARE_FAIL+" Sarvesh";
            notifyRMAboutSoftwareBug(reply);
        }
    }

    private void notifyRMAboutSoftwareBug(String reply) {
        DatagramSocket sendToRM=null;
        try {
            sendToRM = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            DatagramPacket dataPacket = new DatagramPacket(reply.getBytes(),reply.length(), InetAddress.getByName("230.1.1.6"),10001);
            sendToRM.send(dataPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void notifyRMAboutHardwareBug(String reply) {
        DatagramSocket sendToRM=null;
        try {
            sendToRM = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            DatagramPacket dataPacket = new DatagramPacket(reply.getBytes(),reply.length(), InetAddress.getByName("230.1.1.6"),10001);
            sendToRM.send(dataPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMajority(ArrayList<ResponseModel> replies) {
        String result="";
        Map<String, Integer> stringsCount = new HashMap<>();
        for(ResponseModel rm: replies)
        {
            String s = rm.getResponse();
            rm.setResponse(s.toUpperCase());
            s = rm.getResponse();
            Integer c = stringsCount.get(s);
            if(c == null) c = 0;
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
class GetMajority extends Thread {

    private final ArrayList<ResponseModel> replies;
    private final DatagramSocket frontEndSocket;
    private final DatagramPacket receiver;
    private int failCountPras = 0;
    private int failCountShivam = 0;
    private int failCountSarvesh = 0;
    private int failCountRohit = 0;

    public GetMajority(ArrayList<ResponseModel> replies, DatagramSocket frontEndSocket,
                       DatagramPacket receiver) {
        this.replies = replies;
        this.frontEndSocket = frontEndSocket;
        this.receiver = receiver;

    }

    @Override
    public void run() {
        String reply = getMajority(replies);
        DatagramPacket response = new DatagramPacket(reply.getBytes(), reply.length(),
                receiver.getAddress(), receiver.getPort());
        try {
            frontEndSocket.send(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        notifySoftwareBug();
    }

    private String getMajority(ArrayList<ResponseModel> replies) {
        String result = "";
        Map<String, Integer> stringsCount = new HashMap<>();
        for (ResponseModel rm : replies) {
            String s = rm.getResponse();
            rm.setResponse(s.toUpperCase());
            s = rm.getResponse();
            Integer c = stringsCount.get(s);
            if (c == null) {
                c = 0;
            }
            c++;
            stringsCount.put(s, c);
        }
        Map.Entry<String, Integer> mostRepeated = null;
        for (Map.Entry<String, Integer> e : stringsCount.entrySet()) {
            if (mostRepeated == null || mostRepeated.getValue() < e.getValue()) {
                mostRepeated = e;
            }
        }
        if (mostRepeated != null) {
            result = mostRepeated.getKey();
        }
/*
            System.out.println("Most common string: " + mostRepeated.getKey());
*/
        for (ResponseModel rm : replies) {
            if (!rm.getResponse().equalsIgnoreCase(result)) {
                if (rm.getReplicaName().equalsIgnoreCase("Pras")) {
                    failCountPras += 1;
                }
                if (rm.getReplicaName().equalsIgnoreCase("Shivam")) {
                    failCountShivam += 1;
                }
                if (rm.getReplicaName().equalsIgnoreCase("Sarvesh")) {
                    failCountSarvesh += 1;
                }
                if (rm.getReplicaName().equalsIgnoreCase("Rohit")) {
                    failCountRohit += 1;
                }
            }

        }
        return result;
    }

    private void notifySoftwareBug() {
        String reply;

        if (failCountRohit == 1) {
            reply = FrontEndConstants.SOFTWARE_FAIL + " Rohit";
            notifyRMAboutSoftwareBug(reply);
        }
        if (failCountShivam == 1) {
            reply = FrontEndConstants.SOFTWARE_FAIL + " Shivam";
            notifyRMAboutSoftwareBug(reply);
        }
        if (failCountPras == 1) {
            reply = FrontEndConstants.SOFTWARE_FAIL + " Pras";
            notifyRMAboutSoftwareBug(reply);
        }
        if (failCountSarvesh == 1) {
            reply = FrontEndConstants.SOFTWARE_FAIL + " Sarvesh";
            notifyRMAboutSoftwareBug(reply);
        }
    }

    private void notifyRMAboutSoftwareBug(String reply) {
        DatagramSocket sendToRM = null;
        try {
            sendToRM = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            DatagramPacket dataPacket = new DatagramPacket(reply.getBytes(), reply.length(),
                    InetAddress.getByName("230.1.1.6"), 10001);
            sendToRM.send(dataPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
