package services;

import com.sun.org.apache.xpath.internal.operations.Or;
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
        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveRobotForward(steps) + Messages.ARDUINO_END_CODE);  //send to Arduino

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotMovedForward(steps) + "**");
        while(!returnMessage.equals(Messages.robotMovedForward(steps))) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveRobotForward(steps) + Messages.ARDUINO_END_CODE);  //send to Arduino
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The move forward return message is incorrect");
                break;
            }
        }

        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.moveRobotForward(steps) + Messages.ANDROID_END_CODE);    //send to Android

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

        //Testing hard code*****************
        if(direction == Orientation.BACK){
            turn(Orientation.RIGHT);
            turn(Orientation.RIGHT);
            return 0;
        }
        //Testing hard code**********

        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnRobot(direction) + Messages.ARDUINO_END_CODE);

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotTurned(direction) + "**");
        while(!returnMessage.equals(Messages.robotTurned(direction))) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnRobot(direction) + Messages.ARDUINO_END_CODE);
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The turning action return message is incorrect");
                break;
            }
        }

        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.turnRobot(direction) + Messages.ANDROID_END_CODE);

        robot.turn(direction);
        notifyUIChange();
        robot.printStatus();
        System.out.println("The turning action is successful");
        return 0;
    }

    @Override
    public int callibrate() {

        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.callibrate() + Messages.ARDUINO_END_CODE);

        System.out.println("Robot calibrating...");
        robot.printStatus();

        String returnMessage = tcpService.readMessage();
        while(!returnMessage.equals(Messages.callibrated())) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.callibrate() + Messages.ARDUINO_END_CODE);
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The callibration action return message is incorrect");
                break;
            }
        }

        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.callibrate() + Messages.ANDROID_END_CODE);

        System.out.println("Robot calibrated!!");
        return 0;

    }

    @Override
    public int turnDegree(double degree) {
        int centiDegree = (int) (degree * 100 + 0.5);
        int direction;
        if(centiDegree < 180 * 100){
            direction = Orientation.RIGHT;
        }else{
            direction = Orientation.LEFT;
            centiDegree = (360 * 100) - centiDegree;
        }
        if(centiDegree>-0.01 && centiDegree<0.01) {
            System.out.println("In class RealRpiService, centiDegree is 0");
            return 0;
        }
        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnDegree(centiDegree, direction) + Messages.ARDUINO_END_CODE);  //send to Arduino

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotTurnedDegree(centiDegree, direction) + "**");
        while(!returnMessage.equals(Messages.robotTurnedDegree(centiDegree, direction))) {     //if the return message matches
            if (returnMessage.equals(Messages.RESEND_CODE)) {
                try {
                    Thread.sleep(TIME_TO_RESEND);
                } catch (InterruptedException ite) {
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.turnDegree(centiDegree, direction) + Messages.ARDUINO_END_CODE);  //send to Arduino
                returnMessage = tcpService.readMessage();
            } else {
                System.out.println("The move forward return message is incorrect");
                break;
            }
        }
        double newOrientation = (robot.getFullOrientation() + degree) % 360;
        robot.setFullOrientation(newOrientation);
        return 0;
    }

    @Override
    public int moveDistance(double distance) {
        int dis = (int) (distance + 0.5);
        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveDistance(dis) + Messages.ARDUINO_END_CODE);  //send to Arduino

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotMovedDistance(dis) + "**");
        while(!returnMessage.equals(Messages.robotMovedDistance(dis))) {     //if the return message matches
            if(returnMessage.equals(Messages.RESEND_CODE)){
                try{
                    Thread.sleep(TIME_TO_RESEND);
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }
                tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.moveDistance(dis) + Messages.ARDUINO_END_CODE);  //send to Arduino
                returnMessage = tcpService.readMessage();
            }else {
                System.out.println("The move forward return message is incorrect");
                break;
            }
        }

//        tcpService.sendMessage(Messages.ANDROID_CODE + Messages.moveRobotForward(steps) + Messages.ANDROID_END_CODE);    //send to Android

//        robot.moveForward(steps);
//        notifyUIChange();
        robot.printStatus();
        System.out.println("The move distance action is successful");
        return 0;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }

//    public static void main(String[] args){
//        System.out.println("375.3 % 360 = " + (375.3 % 360));
//    }
}
