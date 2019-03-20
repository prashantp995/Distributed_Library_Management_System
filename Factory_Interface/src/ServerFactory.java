import java.io.IOException;

public class ServerFactory {

  private static ServerSARReplica serverSARReplicaConcordia;
  private static ServerSARReplica serverSARReplicaMontreal;
  private static ServerSARReplica serverSARReplicaMcGill;
  private static ConServer conServer;
  private static MonServer monServer;
  private static McgServer mcgServer;
  private static Server_Base concordiaLib;
  private static Server_Base mcgillLib;
  private static Server_Base montrealuLib;

  public static ServerInterface getServerObject(String serverName, String lib) throws Exception {

    switch (serverName) {
      case "Sarvesh":
        return getSarveshServerObject(lib);
      case "Pras":
        return getObjForPrashantReplica(lib);
      case "Rohit":
        return getRohitServerObject(lib);
      case "Shivam":
        switch (lib) {
          case "CON":
            if (conServer == null) {
              conServer = new ConServer();
            }
            return conServer;
          case "MCG":
            if (mcgServer == null)
              mcgServer = new McgServer();
            return mcgServer;
          case "MON":
            if (monServer == null) {
              monServer = new MonServer();
            }
            return monServer;
          default:
            return null;
        }
    }
    return null;
  }


  private static ServerInterface getObjForPrashantReplica(String lib) {
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

}
