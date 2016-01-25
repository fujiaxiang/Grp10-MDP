package models;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Arena {

    public final int COL = 15;
    public final int ROW = 20;
    public final int START_SIZE = 3;
    public final int GOAL_SIZE = 3;

    private int maze_info[][];

    private static Arena instance = new Arena();

    private int[] start;//indicate start center
    private int[] goal;//indicate goal center
    private boolean[][] maze;//indicate whether the grid is a obstacle  , true = obstacle
    private Arena(){}

    public Arena(int[] start,int[] goal,boolean[][] maze){
        this.start = start;
        this.goal = goal;
        this.maze = maze;
    }

    public static Arena getInstance(){
        if(instance==null)
            instance = new Arena();
        return instance;
    }

    public int[][] getMaze_info() {
        return maze_info;
    }
}
