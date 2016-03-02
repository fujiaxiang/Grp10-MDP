package algorithms;

import controllers.Controller;
import models.Arena;
import models.Path;
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
    private AndroidServiceInterface androidService;

    private static final int CALIBRATE_LIMIT = 5;
    private int calibrate_age;

    public static final int DEFAULT_TIME_LIMIT = 6*60*1000;
    public static final double DEFAULT_TARGET_COVERAGE = 1;

    public int TIME_LIMIT = 6 * 60 * 1000;   // 6 minutes
    public double TARGET_COVERAGE = 1;           // 100%
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
            androidService = RealAndroidService.getInstance();
        }
        else{
            rpiService = SimuRPiService.getInstance();
            sensorService = SimuSensorService.getInstance();
            androidService = SimuAndroidService.getInstance();
        }
    }

    public Path explore(boolean isRealRun){

        initialiseServices(isRealRun);

        robot.printStatus();

        int moves = 0; //keeping track of the moves robot has made
        calibrate_age = CALIBRATE_LIMIT;   //initialize calibrate age so that the robot calibrates at the begining

        androidService.waitToStartExploration();

        while(!controller.isStopped()){

            if(controller.getTime()>TIME_LIMIT){
                System.out.println("Explorations exceeds time limit, taking action...");
                notEnoughTimeAction();
                return null;   // need to change if robot want to do something after terminated
            }

            if(robot.getPerceivedArena().coverage()>TARGET_COVERAGE){
                System.out.println("Explorations reached coverage target, taking action...");
                targetCoverageReachedAction();
                return null;   // need to change if robot want to do something after terminated
            }

            observe();

            analyzeAndMove();

            moves++;

            //if the robot comes back to start location after more than or equal to 58 moves, break
            //in order to reach goal and come back to start, 58 is the minimum number of moves
            if(moves>=58 && GlobalUtilities.sameLocation(robot.getLocation(), controller.getArena().getStart()))
                break;

        }

        //**************Testing
//        SecondRoundExploration.getInstance().runToUnknownPlace();
        //*****************

        Path shortestPath = getReadyForShortestPath();
        System.out.println("Exploration completed");

        return shortestPath;

    }

    private void notEnoughTimeAction(){}
    private void targetCoverageReachedAction(){}


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
        System.out.println("The reading is: **" + sensorReadings + "**\n");
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

            //if something goes wrong, retry after 1 milisecond
            try{
                Thread.sleep(1);
            }catch (InterruptedException expt){
                expt.printStackTrace();
            }
            return getSensorReadings();
        }
        markObstaclesOnUI(readings);
        return readings;
    }


//    private void analyzeAndMove(){
//
//        analyzeAndCalibrate();
//
//        if(isLeftEmpty()) {
//            rpiService.turn(Orientation.LEFT);
//            observe();
//            rpiService.moveForward(1);
//
//        } else if(isFrontEmpty()) {
//            rpiService.moveForward(1);
//
//        } else if(isRightEmpty()) {
//            rpiService.turn(Orientation.RIGHT);
//            observe();
//            rpiService.moveForward(1);
//
//        } else {
//            rpiService.turn(Orientation.LEFT);
//
//        }
//
//    }

    private void analyzeAndMove(){

        analyzeAndCalibrate();

        if(isRightEmpty()) {
            rpiService.turn(Orientation.RIGHT);
            observe();
            rpiService.moveForward(1);

        } else if(isFrontEmpty()) {
            rpiService.moveForward(1);

        } else if(isLeftEmpty()) {
            rpiService.turn(Orientation.LEFT);
            observe();
            rpiService.moveForward(1);

        } else {
            rpiService.turn(Orientation.RIGHT);

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
    //and locates the obstacle given a relative location and relative orientation
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

    //after exploring the maze, calculate the ideal path from start zone and turn to the ideal starting orientation
    public Path getReadyForShortestPath(){

        Arena.mazeState[][] maze = robot.getPerceivedArena().getMaze();
        int[] start = robot.getPerceivedArena().getStart();
        int[] goal = robot.getPerceivedArena().getGoal();

        Path pathStartFacingNorth, pathStartFacingEast;

        boolean treatUnknownAsObstacle = true;



        //get path if robot starts facing north
        pathStartFacingNorth = PathFinder.getInstance().aStarStraight(maze, start, goal, treatUnknownAsObstacle, Orientation.NORTH);

        //if path does not exist
        if(pathStartFacingNorth == null){
            treatUnknownAsObstacle = false;
            //get path again
            pathStartFacingNorth = PathFinder.getInstance().aStarStraight(maze, start, goal, treatUnknownAsObstacle, Orientation.NORTH);
        }
        //if path still does not exist even if unknown areas are treated free space
        if(pathStartFacingNorth == null)
            return null;

        //get path if robot starts facing east
        pathStartFacingEast = PathFinder.getInstance().aStarStraight(maze, start, goal, treatUnknownAsObstacle, Orientation.EAST);

        int targetFacingDirection;
        Path idealPath;

        if(pathStartFacingNorth.getTotalCost() < pathStartFacingEast.getTotalCost())
            idealPath = pathStartFacingNorth;
        else
            idealPath = pathStartFacingEast;

        targetFacingDirection = idealPath.getPathNodes().get(0).orientation;

        while(robot.getOrientation() != targetFacingDirection)
            rpiService.turn(Orientation.LEFT);

        //return the ideal path
        return idealPath;
    }

    //notify UI a change is made to the robot
    public void notifyUIChange() {
        controller.setUpdate(true);
    }

    //update UI after getting sensor readings
    private void markObstaclesOnUI(int[] readings){
        controller.detect(readings);
    }


    private void analyzeAndCalibrate(){
        int orientation = checkCalibrate();
        if(orientation >= Orientation.FRONT)
            calibrate(orientation);
    }

    //return (RELATIVE) orientation that can use to calibrate
    //behind is not considered
    //return -1 for unable/unecessary  to calibrate
    private int checkCalibrate(){
        if(calibrate_age++<CALIBRATE_LIMIT) return -1;//still too early to calibrate

        if(locateObstacle("topLeft", Orientation.FRONT)==1&&locateObstacle("topCenter", Orientation.FRONT)==1
                &&locateObstacle("topRight", Orientation.FRONT)==1)
            return Orientation.FRONT;
        else if(locateObstacle("topRight", Orientation.RIGHT)==1&&locateObstacle("middleRight", Orientation.RIGHT)==1
                &&locateObstacle("bottomRight", Orientation.RIGHT)==1)
            return Orientation.RIGHT;
        else if(locateObstacle("topLeft", Orientation.LEFT)==1&&locateObstacle("middleLeft", Orientation.LEFT)==1
                &&locateObstacle("bottomLeft", Orientation.LEFT)==1)
            return Orientation.LEFT;
        return -1;
    }


    private void calibrate(int orientation){
        rpiService.turn(orientation);  //turn to face the wall/long obstacle
        rpiService.callibrate();
        if(orientation != Orientation.FRONT)
            rpiService.turn(Orientation.oppositeOrientation(orientation));
        calibrate_age = 0;
    }
}
