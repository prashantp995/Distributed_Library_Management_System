import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

//Sequencer is not CORBA server , this is why we need to implement concurrent sequencer to handle multiple clients
public class ConcurrentSequencer extends Thread implements Serializable {

  SequencerMain sequencerMain;
  DatagramPacket request;
  ByteArrayInputStream byteArrayInputStream;
  ObjectInputStream ois;
  byte updatedByteArray[];
  InetAddress ip;
  ByteArrayOutputStream byteArrayOutputStream;
  ObjectOutputStream oos;

  public ConcurrentSequencer(SequencerMain sequencerMain, DatagramPacket request) {
    this.sequencerMain = sequencerMain;
    this.request = request;
  }


  @Override
  public void run() {
    try {
      ip = InetAddress.getByName("localhost");
      byteArrayOutputStream = new ByteArrayOutputStream();
      oos = new ObjectOutputStream(byteArrayOutputStream);
      byteArrayInputStream = new ByteArrayInputStream(request.getData());
      ois = new ObjectInputStream(byteArrayInputStream);
      //need to add sequence number in the client request
      ClientRequestModel objForRM = (ClientRequestModel) ois.readObject();
      synchronized (sequencerMain.sequenceNumber) {
        objForRM.setRequestId(++sequencerMain.sequenceNumber);
        //sequencer Added
      }
      //convert to byte updatedByteArray to send replica managers
      oos.writeObject(objForRM);
      updatedByteArray = byteArrayOutputStream.toByteArray();
      forwardRequestToReplica(updatedByteArray);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private void forwardRequestToReplica(byte[] updatedByteArray) {
    try {
      DatagramPacket rm1packet = new DatagramPacket(updatedByteArray, updatedByteArray.length, ip,
          sequencerMain.replica1Port);
      DatagramPacket rm2packet = new DatagramPacket(updatedByteArray, updatedByteArray.length, ip,
          sequencerMain.replica2Port);
      DatagramPacket rm3packet = new DatagramPacket(updatedByteArray, updatedByteArray.length, ip,
          sequencerMain.replica3Port);
      DatagramPacket rm4packet = new DatagramPacket(updatedByteArray, updatedByteArray.length, ip,
          sequencerMain.replica4Port);
      DatagramSocket socket = new DatagramSocket();
      socket.send(rm1packet);
      socket.send(rm2packet);
      socket.send(rm3packet);
      socket.send(rm4packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
