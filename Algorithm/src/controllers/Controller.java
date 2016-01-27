package controllers;
import models.*;

import java.util.Random;

/**
 * Created by Fujitsu on 20/1/2016.
 */

//Waiting fo Rename
public class Controller {
    private Arena arena;
    private Robot robot;
    private int[][] previous;//used to store previous location
    private int[][] location;
    private boolean isDone;//indicate an action issued is done
    private boolean update;//indicate whether an udpate is needed

    public Controller(){
        boolean[][] maze = new boolean[Arena.ROW][Arena.COL];
        for(int i=0;i<maze.length;i++)
            for(int j=0;j<maze[i].length;j++)
                maze[i][j] = false;

        int[] start = {Arena.ROW-2,1};
        int[] goal = {1,Arena.COL-2};
        this.arena = new Arena(start,goal,maze);
        robot = new Robot(start,1000,6,maze);

        previous = new int[6][2];//Stops using -1 for any
        for(int i=0;i<previous.length;i++)
            previous[i][0] = -1;
        location = new int[9][2];
        for(int i=0;i<location.length;i++)
            location[i][0] = -1;//used as a block to previous
    }

    public boolean[][] getMaze(){
        return arena.getMaze();
    }

    //return true for successful set
    public boolean setObstacle(int row,int col){
        if(!isInStartGoalArea(row,col)) {
            arena.setObstacle(row, col, true);
            return true;
        }
        return false;
    }

    public void setFree(int row,int col){
        arena.setObstacle(row,col,false);
    }

    //in goal/start area
    public boolean isInStartGoalArea(int row,int col){
        int half_size = (Arena.START_GOAL_SIZE/2);
        int[] start = arena.getStart();
        int[] goal = arena.getGoal();
        if(start[0] - half_size<=row&&row<=start[0]+half_size)
            if(start[1] - half_size<=col&&col<=start[1]+half_size)
                return true;
        if(goal[0] - half_size<=row&&row<=goal[0]+half_size)
            if(goal[1] - half_size<=col&&col<=goal[1]+half_size)
                return true;
        return false;
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
                arena.setObstacle(i,j,false);

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

        if(robot.getOrientation()==2){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = location[2*Robot.SIZE+i][0];
                previous[i][1] = location[2*Robot.SIZE+i][1];
            }
        }
        else if(robot.getOrientation()==4){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = location[(i+1) * Robot.SIZE-1][0];
                previous[i][1] = location[(i+1) * Robot.SIZE-1][1];
            }
        }
        else if(robot.getOrientation()==6){
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = location[i * Robot.SIZE][0];
                previous[i][1] = location[i * Robot.SIZE][1];
            }
        }
        else{
            for(int i=0;i<Robot.SIZE;i++) {
                previous[i][0] = location[i][0];
                previous[i][1] = location[i][1];
            }
        }


        for(int i=0;i<Robot.SIZE;i++)
            for(int j=0;j<Robot.SIZE;j++){
                location[i*Robot.SIZE+j][0] = robot.getLocation()[0]-Robot.HALF_SIZE+i;//location+half size
                location[i*Robot.SIZE+j][1] = robot.getLocation()[1]-Robot.HALF_SIZE+j;
            }
        update = false;
        return location;
    }

    public int[][] getPrevious(){
        return previous;
    }

    public void startRobot() {
        isDone = false;
        int[] loc = {arena.getStart()[0],arena.getStart()[1]};
        robot.setLocation(loc);
        for(int i=0;i<previous.length;i++)
            previous[i][0] = -1;
        for(int i=0;i<location.length;i++)
            location[i][0] = -1;//used as a block to previous

        //like Sensor detect
        //update Robot
        //Robot move
        update = true;//to show first update
        int i = 0;
        Random r = new Random();
        while(i++<999999){
            try {
                Thread.sleep(50);//Assume waiting for sensor

                //update data

                //decision making

                //for testing
                int rand = r.nextInt(10);
                if(rand==0) {
                    robot.setOrientation((r.nextInt(4)+1)*2);
                }
                else {
                    if (robot.getOrientation() == 6 && robot.getLocation()[1] + 1 + Robot.HALF_SIZE >= Arena.COL)
                        robot.setOrientation(2);
                    else if (robot.getOrientation() == 2 && robot.getLocation()[0] - 1 - Robot.HALF_SIZE < 0)
                        robot.setOrientation(4);
                    else if (robot.getOrientation() == 4 && robot.getLocation()[1] - 1 - Robot.HALF_SIZE < 0)
                        robot.setOrientation(8);
                    else if (robot.getOrientation() == 8 && robot.getLocation()[0] + 1 + Robot.HALF_SIZE >= Arena.ROW)
                        robot.setOrientation(6);
                    else
                        robot.walk();
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
            update = true;
        }
        isDone = true;
    }
    public int getRobotOrientation(){return robot.getOrientation();}
    public boolean isDone(){return isDone;}
    public boolean needUpdate(){return update;}
    public void updated(){update = false;}
}
