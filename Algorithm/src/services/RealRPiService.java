package services;

import controllers.Controller;
import models.Robot;
import utilities.Messages;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class RealRPiService implements RPiServiceInterface {

    private static RealRPiService instance;

    private final int TIME_TO_RESEND = 10;

    private TcpService tcpService = TcpService.getInstance();

    private Robot robot = Robot.getInstance();

    public static RealRPiService getInstance(){
        if(instance==null)
            instance= new RealRPiService();
        return instance;
    }

    private RealRPiService(){}

    @Override
    public int moveForward(int steps) {
        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveRobotForward(steps));  //send to Arduino
        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotMovedForward(steps) + "**");
        while(!returnMessage.equals(Messages.robotMovedForward(steps))) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveRobotForward(steps));  //send to Arduino
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The move forward return message is incorrect");
                break;
            }
        }
        robot.moveForward(steps);
        notifyUIChange();
        robot.printStatus();
        System.out.println("The move forward action is successful");
        return 0;
    }

    @Override
    public int turn(int direction) {

        if(direction == Orientation.FRONT)
            return -1;

        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnRobot(direction));

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotTurned(direction) + "**");
        while(!returnMessage.equals(Messages.robotTurned(direction))) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnRobot(direction));
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The turning action return message is incorrect");
                break;
            }
        }
        robot.turn(direction);
        notifyUIChange();
        robot.printStatus();
        System.out.println("The turning action is successful");
        return 0;
    }

//    @Override
//    public int callibrate() {
//
//        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.callibrate());
//
//        System.out.println("Robot calibrating...");
//        robot.printStatus();
//
//        String returnMessage = tcpService.readMessage();
//        while(!returnMessage.equals(Messages.callibrated())) {     //if the return message matches
//            if(returnMessage.equals(Messages.RESEND_CODE)){
//                try{
//                    Thread.sleep(TIME_TO_RESEND);
//                }catch (InterruptedException ite){
//                    ite.printStackTrace();
//                }
//                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.callibrate());
//                returnMessage = tcpService.readMessage();
//            }else {
//                System.out.println("The callibration action return message is incorrect");
//                break;
//            }
//        }
//        System.out.println("Robot calibrated!!");
//        return 0;
//
//    }

    @Override
    public int callibrate() {
        return 0;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
