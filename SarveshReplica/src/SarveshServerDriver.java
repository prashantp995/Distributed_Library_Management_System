

/**Server Driver to initialize the server objects and delegate UDP servers*/
public class SarveshServerDriver {
    public static void main(String[] args) {
        try{
            // create and initialize the ORB //// get reference to rootpoa & activate the POAManager

            // create servant and register it with the ORB
            ServerSARReplica concordia = new ServerSARReplica("CON");
            ServerSARReplica mcgill = new ServerSARReplica("MCG");
            ServerSARReplica montreal = new ServerSARReplica("MON");

            Thread concordiaDelegate = new Thread(new Delegate(1301,concordia));
            concordiaDelegate.start();
            Thread mcgillDelegate = new Thread(new Delegate(1302,mcgill));
            mcgillDelegate.start();
            Thread montrealDelegate = new Thread(new Delegate(1303,montreal));
            montrealDelegate.start();

            System.out.println("All library Servers are ready and waiting ...");

            while(true){}
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
