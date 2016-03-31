package algorithms;

import com.sun.org.apache.xpath.internal.operations.Or;
import controllers.Controller;
import models.Arena;
import models.Path;
import models.Robot;
import models.Sensor;
import services.*;
import utilities.GlobalUtilities;
import utilities.Messages;
import utilities.Orientation;
import utilities.VoiceOut;

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

    private static final int CALIBRATE_LIMIT = 7;
    //private static final int DOUBLE_CALIBRATE_LIMIT = 5;
    private static final int CALIBRATE_DISTANCE = 1;
//    private int calibrate_age;
//    private int double_calibrate_age;

    private int calibrate_age_ns;
    private int calibrate_age_we;

    public static final int DEFAULT_TIME_LIMIT = 6*60*1000;
    public static final double DEFAULT_TARGET_COVERAGE = 1;
    public static final int IGNORE_DISTANCE = -3004;

    //private int mazeStateOverridenOrientation = -1;

    public int TIME_LIMIT = 6 * 60 * 1000;   // 6 minutes
    public double TARGET_COVERAGE = 1;           // 100%
    private MazeExplorer(){}

    public static MazeExplorer getInstance(){
        if(instance==null)
            instance = new MazeExplorer();
        return instance;
    }

//    public void setMazeStateOverridenOrientation(int mazeStateOverridenOrientation) {
//        this.mazeStateOverridenOrientation = mazeStateOverridenOrientation;
//    }
//
//    public int getMazeStateOverridenOrientation() {
//        return mazeStateOverridenOrientation;
//    }

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
        //calibrate_age = CALIBRATE_LIMIT;   //initialize calibrate age so that the robot calibrates at the begining
        //double_calibrate_age = DOUBLE_CALIBRATE_LIMIT;

        androidService.waitToStartExploration();
        controller.startTimer();

        robot.getPerceivedArena().makeBlocksPath(robot.getRobotBlocks());
        robot.getPerceivedArena().makeBlocksPath(robot.getPerceivedArena().getStartBlocks());
        robot.getPerceivedArena().makeBlocksPath(robot.getPerceivedArena().getGoalBlocks());
        robot.getPerceivedArena().print();
        //robot.getPerceivedArena().makeBlocksFree(Convertor.convertToBlock(Controller.getInstance().getArena().getStart(),Arena.START_GOAL_SIZE));
        //robot.getPerceivedArena().makeBlocksFree(Convertor.convertToBlock(Controller.getInstance().getArena().getGoal(),Arena.START_GOAL_SIZE));

        forcePerformDoubleCalibrate();

        while(!controller.isStopped()){

//            if(controller.getTime()>TIME_LIMIT){
//                System.out.println("Explorations exceeds time limit, taking action...");
//                notEnoughTimeAction();
//                return null;   // need to change if robot want to do something after terminated
//            }
//
//            if(robot.getPerceivedArena().coverage()>TARGET_COVERAGE){
//                System.out.println("Explorations reached coverage target, taking action...");
//                targetCoverageReachedAction();
//                return null;   // need to change if robot want to do something after terminated
//            }
            robot.getPerceivedArena().makeBlocksPath(robot.getRobotBlocks());
            //robot.getPerceivedArena().print();
            observe();
            androidService.sendObstacleInfo();

            analyzeAndMove();

            moves++;

            //if the robot comes back to start location after more than or equal to 58 moves, break
            //in order to reach goal and come back to start, 58 is the minimum number of moves
            if(moves>=58 && GlobalUtilities.sameLocation(robot.getLocation(), controller.getArena().getStart()))
                break;

        }

        //**************Testing
        //SecondRoundExploration.getInstance().runToUnknownPlace(isRealRun);
        //*****************
        VoiceOut.voiceOut("WLExplorationDone.wav");
