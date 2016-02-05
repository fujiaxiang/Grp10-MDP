package models;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Arena {

    public static final int COL = 15;
    public static final int ROW = 20;
    public static final int START_GOAL_SIZE = 3;

    public enum mazeState{
        freeSpace, obstacle, unknow
    }

//    private static Arena instance = new Arena();

    private int[] start;//indicate start center
    private int[] goal;//indicate goal center
    private mazeState[][] maze; //indicate whether the grid is a obstacle

    public Arena(){}

    public Arena(int[] start,int[] goal){
        this.start = start;
        this.goal = goal;
        this.maze = new mazeState[ROW][COL];
    }
    public Arena(int[] start,int[] goal, mazeState[][] maze){
        this.start = start;
        this.goal = goal;
        this.maze = maze;
    }

    public void resetToCertainState(mazeState state){
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[0].length; j++)
                maze[i][j] = state;
        }
    }

//    public void initialize(){
//        this.start = start;
//        this.goal = goal;
//        this.maze = maze;
//    }

//    private static Arena getInstance(){
//        if(instance==null)
//            instance = new Arena();
//        return instance;
//    }

    public int[] getStart(){
        return start;
    }
    public int[] getGoal(){
        return goal;
    }

    public void setStart(int[] start){
        this.start = start;
    }

    public void setGoal(int[] goal){
        this.goal = goal;
    }

    public void setObstacle(int row,int col,mazeState state){
        maze[row][col] = state;
    }

    public mazeState[][] getMaze(){
        return maze;
    }
}
