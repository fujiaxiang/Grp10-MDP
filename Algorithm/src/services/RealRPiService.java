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

        if(returnMessage.equals(Messages.robotMovedForward(steps))) {     //if the return message matches
            robot.moveForward(steps);
            notifyUIChange();
            return 0;
        }
        robot.printStatus();
        return -1;
    }

    @Override
    public int turn(int direction) {

        if(direction == Orientation.FRONT)
            return -1;

        tcpService.sendMessage(Messages.arduinoCode + Messages.turnRobot(direction));

        String returnMessage = tcpService.readMessage();
        if(returnMessage.equals(Messages.robotTurned(direction))) {     //if the return message matches
            robot.turn(direction);
            notifyUIChange();
            return 0;
        }
        robot.printStatus();
        return -1;
    }

    @Override
    public int callibrate() {

        tcpService.sendMessage(Messages.arduinoCode + Messages.callibrate());

        System.out.println("Robot calibrating calibrating");
        robot.printStatus();

        String returnMessage = tcpService.readMessage();
        if(returnMessage.equals(Messages.callibrated())) {     //if the return message matches
            System.out.println("Robot calibrated!!");
            return 0;
        }
        return -1;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
