public class ServerFactory {

  private static ServerSARReplica serverSARReplicaConcordia;
  private static ServerSARReplica serverSARReplicaMontreal;
  private static ServerSARReplica serverSARReplicaMcGill;
  private static ConServer conServer;
  private static MonServer monServer;
  private static McgServer mcgServer;

  public static ServerInterface getServerObject(String serverName, String lib) throws Exception {

    switch (serverName) {
      case "Sarvesh":
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
        }
        break;
      case "Pras":
        return getObjForPrashantReplica(lib);
      case "Rohit":
        break;
      case "Shivam":
        switch (lib){
          case "CON":
            if(conServer==null){
              conServer = new ConServer();
            }
            return conServer;
          case "MCG":
            if(mcgServer==null)
              mcgServer = new McgServer();
            return mcgServer;
          case "MON":
            if(monServer==null){
              monServer=new MonServer();
            }
            return monServer;
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
}
