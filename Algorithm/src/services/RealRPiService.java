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
        tcpService.sendMessage(Messages.arduinoCode + Messages.moveRobotForward(steps));  //send to Arduino
        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotMovedForward(steps) + "**");
        if(!returnMessage.equals(Messages.robotMovedForward(steps))) {     //if the return message matches
            System.out.println("The move forward action is unsuccessful");
            return -1;
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

        tcpService.sendMessage(Messages.arduinoCode + Messages.turnRobot(direction));

        String returnMessage = tcpService.readMessage();
        System.out.println("The return message is supposed to be **" + Messages.robotTurned(direction) + "**");
        if(!returnMessage.equals(Messages.robotTurned(direction))) {     //if the return message matches
            System.out.println("The turning action is unsuccessful");
            return -1;
        }
        System.out.println("The turning action is successful");
        robot.turn(direction);
        notifyUIChange();
        robot.printStatus();
        return 0;
    }

    @Override
    public int callibrate() {

        tcpService.sendMessage(Messages.arduinoCode + Messages.callibrate());

        System.out.println("Robot calibrating calibrating");
        robot.printStatus();

        String returnMessage = tcpService.readMessage();
        if(!returnMessage.equals(Messages.callibrated())) {     //if the return message matches
            System.out.println("The callibration action is unsuccessful");
            return -1;
        }
        System.out.println("Robot calibrated!!");
        return 0;

    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
