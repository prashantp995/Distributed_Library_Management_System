import java.io.IOException;

public class ServerFactory {

  private static ServerSARReplica serverSARReplicaConcordia = null;
  private static ServerSARReplica serverSARReplicaMontreal = null;
  private static ServerSARReplica serverSARReplicaMcGill = null;
  public static boolean simulateCrashSar = false;
  public static boolean simulateCrashPra = false;
  public static boolean simulateCrashShi = false;
  public static boolean simulateCrashRoh = false;
  static boolean shivamServerFlag = false;
  static boolean pras_serverFlag = false;
  private static Server_Base concordiaLib;
  private static Server_Base mcgillLib;
  private static Server_Base montrealuLib;
  private static boolean sarveshIS = true;

  static {
    try {
      concordiaLib = new Server_Base("CONCORDIA");
      mcgillLib = new Server_Base("MCGILL");;
      montrealuLib = new Server_Base("MONTREALU");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static ServerInterface getServerObject(String serverName, String lib) throws Exception {

    switch (serverName) {
      case "Sarvesh":
        if(!simulateCrashSar)
          return getSarveshServerObject(lib);
        return null;
      case "Pras":
        if(!simulateCrashPra)
          return getObjForPrashantReplica(lib);
        return null;
      case "Rohit":
        if(!simulateCrashRoh)
          return getRohitServerObject(lib);
        return null;
      case "Shivam":
        if(!simulateCrashShi) {
          if (!shivamServerFlag) {
            runInterServer();
          }
          switch (lib) {
            case "CON":
            /*if (conServer == null) {
              conServer = new ConServer();
            }*/

              return ConServer.getConcordiaObject();
            case "MCG":
            /*if (mcgServer == null)
              mcgServer = new McgServer();*/

              return McgServer.getMcgillObject();
            case "MON":
           /* if (monServer == null) {
              monServer = new MonServer();
            }*/
              return MonServer.getMonObject();
            default:
              return null;
          }
        }
        return null;
    }
    return null;
  }


  private static ServerInterface getObjForPrashantReplica(String lib) {
    if (!pras_serverFlag) {
      ConcordiaRemoteServiceImpl.getConcordiaObject();
      MonRemoteServiceImpl.getMontrealObject();
      McGillRemoteServiceImpl.getMcGillObject();
      pras_serverFlag = true;
    }
    if (lib.equalsIgnoreCase("CON")) {
      return ConcordiaRemoteServiceImpl.getConcordiaObject();
    } else if (lib.equalsIgnoreCase("MON")) {
      return MonRemoteServiceImpl.getMontrealObject();
    } else if (lib.equalsIgnoreCase("MCG")) {
      return McGillRemoteServiceImpl.getMcGillObject();
    }
    return null;
  }

  public static ServerSARReplica getSarveshServerObject(String lib) {
      if(sarveshIS){
          serverSARReplicaConcordia = new ServerSARReplica("CON");
          serverSARReplicaMcGill = new ServerSARReplica("MCG");
          serverSARReplicaMontreal = new ServerSARReplica("MON");
          ServerFactory.initIS();
          sarveshIS = false;
      }
    switch (lib) {
      case "CON":
        if (serverSARReplicaConcordia == null) {
          serverSARReplicaConcordia = new ServerSARReplica(lib);
          return serverSARReplicaConcordia;
        } else {
          return serverSARReplicaConcordia;
        }
      case "MCG":
        if (serverSARReplicaMcGill == null) {
          serverSARReplicaMcGill = new ServerSARReplica(lib);
          return serverSARReplicaMcGill;
        } else {
          return serverSARReplicaMcGill;
        }
      case "MON":
        if (serverSARReplicaMontreal == null) {
          serverSARReplicaMontreal = new ServerSARReplica(lib);
          return serverSARReplicaMontreal;
        } else {
          return serverSARReplicaMontreal;
        }
      default:
        return null;
    }
  }

  public static Server_Base getRohitServerObject(String lib) {
    if (lib.equalsIgnoreCase("CON")) {
      try {
        concordiaLib = new Server_Base("CONCORDIA");
      } catch (IOException e) {
        e.printStackTrace();
      }
      return concordiaLib;
    } else if (lib.equalsIgnoreCase("MCG")) {
      try {
        mcgillLib = new Server_Base("MCGILL");
      } catch (IOException e) {
        e.printStackTrace();
      }
      return mcgillLib;
    } else if (lib.equalsIgnoreCase("MON")) {
      try {
        montrealuLib = new Server_Base("MONTREALU");
      } catch (IOException e) {
        e.printStackTrace();
      }

      return montrealuLib;
    }
    return null;
  }

  private static void runInterServer(){
    shivamServerFlag = true;
    InterServComServer con = new InterServComServer(3, null, ConServer.getConcordiaObject());
    Thread interServCon = new Thread(con);
    interServCon.start();
    shivamServerFlag = true;
    InterServComServer mcg = new InterServComServer(1, null, McgServer.getMcgillObject());
    Thread interServmcg = new Thread(mcg);
    interServmcg.start();
    shivamServerFlag = true;
    InterServComServer mon = new InterServComServer(2, null, MonServer.getMonObject());
    Thread interServMon = new Thread(mon);
    interServMon.start();
  }

  private static void initIS(){
      Thread concordiaDelegate = new Thread(new Delegate(1301,serverSARReplicaConcordia));
      concordiaDelegate.start();
      System.out.println("Sarvesh Concordia InterSarvesh Started");
      Thread mcgillDelegate = new Thread(new Delegate(1302,serverSARReplicaMcGill));
      mcgillDelegate.start();
      System.out.println("Sarvesh McGill InterSarvesh Started");
      Thread montrealDelegate = new Thread(new Delegate(1303,serverSARReplicaMontreal));
      montrealDelegate.start();
      System.out.println("Sarvesh Montreal InterSarvesh Started");
  }

}
