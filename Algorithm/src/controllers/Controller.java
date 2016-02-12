package controllers;
import algorithms.MazeExplorer;
import models.*;
import utilities.Orientation;

/**
 * Created by Fujitsu on 20/1/2016.
 */

//Waiting fo Rename
public class Controller {
    private final Arena arena;
    private final Robot robot;
    private int[][] previous;//used to store previous location
    private int[][] detected;//[sensor]{ROW,COL,ORIENTATION,DISTANCE}
    private int[][] location;
    private boolean isDone;//indicate an action issued is done
    private boolean update;//indicate whether an udpate is needed
    private boolean isStopped;
    public static int simulationSpeed = 10; //10 being the standard speed
    private final int NUMBER_OF_SENSOR = 5;
    private static Controller instance;

    public static final int ABSOLUTE_ROW = 0;
    public static final int ABSOLUTE_COL = 1;
    public static final int ABSOLUTE_ORIENTATION = 2;
    public static final int DISTANCE = 3;
    public static final int DETECT_RANGE = 4;

    public static Controller getInstance(){
        if(instance==null)
            instance = new Controller();
        return instance;
    }

    private Controller(){
        isStopped = true;
        Arena.mazeState[][] maze = new Arena.mazeState [Arena.ROW][Arena.COL];
        for (int i = 0; i < maze.length; i++)
            for (int j = 0; j < maze[i].length; j++)
                maze[i][j] = Arena.mazeState.freeSpace;  //initializing maze to freeSpace
        int[] start = {Arena.ROW-2,1};
        int[] goal = {1,Arena.COL-2};
        arena = new Arena(start, goal, maze);
//        arena.initialize(start,goal,maze);
        robot = Robot.getInstance();
        robot.initialize(start, Orientation.EAST);
        previous = new int[6][2];//Stops using -1 for any
        for(int i=0;i<previous.length;i++)
            previous[i][0] = -1;

        detected = new int[NUMBER_OF_SENSOR][5];//Stops using -1 for any
        for(int i=0;i<detected.length;i++)
            detected[i][0] = -1;

        location = new int[9][2];
        for(int i=0;i<location.length;i++)
            location[i][0] = -1;//used as a block to previous
    }

    public Arena.mazeState[][] getMaze(){
        return arena.getMaze();
    }

    //return true for successful set
    public boolean setObstacle(int row,int col){
        if(isInStartGoalArea(row,col)==0) {
            arena.setObstacle(row, col, Arena.mazeState.obstacle);
            return true;
        }
        return false;
    }

