import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RequestHandlerMain extends Thread {

  DatagramSocket requestHandlerSocket = null;
  int requestHandlerPort = 9002; //change this based on your implementation
  static Logger logger = null;

  public static void main(String[] args) {
    RequestHandlerMain requestHandlerMain = new RequestHandlerMain();
    requestHandlerMain.start();
  }

  @Override
  public void run() {
    byte requestBuffer[] = new byte[1000];
    try {
      requestHandlerSocket = new DatagramSocket(requestHandlerPort);
    } catch (SocketException e) {
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
        //once request received , there should be new unique thread to handle the request.
        //Concurrent Sequencer will handle the request
        ConcurrentRequestHandler concurrentSequencer = new ConcurrentRequestHandler(this, requestReceived);
        concurrentSequencer.start();
      } catch (IOException e) {
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
