public class ServerFactory {

  private static ServerSARReplica serverSARReplicaConcordia;

  public static ServerInterface getServerObject(String serverName, String lib) {
    if (serverName.equalsIgnoreCase("Sarvesh")) {
      if (serverSARReplicaConcordia == null) {
        serverSARReplicaConcordia = new ServerSARReplica(lib);
        return serverSARReplicaConcordia;
      } else {
        return serverSARReplicaConcordia;
      }

    }
    return null;
  }
}
