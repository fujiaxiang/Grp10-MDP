package sample;

/**
 * Created by Fujitsu on 20/1/2016.
 */
public class Controller {
    private boolean[][] maze;
    //can be stored inside robot model
    private int[] start;//record center of start   ROW,COL
    private int[] goal;//center of goal  ROW,COL

    private int start_goal_size;

    private Robot robot;
    private int[][] previous;//used to store previous location

    public Controller(int row,int col,int[] start,int[] goal,int start_goal_size,int speed){
        maze = new boolean[row][col];
        initializeMaze();

        this.start = start;
        this.goal = goal;
        this.start_goal_size = start_goal_size;

        int[] robot_loc = new int[2];
        robot_loc[0] = start[0];
        robot_loc[1] = start[1];
        robot = new Robot(robot_loc,speed,6,maze);
    }

    private void initializeMaze(){
        for(int i=0;i<maze.length;i++)
            for(int j=0;j<maze[i].length;j++)
                maze[i][j] = false;
    }

    public boolean[][] getMaze(){
        return maze;
    }

    public void setObstacle(int row,int col){
        if(isValidForObstacle(row,col))
            maze[row][col] = true;
    }

    public void setFree(int row,int col){
        maze[row][col] = false;
    }

    //might need another check for start/goal set


    //check whether it's valid to mark as obstacle
    //condition:not in goal/start area
    private boolean isValidForObstacle(int row,int col){
        int half_size = (start_goal_size/2);
        if(start[0] - half_size<=row&&row<=start[0]+half_size)
            if(start[1] - half_size<=col&&col<=start[1]+half_size)
                return false;
        if(goal[0] - half_size<=row&&row<=goal[0]+half_size)
            if(goal[1] - half_size<=col&&col<=goal[1]+half_size)
                return false;
        return true;
    }

    public boolean isValidForStartGoal(boolean setStart,int row,int col){
        int[] relative = {row,col};
        int[] compare  = setStart?goal:start;
        int half_size = (start_goal_size/2)+1;

        System.out.println(Math.abs(relative[0]-compare[0])+" "+Math.abs(relative[1]-compare[1]));
        if(Math.abs(relative[0]-compare[0])<=half_size&&Math.abs(relative[1]-compare[1])<=half_size)
            return false;
        return true;
    }

    public void setStartGoal(boolean setStart,int row,int col){
        int half_size = (start_goal_size/2);
        while(row<half_size)row++;
        while(row>maze.length-1-half_size)row--;

        while(col<half_size)col++;
        while(col>maze[0].length-1-half_size)col--;

        //invalid
        if(!isValidForStartGoal(setStart,row,col))
            return;

        //remove all obstacle
        for(int i=row-half_size;i<row-half_size+start_goal_size;i++)
            for(int j=col-half_size;j<col-half_size+start_goal_size;j++)
                maze[i][j] = false;

        if(setStart){
            start[0] = row;
            start[1] = col;
        }
        else{
            goal[0] = row;
            goal[1] = col;
        }
    }

    //This function returns start location
    //calculation using size of start goal and start/goal location
    //Example :
    // | * | * | * |    |    |    = [0,1,2,
    // | * | * | * |    |    |    = [5,6,7,
    // | * | * | * |    |    |    = [10,11,12,
    public int[] getStartGoalLoc(boolean isStart) {
        int[] data = new int[start_goal_size*start_goal_size];
        int first = (isStart?start[0]*maze[0].length+start[1]:goal[0]*maze[0].length+goal[1]) - start_goal_size/2;//indicate middle row 1st col
        for(int i=0;i<start_goal_size;i++){
            data[i] = first - maze[0].length + i;
            data[i+start_goal_size] = first + i;
            data[i+2*start_goal_size] = first +maze[0].length + i;
        }
        return data;
    }

    public int getStart_goal_size() {
        return start_goal_size;
    }

    //return robot location to paint
    //assumed robot size = 3
    public int[][] getRobotLocation(){
        int[][] location = new int[9][2];
        //calculate robot location base on robot loc(center)+robot size
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                location[i*3+j][0] = robot.getLocation()[0]-1+i;//location+half size
                location[i*3+j][1] = robot.getLocation()[1]-1+j;
            }

        if(robot.getDirection()==6){
            previous = new int[3][2];
            for(int i=0;i<3;i++) {
                previous[i][0] = location[i * 3][0];
                previous[i][1] = location[i * 3][1]-1;
            }
            System.out.println();
        }
        return location;
    }

    public int[][] getPrevious(){
        return previous;
    }

    public void startRobot() {
        robot.explore();
    }

}
