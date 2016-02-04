package controllers;

import models.Robot;
import services.*;

/**
 * Created by Jiaxiang on 4/2/16.
 */
public class MazeExplorer {

    private static MazeExplorer instance = new MazeExplorer();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();

    private RPiServiceInterface rpiService;
    private SensorServiceInterface sensorService;

    private MazeExplorer(){}

    public static MazeExplorer getInstance(){
        if(instance==null)
            instance = new MazeExplorer();
        return instance;
    }

    private void initialiseServices(boolean isRealRun){
        if(isRealRun){
            rpiService = RealRPiService.getInstance();
            sensorService = RealSensorService.getInstance();
        }
        else{
            rpiService = SimuRPiService.getInstance();
            sensorService = SimuSensorService.getInstance();
        }
    }

    public int explore(){

        //****************************//
        //****************************//
        //****************************//
        //need to change false to a variable//
        initialiseServices(false);
        //****************************//
        //****************************//


        while(!controller.isStopped()){
            //update data

            if(sensorService.detectObstacle(robot.getSensors()[0]) == 1)
                robot.turn(1);
            else
                robot.moveForward(1);

            controller.setUpdate(true);
        }
        return 0;
    }
}
