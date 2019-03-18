import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class Utilities {

  private static final String LOG_DIR = "C:\\Git\\Distributed_Library_Management_System\\PrashantReplica\\logs";

  public static void startRegistry(int RMIPortNum)
      throws RemoteException {
    try {
      Registry registry = LocateRegistry.getRegistry(RMIPortNum);
      registry.list();
    } catch (RemoteException e) {
      System.out.println("RMI registry cannot be located at port " + RMIPortNum);
      Registry registry =
          LocateRegistry.createRegistry(RMIPortNum);
      System.out.println("RMI registry created at port " + RMIPortNum);
    }
  }

  public static void listRegistry(String registryURL)
      throws RemoteException, MalformedURLException {
    System.out.println("Registry " + registryURL + " contains: ");
    String[] names = Naming.list(registryURL);
    for (int i = 0; i < names.length; i++) {
      System.out.println(names[i]);
    }
  }

  public static Logger setupLogger(Logger logger, String fileName, boolean showlogsInConsole)
      throws IOException {

    FileHandler fh;

    try {
      if (!showlogsInConsole) {
        logger.setUseParentHandlers(false);
      }
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

  public static void closeLoggerHandlers(Logger logger) {
    for (Handler h : logger.getHandlers()) {
      h.close();   //must call h.close or a .LCK file will remain.
    }
  }



}
