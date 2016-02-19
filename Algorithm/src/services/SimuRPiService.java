package services;

import controllers.Controller;
import models.Robot;
import utilities.GlobalUtilities;
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
            Thread.sleep(1000/Controller.simulationSpeed);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("Robot calibrated!!");
        return 0;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
