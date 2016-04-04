package services;

import controllers.Controller;
import models.Robot;
import utilities.GlobalUtilities;
import utilities.Messages;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class SimuRPiService implements RPiServiceInterface{

    private static SimuRPiService instance;

    public static SimuRPiService getInstance(){
        if(instance==null)
            instance= new SimuRPiService();
        return instance;
    }

    private SimuRPiService(){}

    private final Robot robot = Robot.getInstance();

    @Override
    public int moveForward(int steps) {
        for(int i=0; i<steps; i++) {
            try {
                Thread.sleep(500/Controller.simulationSpeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("Handled exception in class SimuRPiService, moveForward method");
            }
            robot.moveForward(1);
            notifyUIChange();
        }
        robot.printStatus();
        return 0;
    }

    @Override
    public int turn(int direction) {
        if(direction == Orientation.FRONT)
            return -1;
        try {
            Thread.sleep(500/Controller.simulationSpeed);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Handled exception in class SimuRPiService, moveForward method");
        }
        robot.turn(direction);
        notifyUIChange();
        robot.printStatus();
        return 0;
    }

    @Override
    public int callibrate() {
        System.out.println("Robot calibrating calibrating");
        robot.printStatus();
        try {
            Thread.sleep(2000/Controller.simulationSpeed);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Robot calibrated!!");
        return 0;
    }

    @Override
    public int moveDistance(double distance) {
        int dis = (int) distance;
        System.out.println("****Message sent: " + Messages.ARDUINO_CODE + Messages.moveDistance(dis) + Messages.ARDUINO_END_CODE + "****");  //send to Arduino
        return 0;
    }

    @Override
    public int turnDegree(double degree) {
        int deg = (int) (degree + 0.5);
        int direction;
        if(deg < 180){
            direction = Orientation.RIGHT;
        }else{
            direction = Orientation.LEFT;
            deg = 360 - deg;
        }
        System.out.println("****Message sent: " + Messages.ARDUINO_CODE + Messages.turnDegree(deg, direction) + Messages.ARDUINO_END_CODE + "****");  //send to Arduino

        return 0;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
