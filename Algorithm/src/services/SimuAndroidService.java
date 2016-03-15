package services;


import models.Robot;
import utilities.Convertor;
import utilities.Messages;

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
        System.out.println("(Simulation) Sent map descriptor to Android");
        return 0;
    }

    @Override
    public int sendObstacleInfo() {
        System.out.println("(Simulation) Sent obstacle info to Android");
//        System.out.println("The obstacle info string is ****" + Messages.obstacleInfo() + "****");
//        System.out.println("The converted back string is " + Convertor.fromObstacleInfo(Messages.obstacleInfo()));
//        if(Robot.getInstance().getPerceivedArena().toObstacleInfoTest().equals(Convertor.fromObstacleInfo(Messages.obstacleInfo()) + " ")){
//            System.out.println("The convertors are correct");
//        }else
//            System.out.println("The convertors are not correct");
//        System.out.println();
        return 0;
    }
}
