

/**Server Driver to initialize the server objects and delegate UDP servers*/
public class SarveshServerDriver {
    public static void main(String[] args) {
        try{
            // create server
            ServerSARReplica concordia = ServerFactory.getSarveshServerObject("CON");
            ServerSARReplica mcgill = ServerFactory.getSarveshServerObject("MCG");
            ServerSARReplica montreal = ServerFactory.getSarveshServerObject("MON");
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
