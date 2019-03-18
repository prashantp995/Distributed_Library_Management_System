import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ClientUtilities {

  private static final String LOG_DIR = FileSystems.getDefault().getPath(".") + "\\Client\\logs\\";

  public static Logger setupLogger(Logger logger, String fileName, boolean showlogsInConsole)
      throws IOException {

    FileHandler fh;

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

  public static void closeLoggerHandlers(Logger logger) {
    for (Handler h : logger.getHandlers()) {
      h.close();   //must call h.close or a .LCK file will remain.
    }
  }


  public static boolean validateItemIdAndNumberOfDays(String itemId, int numberOfDays) {
    return Client.validateItemId(itemId) && numberOfDays > 0;
  }


  public static boolean validateUserName(String username) {
    return username.length() == 8 &&
        (username.startsWith("CONU")
            || username.startsWith("MCGU")
            || username.startsWith("MONU") || username.startsWith("CONM") || username
            .startsWith("MCGM") || username.startsWith("MONM"));
  }

}
