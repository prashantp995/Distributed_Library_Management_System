public class ServerFactory {

  private static ServerSARReplica serverSARReplicaConcordia;
  private static ServerSARReplica serverSARReplicaMontreal;
  private static ServerSARReplica serverSARReplicaMcGill;

  public static ServerInterface getServerObject(String serverName, String lib) {

    switch (serverName){
      case "Sarvesh":
        switch (lib){
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
      case "Prashant":
        switch (lib){
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
      case "Rohit":
        break;
      case "Shivam":
        break;
    }
    return null;
  }
}