    public void setFree(int row,int col){
        arena.setObstacle(row, col, Arena.mazeState.freeSpace);
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    //in goal/start area
    //return 0 = safe,1 = start,2 = goal
    public int isInStartGoalArea(int row,int col){
        int half_size = (Arena.START_GOAL_SIZE/2);
        int[] start = arena.getStart();
        int[] goal = arena.getGoal();
        if(start[0] - half_size<=row&&row<=start[0]+half_size)
            if(start[1] - half_size<=col&&col<=start[1]+half_size)
                return 1;
        if(goal[0] - half_size<=row&&row<=goal[0]+half_size)
            if(goal[1] - half_size<=col&&col<=goal[1]+half_size)
                return 2;
        return 0;
    }

    public boolean isValidForStartGoal(boolean setStart,int row,int col){
        int[] relative = {row,col};
        int[] compare  = setStart?arena.getGoal():arena.getStart();
        int half_size = (Arena.START_GOAL_SIZE/2)+1;

        System.out.println(Math.abs(relative[0]-compare[0])+" "+Math.abs(relative[1]-compare[1]));
        if(Math.abs(relative[0]-compare[0])<=half_size&&Math.abs(relative[1]-compare[1])<=half_size)
            return false;
        return true;
    }

    public void setStartGoal(boolean setStart,int row,int col){
        int half_size = (Arena.START_GOAL_SIZE/2);
        while(row<half_size)row++;//if number of grids at left side of center<half size
        while(Arena.ROW-1-row<half_size)row--;//if number of grids at right side

        while(col<half_size)col++;//if number of grids at left side of center<half size
        while(Arena.COL-1-col<half_size)col--;//if number of grids at right side

        //invalid
        if(!isValidForStartGoal(setStart,row,col))
            return;

        //remove all obstacle
        //from center - half size to center+ half size
        //half size of 3 = 1
        //center = A
        //A-1,A,A+1(A-1+2)
        for(int i=row-half_size;i<row-half_size+Arena.START_GOAL_SIZE;i++)
            for(int j=col-half_size;j<col-half_size+Arena.START_GOAL_SIZE;j++)
                arena.setObstacle(i,j,Arena.mazeState.freeSpace);

        int[] data = {row,col};
        if(setStart)
            arena.setStart(data);
        else
            arena.setGoal(data);

    }

    //This function returns start location
    //calculation using size of start goal and start/goal location
    //Example :
    // | * | * | * |    |    |    = [0,1,2,
    // | * | * | * |    |    |    = [5,6,7,
    // | * | * | * |    |    |    = [10,11,12,
    public int[] getStartGoalLoc(boolean isStart) {
        int[] data = new int[Arena.START_GOAL_SIZE*Arena.START_GOAL_SIZE];
        int first = (isStart?arena.getStart()[0]*Arena.COL+arena.getStart()[1]:arena.getGoal()[0]*Arena.COL+arena.getGoal()[1]) - Arena.START_GOAL_SIZE/2;//indicate middle row 1st col
        for(int i=0;i<Arena.START_GOAL_SIZE;i++){
            data[i] = first - Arena.COL + i;//upper row
            data[i+Arena.START_GOAL_SIZE] = first + i;//center
            data[i+2*Arena.START_GOAL_SIZE] = first +Arena.COL + i;//lower row
        }
        return data;
    }

    public int getStart_goal_size() {
        return Arena.START_GOAL_SIZE;
    }

    //return robot location to paint
    //assumed robot size = 3
    public int[][] getRobotLocation(){
        //calculate robot location base on robot loc(center)+robot size
        for(int i=0;i<Robot.SIZE;i++)
            for(int j=0;j<Robot.SIZE;j++){
                location[i*Robot.SIZE+j][0] = robot.getLocation()[0]-Robot.HALF_SIZE+i;//location+half size
                location[i*Robot.SIZE+j][1] = robot.getLocation()[1]-Robot.HALF_SIZE+j;
            }
        update = false;
        return location;
    }

    public void savePrevious(){
        int orientation = robot.getOrientation();
        if(orientation==Orientation.NORTH){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = robot.getLocation()[0]+1;
                previous[i][1] = robot.getLocation()[1]-1+i;
            }
        }
        else if(orientation==Orientation.WEST){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = robot.getLocation()[0]-1+i;
                previous[i][1] = robot.getLocation()[1]+1;
            }
        }
        else if(orientation==Orientation.EAST){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = robot.getLocation()[0]-1+i;
                previous[i][1] = robot.getLocation()[1]-1;
            }
        }
        else{
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = robot.getLocation()[0]-1;
                previous[i][1] = robot.getLocation()[1]-1+i;
            }
        }
    }
    public int[][] getPrevious(){
        return previous;
    }


    public void startRobot() {
        isDone = false;
        isStopped = false;

        for(int i=0;i<previous.length;i++)
            previous[i][0] = -1;
        for(int i=0;i<location.length;i++)
            location[i][0] = -1;//used as a block to previous

        update = false;
        //update = true;//to show first update

        MazeExplorer.getInstance().explore();
        isDone = true;
    }

    public int[][] getDetected(){
        return detected;
    }


    public void setRobotLocation(int[] loc){robot.setLocation(loc);}
    public int getRobotOrientation(){return robot.getOrientation();}
    public int getRobotOrientation1357(){
        int orientation;
        switch(getRobotOrientation()){
            case Orientation.NORTH:orientation = 1;break;
            case Orientation.WEST:orientation = 3;break;
            case Orientation.EAST:orientation = 5;break;
            case Orientation.SOUTH:orientation = 7;break;
            default : orientation = 0;
        }
        return orientation;
    }
    public void detect(int sensor,int absolute_row,int absolute_col,int orientation,int distance,int detect_range){
        detected[sensor][ABSOLUTE_ROW] = absolute_row;
        detected[sensor][ABSOLUTE_COL] = absolute_col;
        detected[sensor][ABSOLUTE_ORIENTATION] = orientation;
        detected[sensor][DISTANCE] = distance;
        detected[sensor][DETECT_RANGE] = detect_range;
    }

    public void setRobotOrientation(int orientation){robot.setOrientation(orientation);}
    public boolean isDone(){return isDone;}
    public boolean isStopped(){return isStopped;}
    public boolean needUpdate(){return update;}
    public void updated(){update = false;}
    public void setSimulationSpeed(int speed){simulationSpeed = speed;}
    public Arena getArena() {
        return arena;
    }
}