//        if(HARD_CODE == 1){
////            if(!isRealRun)
////                TcpService.getInstance().connectToHost();
//            TcpService.getInstance().sendMessage(Messages.ANDROID_CODE + "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff\n" +
//                    "\n" +
//                    ";\n" +
//                    "00000000000010002000600002001c4000c00000001f803f000000007001000000000800120ff" + Messages.ANDROID_END_CODE);
//        }else
        androidService.sendMapDescriptor();

        System.out.println("The map string is :******" + robot.getPerceivedArena().toMapDescriptor() + "*******");
        controller.stopTimer();

        Path shortestPath = getReadyForShortestPath();
        System.out.println("Exploration completed");

        return shortestPath;

    }

    private void notEnoughTimeAction(){}
    private void targetCoverageReachedAction(){}


    public void observe(){
        int[] readings = getSensorReadings();
        Sensor[] sensors = robot.getSensors();
        for(int i=0; i<robot.getSensors().length; i++){
            markMaze(sensors[i], readings[i]);
        }

        markObstaclesOnUI(readings);
    }


    /**
     * This function takes in a sensor and the readings it has returned. Based on the sensor location, orientation
     * and its range, the function marks cells in perceivedMaze to be freeSpace or obstacles
     * @param sensor
     * @param steps
     */
    private void markMaze(Sensor sensor, int steps){
        try {
            if(steps == IGNORE_DISTANCE)return;
            Arena arena = robot.getPerceivedArena();
            if (steps < 0) {
                for (int i = sensor.getMinRange(); i <= sensor.getMaxRange(); i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i);

                    //adding maze state type: path
                    if(arena.getMaze()[location[0]][location[1]] == Arena.mazeState.path)
                        continue;

                    if(i != sensor.getMaxRange() /*&& i != sensor.getMaxRange()-1*/) {         //override the original obstacle info only when sensor reading does not equal to max range
                        arena.setObstacle(location[0], location[1], Arena.mazeState.freeSpace);
                    } else if(arena.getMaze()[location[0]][location[1]]== Arena.mazeState.unknown)
                        arena.setObstacle(location[0], location[1], Arena.mazeState.freeSpace);

                }
            } else if (sensor.getMinRange() <= steps && steps <= sensor.getMaxRange()) {
                for (int i = sensor.getMinRange(); i < steps; i++) {
                    int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), i);
                    //adding maze state type: path
                    if(arena.getMaze()[location[0]][location[1]] == Arena.mazeState.path)
                        continue;
                    arena.setObstacle(location[0], location[1], Arena.mazeState.freeSpace);
                }
                int[] location = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), steps);

                //adding maze state type: path
                if(arena.getMaze()[location[0]][location[1]] != Arena.mazeState.path) {
                    if (steps != sensor.getMaxRange()/* && steps != sensor.getMaxRange()-1*/) {      //override the original obstacle info only when sensor reading does not equal to max range
                        arena.setObstacle(location[0], location[1], Arena.mazeState.obstacle);
                    } else if (arena.getMaze()[location[0]][location[1]] == Arena.mazeState.unknown)
                        arena.setObstacle(location[0], location[1], Arena.mazeState.obstacle);

                }
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
//            rpiService.turn(Orientation.RIGHT);
//
//        }
//
//    }

