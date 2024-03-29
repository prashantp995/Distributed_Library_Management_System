import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * This class will act as server (As in assignment 2 ConcordiaServer ) While FrontEndImpl act as
 * ConcordiaServerImpl
 */
public class FrontEndMain {
  static boolean shivamUp = false;
  static boolean prasUp = false;
  static boolean sarveshUp = false;
  static boolean rohitUp = false;
  public static void main(String[] args) {
    try {
      Thread frontEndUDP = new Thread(new FrontEndUDP());
      frontEndUDP.start();
      ORB orb = ORB.init(getServerInfo(), null);
      POA rootpoa =
          (POA) orb.resolve_initial_references("RootPOA");
      rootpoa.the_POAManager().activate();
      FrontEndImpl exportedObj = new FrontEndImpl();
      exportedObj.setORB(orb);
      org.omg.CORBA.Object ref =
          rootpoa.servant_to_reference(exportedObj);
      LibraryService href = LibraryServiceHelper.narrow(ref);
      org.omg.CORBA.Object objRef =
          orb.resolve_initial_references("NameService");
      NamingContextExt ncRef =
          NamingContextExtHelper.narrow(objRef);
      String name = "FE";
      NameComponent path[] = ncRef.to_name(name);
      ncRef.rebind(path, href);
      orb.run(); // TODO:there will bo code to run multithreaded server before running this orb.run()
    } catch (WrongPolicy wrongPolicy) {
      wrongPolicy.printStackTrace();
    } catch (AdapterInactive adapterInactive) {
      adapterInactive.printStackTrace();
    } catch (ServantNotActive servantNotActive) {
      servantNotActive.printStackTrace();
    } catch (CannotProceed cannotProceed) {
      cannotProceed.printStackTrace();
    } catch (NotFound notFound) {
      notFound.printStackTrace();
    } catch (InvalidName invalidName) {
      invalidName.printStackTrace();
    } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
      invalidName.printStackTrace();
    }
  }

  private static String[] getServerInfo() {
    String serverInfo[] = new String[4];

    serverInfo[0] = "-ORBInitialHost";
    serverInfo[1] = "localhost";
    serverInfo[2] = "-ORBInitialPort";
    serverInfo[3] = "8090";

    return serverInfo;
  }
}
