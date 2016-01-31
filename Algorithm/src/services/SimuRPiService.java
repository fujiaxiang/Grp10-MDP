package services;

import controllers.Controller;
import models.Robot;
import utilities.GlobalUtilities;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class SimuRPiService implements RPiServiceInterface{

    private Robot robot = Robot.getInstance();

    @Override
    public int moveForward(int steps) {
        for(int i=0; i<steps; i++) {
            try {
                Thread.sleep(50);
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
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Handled exception in class SimuRPiService, moveForward method");
        }
        robot.turn(direction);
        notifyUIChange();
        return 0;
    }

    @Override
    public int callibrate() {
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void notifyUIChange() {
        Controller.getInstance().setUpdate(true);
    }
}
