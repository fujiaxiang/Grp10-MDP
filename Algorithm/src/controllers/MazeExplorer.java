package controllers;

import models.Arena;
import models.Robot;
import models.Sensor;
import services.*;
import utilities.GlobalUtilities;
import utilities.Orientation;

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
        //need to change this to a variable//
        initialiseServices(false);
        //****************************//
        //****************************//

        robot.printStatus();
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
        String sensorReadings = sensorService.detect();
        System.out.println("The reading is: " + sensorReadings + "\n");
        String[] parts = sensorReadings.split(":");
        int[] readings = new int[parts.length];
        try{
            int i = 0;
            for(String part : parts) {
                readings[i] = Integer.parseInt(part);
                i++;
            }
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

//        if(getSensorReadings()[1] == 1)
//            rpiService.turn(1);
//        else
//            rpiService.moveForward(1);

        if(isRightEmpty()) {
            rpiService.turn(Orientation.RIGHT);
            observe();
            rpiService.moveForward(1);
//            System.out.println("Right is empty");
        } else if(isFrontEmpty()) {
            rpiService.moveForward(1);
//            System.out.println("Front is empty");
        } else if(isLeftEmpty()) {
            rpiService.turn(Orientation.LEFT);
            observe();
            rpiService.moveForward(1);
//            System.out.println("Left is empty");
        } else {
            rpiService.turn(Orientation.RIGHT);
//            System.out.println("Nowhere is empty");
        }

    }

    private boolean isRightEmpty(){
        if(locateObstacle("topRight", Orientation.RIGHT)==1||locateObstacle("middleRight", Orientation.RIGHT)==1
                ||locateObstacle("bottomRight", Orientation.RIGHT)==1)
            return false;
        return true;
    }

    private boolean isFrontEmpty(){
        if(locateObstacle("topLeft", Orientation.FRONT)==1||locateObstacle("topCenter", Orientation.FRONT)==1
                ||locateObstacle("topRight", Orientation.FRONT)==1)
            return false;
        return true;
    }

    private boolean isLeftEmpty(){
        if(locateObstacle("topLeft", Orientation.LEFT)==1||locateObstacle("middleLeft", Orientation.LEFT)==1
                ||locateObstacle("bottomLeft", Orientation.LEFT)==1)
            return false;
        return true;
    }

    //This method reads data stored in Robot.getInstance().getPerceivedArena() rather than sensor readings
    //and locates the obstacle given a absolute relative location and relative orientation
    private int locateObstacle(String relativeLocation, int relativeOrientation) {
        Arena.mazeState[][] maze = robot.getPerceivedArena().getMaze();
        for(int step=1; step<Math.max(Arena.ROW,Arena.COL); step++){
            try{
                int[] absoluteLocation = toAbsoluteLocation(relativeLocation);
                int absoluteOrientation = Orientation.turn(relativeOrientation, robot.getOrientation());
                int[] tempLocation = locationParser(absoluteLocation, absoluteOrientation, step);
                Arena.mazeState obstacle = maze[tempLocation[0]][tempLocation[1]];
                if(obstacle!=Arena.mazeState.freeSpace)
                    return step;
            }catch (ArrayIndexOutOfBoundsException e){
                return step;
            }
        }
        return -1;
    }

    public int[] toAbsoluteLocation(String relativeLocation){
        int[] absoluteLocation= new int[2];
        int[] rotatedRelativeLocation = Orientation.rotateCoordinates(GlobalUtilities.relativeLocation.get(relativeLocation), robot.getOrientation());
        absoluteLocation[0] = robot.getLocation()[0] + rotatedRelativeLocation[0];
        absoluteLocation[1] = robot.getLocation()[1] + rotatedRelativeLocation[1];
        return absoluteLocation;
    }


    public void notifyUIChange() {
        controller.setUpdate(true);
    }


}
