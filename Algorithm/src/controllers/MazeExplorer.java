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
            observe();
            analyzeAndMove();
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

    /**
     * This function takes in a sensor and the readings it has returned. Based on the sensor location, orientation
     * and its range, the function marks cells in perceivedMaze to be freeSpace or obstacles
     * @param sensor
     * @param steps
     */
    private void markMaze(Sensor sensor, int steps){
        try {
            if (steps < 0) {
                for (int i = sensor.getMinRange(); i <= sensor.getMaxRange(); i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i);
                    robot.getPerceivedArena().setObstacle(location[0], location[1], Arena.mazeState.freeSpace);
                }
            } else if (sensor.getMinRange() <= steps && steps <= sensor.getMaxRange()) {
                for (int i = sensor.getMinRange(); i < steps; i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i);
                    robot.getPerceivedArena().setObstacle(location[0], location[1], Arena.mazeState.freeSpace);
                }
                int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), steps);
                robot.getPerceivedArena().setObstacle(location[0], location[1], Arena.mazeState.obstacle);
            }
        }catch (ArrayIndexOutOfBoundsException e){
//            System.out.println("This message comes from MazeExplorer method markMaze. This is normal and please ignore this message");
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


    private void analyzeAndMove(){
        //update data

        if(getSensorReadings()[0] == 1)
            rpiService.turn(1);
        else
            rpiService.moveForward(1);

    }


    public void notifyUIChange() {
        controller.setUpdate(true);
    }


}
