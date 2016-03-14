package services;

import utilities.Messages;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public class RealAndroidService implements AndroidServiceInterface {

    private static RealAndroidService instance;

    private final int TIME_TO_RESEND = 10;

    private TcpService tcpService = TcpService.getInstance();

    public static RealAndroidService getInstance(){
        if(instance==null)
            instance= new RealAndroidService();
        return instance;
    }

    private RealAndroidService(){}

    @Override
    public int waitToStartExploration() {
        System.out.println("Waiting for start exploration command from Android...");
        String message = tcpService.readMessage();
        while(!message.equals(Messages.startExploration())) {     //if the return message matches
            System.out.println("The command from Android to start exploration is not correct");
            message = tcpService.readMessage();
        }
        System.out.println("(Real Run)Received start exploring command from Android");
        System.out.println("Exploration starting...");
        return 0;
    }

    @Override
    public int waitToRunShortestPath() {
        System.out.println("Waiting for start shortest path command from Android...");
        String message = tcpService.readMessage();

        while(!message.equals(Messages.startShortestPath())) {     //if the return message matches
            System.out.println("The command from Android to start shortest path is not correct");
            message = tcpService.readMessage();
        }
        System.out.println("(Real Run)Received start shortest path command from Android");
        System.out.println("Shortest path run starting...");
        return 0;
    }

    @Override
    public int sendObstacleInfo() {

        System.out.println("Sending map info...");

        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.obstacleInfo() + Messages.ANDROID_END_CODE);
//        String returnMessage = tcpService.readMessage();
//        while(!returnMessage.equals(Messages.mapDescriptorReceived())) {     //if the return message matches
//            if(returnMessage.equals(Messages.RESEND_CODE)){
//                try{
//                    Thread.sleep(TIME_TO_RESEND);
//                }catch (InterruptedException ite){
//                    ite.printStackTrace();
//                }
//                tcpService.sendMessage(Messages.ANDROID_CODE + Messages.mapDescriptor());
//                returnMessage = tcpService.readMessage();
//            }else {
//                System.out.println("The map descriptor return message is incorrect");
//                break;
//            }
//        }
        System.out.println("Map info sent!!");
        return 0;
    }

    @Override
    public int sendMapDescriptor() {
        System.out.println("Sending map descriptor...");
        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.mapDescriptor() + Messages.ANDROID_END_CODE);
        System.out.println("Map descriptor sent!!");
        return 0;
    }
}
