import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class SequencerMain extends Thread implements Serializable {

  Integer sequenceNumber = 0;
  DatagramSocket sequencerSocket = null;
  int replica1Port = 9001;
  int replica2Port = 9002;
  int replica3Port = 9003;
  int replica4Port = 9004;
  int sequencerPort = 9090;

  public static void main(String[] args) {
    SequencerMain sequencerMain = new SequencerMain(); //can not use this. as it is not static
    sequencerMain.start();
  }

  @Override
  public void run() {
    byte requestBuffer[] = new byte[1000];
    try {
      sequencerSocket = new DatagramSocket(sequencerPort);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    while (true) {
      DatagramPacket requestReceived = new DatagramPacket(requestBuffer, requestBuffer.length);
      try {
        System.out.println("Sequencer is listening at " + sequencerPort);
        sequencerSocket.receive(requestReceived);
        //once request received , there should be new unique thread to handle the request.
        //Concurrent Sequencer will handle the request
        ConcurrentSequencer concurrentSequencer = new ConcurrentSequencer(this, requestReceived);
        concurrentSequencer.start();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