//    public static final int HARD_CODE = 1;
    private void analyzeAndMove(){

        //if(getMazeStateOverridenOrientation()>=Orientation.NORTH){
        preemptRobotCircling(Orientation.RIGHT);
        //}
        analyzeAndCalibrate();

//        if(HARD_CODE==1 && robot.getLocation()[0]==10 && robot.getLocation()[1] == 8){
//            rpiService.turn(Orientation.LEFT);
//        }

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
            rpiService.turn(Orientation.LEFT);
            //rpiService.turn(Orientation.RIGHT);
            System.out.println("DEFAULT CASE");
        }

    }

    private void preemptRobotCircling(int orientation){
        //setMazeStateOverridenOrientation(-1);
        if(isRightEmpty()&&isBottomRigthCornerEmpty()){
            //System.out.println("*************Inside function preemptRobotCircling*******");
            rpiService.turn(orientation);
            observe();
            rpiService.moveForward(1);
            observe();
            if(!isFrontEmpty()){
                rpiService.turn(Orientation.LEFT);
                return;
            }
            preemptRobotCircling(Orientation.FRONT);
        }
    }

    private boolean isBottomRigthCornerEmpty(){
        //System.out.println("*********Inside isBottomRightCornerEmpty function*******");
        try {
            int[] rightSide = GlobalUtilities.locationParser(robot.getLocation(), Orientation.turn(robot.getOrientation(), Orientation.RIGHT), Robot.HALF_SIZE + 1);
            int[] bottomRightCorner = GlobalUtilities.locationParser(rightSide, Orientation.oppositeOrientation(robot.getOrientation()), Robot.HALF_SIZE + 1);
            Arena.mazeState state = robot.getPerceivedArena().getMaze()[bottomRightCorner[0]][bottomRightCorner[1]];
            if (state == Arena.mazeState.freeSpace || state == Arena.mazeState.path) {
                //System.out.println("isBottomRightCorner");
                return true;
            }
        }catch (ArrayIndexOutOfBoundsException aiobe){
            //System.out.println("Handled exception in MazeExplorer.isBottomRightCornerEmpty");
            //aiobe.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isRightEmpty(){
        if(locateObstacle("topRight", Orientation.RIGHT,true)==1||locateObstacle("middleRight", Orientation.RIGHT,true)==1
                ||locateObstacle("bottomRight", Orientation.RIGHT,true)==1)
            return false;
        return true;
    }

    private boolean isFrontEmpty(){
        if(locateObstacle("topLeft", Orientation.FRONT,true)==1||locateObstacle("topCenter", Orientation.FRONT,true)==1
                ||locateObstacle("topRight", Orientation.FRONT,true)==1)
            return false;
        return true;
    }

    private boolean isLeftEmpty(){
        if(locateObstacle("topLeft", Orientation.LEFT,true)==1||locateObstacle("middleLeft", Orientation.LEFT,true)==1
                ||locateObstacle("bottomLeft", Orientation.LEFT,true)==1)
            return false;
        return true;
    }

    private boolean isDeadEnd(){
        int orientation = robot.getOrientation();
        return false;
    }

    //This method reads data stored in Robot.getInstance().getPerceivedArena() rather than sensor readings
    //and locates the obstacle given a relative location and relative orientation
    private int locateObstacle(String relativeLocation, int relativeOrientation,boolean treatUnknownAsObstacle) {
        Arena.mazeState[][] maze = robot.getPerceivedArena().getMaze();
        for(int step=1; step<Math.max(Arena.ROW,Arena.COL); step++){
            try{
                int[] absoluteLocation = toAbsoluteLocation(relativeLocation);
                int absoluteOrientation = Orientation.turn(relativeOrientation, robot.getOrientation());
                int[] tempLocation = locationParser(absoluteLocation, absoluteOrientation, step);
                Arena.mazeState obstacle = maze[tempLocation[0]][tempLocation[1]];
                if(obstacle!=Arena.mazeState.freeSpace && obstacle!=Arena.mazeState.path)
                    if(treatUnknownAsObstacle)
                        return step;
                    else {
                        if(obstacle==Arena.mazeState.unknown)
                            return -1;
                        else
                            return step;
                    }
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

        if(robot.getOrientation()==Orientation.SOUTH)
            rpiService.turn(Orientation.RIGHT);
        forcePerformDoubleCalibrate();  //force a double calibration before shortest

        targetFacingDirection = idealPath.getPathNodes().get(0).orientation;

        while(robot.getOrientation() != targetFacingDirection)
            rpiService.turn(Orientation.LEFT);

        //take the first move before running shortest path, as the center of robot is still within start zone after 1 move
        if(robot.getOrientation() == idealPath.getPathNodes().get(1).orientation)
            rpiService.moveForward(1);

        //if the second move is turning, then do this as well
        if(robot.getOrientation() != idealPath.getPathNodes().get(2).orientation) {
            int direction = Orientation.whichDirectionToTurn(idealPath.getPathNodes().get(2).orientation, robot.getOrientation());
            rpiService.turn(direction);
        }
        idealPath = PathFinder.getInstance().aStarStraight(maze, robot.getLocation(), goal, treatUnknownAsObstacle, robot.getOrientation());
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

    private void analyzeAndCalibrate(boolean isNS){
        if(isNS)
            calibrate_age_ns++;
        else
            calibrate_age_we++;
        int orientation = checkCalibrate(isNS);
        System.out.println("age ns:"+calibrate_age_ns+" age we:"+calibrate_age_we+" Orientation: "+orientation);
        if(orientation>=Orientation.FRONT)
            calibrate(orientation);
    }
    private void analyzeAndCalibrate(){
        //int orientation;
        analyzeAndCalibrate(true);
        analyzeAndCalibrate(false);
//        orientation = checkDoubleCalibrate();
//        if(orientation>= Orientation.FRONT){
//            doubleCalibrate(orientation);
//            return;
//        }
//
//        //unable to perform double, check if single is possible
//        orientation = checkCalibrate();
//        if(orientation >= Orientation.FRONT)
//            calibrate(orientation);
    }

    //we will skip using behind for calibration
//    private int checkDoubleCalibrate(){
//        if(double_calibrate_age++<DOUBLE_CALIBRATE_LIMIT) return -1;
//        if(locateObstacle("topLeft", Orientation.FRONT)==CALIBRATE_DISTANCE&&locateObstacle("topCenter", Orientation.FRONT)==CALIBRATE_DISTANCE
//                &&locateObstacle("topRight", Orientation.FRONT)==CALIBRATE_DISTANCE){
//            if(locateObstacle("topRight", Orientation.RIGHT)==CALIBRATE_DISTANCE&&locateObstacle("middleRight", Orientation.RIGHT)==CALIBRATE_DISTANCE
//                    &&locateObstacle("bottomRight", Orientation.RIGHT)==CALIBRATE_DISTANCE)
//                return Orientation.RIGHT;
//            if(locateObstacle("topLeft", Orientation.LEFT)==CALIBRATE_DISTANCE&&locateObstacle("middleLeft", Orientation.LEFT)==CALIBRATE_DISTANCE
//                    &&locateObstacle("bottomLeft", Orientation.LEFT)==CALIBRATE_DISTANCE)
//                return Orientation.LEFT;
//        }
//        return -1;
//    }

    //return (RELATIVE) orientation that can use to calibrate
    //behind is not considered
    //return -1 for unable/unecessary  to calibrate
//    private int checkCalibrate(){
////        if(calibrate_age++<CALIBRATE_LIMIT) return -1;//still too early to calibrate
////
////        if(locateObstacle("topLeft", Orientation.FRONT)==CALIBRATE_DISTANCE&&locateObstacle("topCenter", Orientation.FRONT)==CALIBRATE_DISTANCE
////                &&locateObstacle("topRight", Orientation.FRONT)==CALIBRATE_DISTANCE)
////            return Orientation.FRONT;
////        else if(locateObstacle("topRight", Orientation.RIGHT)==CALIBRATE_DISTANCE&&locateObstacle("middleRight", Orientation.RIGHT)==CALIBRATE_DISTANCE
////                &&locateObstacle("bottomRight", Orientation.RIGHT)==CALIBRATE_DISTANCE)
////            return Orientation.RIGHT;
////        else if(locateObstacle("topLeft", Orientation.LEFT)==CALIBRATE_DISTANCE&&locateObstacle("middleLeft", Orientation.LEFT)==CALIBRATE_DISTANCE
////                &&locateObstacle("bottomLeft", Orientation.LEFT)==CALIBRATE_DISTANCE)
////            return Orientation.LEFT;
////        else if(locateObstacle("bottomLeft", Orientation.BACK)==CALIBRATE_DISTANCE&&locateObstacle("bottomCenter", Orientation.BACK)==CALIBRATE_DISTANCE
////                &&locateObstacle("bottomRight", Orientation.BACK)==CALIBRATE_DISTANCE)
////            return Orientation.BACK;
////        System.out.println(locateObstacle("bottomLeft", Orientation.BACK) + " " + locateObstacle("bottomCenter", Orientation.BACK)+ " " + locateObstacle("bottomRight", Orientation.BACK));
////        return -1;
//        if(calibrate_age_ns<CALIBRATE_LIMIT&&calibrate_age_we<CALIBRATE_LIMIT) return -1;//still too early to calibrate
//
//        if(calibrate_age_ns>=CALIBRATE_LIMIT){
//            if(locateObstacle("topLeft", Orientation.FRONT)==CALIBRATE_DISTANCE&&locateObstacle("topCenter", Orientation.FRONT)==CALIBRATE_DISTANCE
//                    &&locateObstacle("topRight", Orientation.FRONT)==CALIBRATE_DISTANCE)
//                return Orientation.FRONT;
//            else if(locateObstacle("bottomLeft", Orientation.BACK)==CALIBRATE_DISTANCE&&locateObstacle("bottomCenter", Orientation.BACK)==CALIBRATE_DISTANCE
//                    &&locateObstacle("bottomRight", Orientation.BACK)==CALIBRATE_DISTANCE)
//                return Orientation.BACK;
//            System.out.println((locateObstacle("bottomLeft", Orientation.BACK)+" "+locateObstacle("bottomCenter", Orientation.BACK)+" "+locateObstacle("bottomRight", Orientation.BACK)+" "));
//        }
//
//        if(calibrate_age_we>=CALIBRATE_LIMIT) {
//            if (locateObstacle("topRight", Orientation.RIGHT) == CALIBRATE_DISTANCE && locateObstacle("middleRight", Orientation.RIGHT) == CALIBRATE_DISTANCE
//                    && locateObstacle("bottomRight", Orientation.RIGHT) == CALIBRATE_DISTANCE)
//                return Orientation.RIGHT;
//            else if (locateObstacle("topLeft", Orientation.LEFT) == CALIBRATE_DISTANCE && locateObstacle("middleLeft", Orientation.LEFT) == CALIBRATE_DISTANCE
//                    && locateObstacle("bottomLeft", Orientation.LEFT) == CALIBRATE_DISTANCE)
//                return Orientation.LEFT;
//        }
//        System.out.println(calibrate_age_ns+" "+calibrate_age_we);
//        //System.out.println(locateObstacle("bottomLeft", Orientation.BACK) + " " + locateObstacle("bottomCenter", Orientation.BACK)+ " " + locateObstacle("bottomRight", Orientation.BACK));
//        return -1;
//    }

    private boolean checkCanCalibrateFront(){
        if(locateObstacle("topLeft", Orientation.FRONT,false)==CALIBRATE_DISTANCE&&locateObstacle("topCenter", Orientation.FRONT,false)==CALIBRATE_DISTANCE
                &&locateObstacle("topRight", Orientation.FRONT,false)==CALIBRATE_DISTANCE)
            return true;
        return false;
    }

    private boolean checkCanCalibrateBack(){
        if(locateObstacle("bottomLeft", Orientation.BACK,false)==CALIBRATE_DISTANCE&&locateObstacle("bottomCenter", Orientation.BACK,false)==CALIBRATE_DISTANCE
                &&locateObstacle("bottomRight", Orientation.BACK,false)==CALIBRATE_DISTANCE)
            return true;
        return false;
    }

    private boolean checkCanCalibrateRight(){
        if (locateObstacle("topRight", Orientation.RIGHT,false) == CALIBRATE_DISTANCE && locateObstacle("middleRight", Orientation.RIGHT,false) == CALIBRATE_DISTANCE
                && locateObstacle("bottomRight", Orientation.RIGHT,false) == CALIBRATE_DISTANCE)
            return true;
        return false;
    }

    private boolean checkCanCalibrateLeft(){
        if (locateObstacle("topLeft", Orientation.LEFT,false) == CALIBRATE_DISTANCE && locateObstacle("middleLeft", Orientation.LEFT,false) == CALIBRATE_DISTANCE
                && locateObstacle("bottomLeft", Orientation.LEFT,false) == CALIBRATE_DISTANCE)
            return true;
        return false;
    }

    private int checkCalibrate(boolean isNS) {
        if (calibrate_age_ns < CALIBRATE_LIMIT && calibrate_age_we < CALIBRATE_LIMIT) return -1;
        //if we are checking NS,
        //NS age
        //North South,  if robot facing North/South, direct check, if not orientation = turn(1)
        int orientation = robot.getOrientation();
        if(isNS){
            if(calibrate_age_ns < CALIBRATE_LIMIT) return -1;
            if(orientation==Orientation.WEST||orientation==Orientation.EAST){
                if(checkCanCalibrateRight())return Orientation.RIGHT;
                if(checkCanCalibrateLeft())return Orientation.LEFT;
            }
            else{
                if(checkCanCalibrateFront())return Orientation.FRONT;
                if(checkCanCalibrateBack())return Orientation.BACK;
            }
        }
        else{
            if(calibrate_age_we < CALIBRATE_LIMIT) return -1;
            if(orientation==Orientation.WEST||orientation==Orientation.EAST){
                if(checkCanCalibrateFront())return Orientation.FRONT;
                if(checkCanCalibrateBack())return Orientation.BACK;
            }
            else{
                if(checkCanCalibrateRight())return Orientation.RIGHT;
                if(checkCanCalibrateLeft())return Orientation.LEFT;
            }
        }
        return -1;
    }


    private void calibrate(int orientation){
        rpiService.turn(orientation);  //turn to face the wall/long obstacle
        rpiService.callibrate();
        if(orientation != Orientation.FRONT && orientation != Orientation.BACK)
            rpiService.turn(Orientation.oppositeOrientation(orientation));
        else if(orientation == Orientation.BACK)
            rpiService.turn(Orientation.BACK);

        int calibrated_orientation = Orientation.turn(robot.getOrientation(),orientation);
        if(calibrated_orientation == Orientation.NORTH||calibrated_orientation == Orientation.SOUTH)
            calibrate_age_ns = 0;
        if(calibrated_orientation == Orientation.WEST||calibrated_orientation == Orientation.EAST)
            calibrate_age_we = 0;
        //calibrate_age = 0;
    }

    //Use front,then left/right to calibrate
    private void doubleCalibrate(int orientation){
        calibrate(Orientation.FRONT);
        calibrate(orientation);
        //double_calibrate_age = 0;
        System.out.println("Double Calibrated");
    }

//    private boolean forcePerformDoubleCalibrate(){
//        int[] robot_loc = robot.getLocation();
//        int[] start = controller.getArena().getStart();
//        if(robot_loc[0]==start[0]-1&& //row-1
//                robot_loc[1]==start[1]+1)//if diagonal offset from start
//        {
//            //if(robot.getOrientation()!=Orientation.NORTH&&robot.getOrientation()!=Orientation.EAST)return;
//            if(robot.getOrientation()!=Orientation.WEST)return false;
//            int orientation = robot.getOrientation();
//
//            //force update perceived arena
//            int[] reading = new int[robot.getSensors().length];
//            for(int i=0;i<reading.length;i++)
//                reading[i] = robot.getSensors()[i].getrelativeOrientation()==Orientation.FRONT ? CALIBRATE_DISTANCE : IGNORE_DISTANCE;
//
//            //turn to back
//            for(int i=0;i<2;i++){
//                //rpiService.turn(orientation==Orientation.NORTH?Orientation.LEFT:Orientation.RIGHT);
//                markObstaclesOnUI(reading);
//                for(int j=0;j<reading.length;j++)
//                    if( robot.getSensors()[i].getrelativeOrientation()==Orientation.FRONT ){
//                        markMaze(robot.getSensors()[j],reading[j]);
//                    }
//                analyzeAndCalibrate(i%2==1);
//                if(i==1)rpiService.moveForward(CALIBRATE_DISTANCE-1);//move to wall
//                rpiService.turn(Orientation.LEFT);
//            }
//
//            return true;
//            //rpiService.turn(Orientation.BACK);
//        }
//        return false;
//    }


    private boolean forcePerformDoubleCalibrate(){

        calibrate_age_ns = CALIBRATE_LIMIT;     //force the robot to calibrate
        calibrate_age_we = CALIBRATE_LIMIT;     //force the robot to calibrate

        int[] robot_loc = robot.getLocation();
        int[] start = controller.getArena().getStart();

        if(robot_loc[0]==start[0]&& //row-1
        robot_loc[1]==start[1])//if diagonal offset from start
        {
            //if(robot.getOrientation()!=Orientation.NORTH&&robot.getOrientation()!=Orientation.EAST)return;
            while(robot.getOrientation()!=Orientation.WEST)
                rpiService.turn(Orientation.LEFT);

            //force update perceived arena
            int[] reading = new int[robot.getSensors().length];
//            for(int i=0;i<reading.length;i++)
//                reading[i] = robot.getSensors()[i].getrelativeOrientation()==Orientation.FRONT ? CALIBRATE_DISTANCE : IGNORE_DISTANCE;

            //turn to back
            for(int i=0;i<2;i++){
                //rpiService.turn(orientation==Orientation.NORTH?Orientation.LEFT:Orientation.RIGHT);
                markObstaclesOnUI(reading);
                for(int j=0;j<reading.length;j++)
                    if( robot.getSensors()[i].getrelativeOrientation()==Orientation.FRONT ){
                        markMaze(robot.getSensors()[j],reading[j]);
                    }
                markObstaclesOnUI(reading);
                analyzeAndCalibrate(i%2==1);
                rpiService.turn(Orientation.LEFT);
            }

            return true;
            //rpiService.turn(Orientation.BACK);
        }
        return false;
    }
}
