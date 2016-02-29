package services;

import utilities.Messages;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public class RealAndroidService implements AndroidServiceInterface {

    private static RealAndroidService instance;

    private TcpService tcpService = TcpService.getInstance();


    public static RealAndroidService getInstance(){
        if(instance==null)
            instance= new RealAndroidService();
        return instance;
    }

    private RealAndroidService(){}

    @Override
    public int waitToStartExploration() {
        String message = tcpService.readMessage();
        System.out.println("(Real Run)Received start exploring command from Android");
        if(!message.equals(Messages.startExploration())) {     //if the return message matches
            System.out.println("The command from Android to start exploration is not correct");
            return -1;
        }
        System.out.println("Robot starting...");
        return 0;
    }

    @Override
    public int waitToRunShortestPath() {
        return 0;
    }

    @Override
    public int sendMapDescriptor() {
        return 0;
    }
}
