package services;


/**
 * Created by Jiaxiang on 29/2/16.
 */
public class SimuAndroidService implements AndroidServiceInterface {

    private static SimuAndroidService instance;

    private TcpService tcpService = TcpService.getInstance();


    public static SimuAndroidService getInstance(){
        if(instance==null)
            instance= new SimuAndroidService();
        return instance;
    }

    private SimuAndroidService(){}


    @Override
    public int waitToStartExploration() {
        System.out.println("(Simulation)Received start exploring command from Android");
        return 0;
    }

    @Override
    public int waitToRunShortestPath() {
        try{
            Thread.sleep(1000);
        }catch (InterruptedException ite){
            ite.printStackTrace();
        }
        System.out.println("(Simulation)Received start shortest path from Android");
        return 0;
    }

    @Override
    public int sendMapDescriptor() {
        System.out.println("(Simulation)Sent map descriptor to Android");
        return 0;
    }
}
