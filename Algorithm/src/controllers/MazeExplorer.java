package controllers;

import models.Arena;
import models.Robot;
import models.Sensor;
import services.*;

import static utilities.GlobalUtilities.locationParser;

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

    //initialises the two types of services based on simulation or real run
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

            if(getSensorReadings()[0] == 1)
                robot.turn(1);
            else
                robot.moveForward(1);

            controller.setUpdate(true);
//            try{
//                Thread.sleep(1000);
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
        }
        return 0;
    }


    private void observe(){
        int[] readings = getSensorReadings();
        Sensor[] sensors = robot.getSensors();
        for(int i=0; i<robot.getSensors().length; i++){
            markMaze(sensors[i], readings[i]);
        }
    }

    private void markMaze(Sensor sensor, int steps){
        try {
            if (steps < 0) {
                for (int i = sensor.getMinRange(); i <= sensor.getMaxRange(); i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i + 1);
                    robot.getPerceivedArena().setObstacle(location[0], location[1], false);
                }
            } else if (sensor.getMinRange() <= steps && steps <= sensor.getMaxRange()) {
                for (int i = sensor.getMinRange(); i < steps; i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i + 1);
                    robot.getPerceivedArena().setObstacle(location[0], location[1], false);
                }
                int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), steps + 1);
                robot.getPerceivedArena().setObstacle(location[0], location[1], true);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        notifyUIChange();
    }

    private int[] getSensorReadings(){
        String sensorReadings = SimuSensorService.getInstance().detect();
        String[] parts = sensorReadings.split(":");
        int[] readings = new int[parts.length];
        try{
            int i = 0;
            for(String part : parts)
                readings[i] =  Integer.parseInt(part);
        }catch (Exception e){
            e.printStackTrace();

            //if something goes wrong, retry after 0.5 second
            try{
                Thread.sleep(500);
            }catch (InterruptedException expt){
                expt.printStackTrace();
            }
            return getSensorReadings();
        }
        return readings;
    }


    private void analyze(){}


    public void notifyUIChange() {
        controller.setUpdate(true);
    }


}
