package controllers;

import models.Arena;
import models.Robot;

/**
 * Created by Jiaxiang on 5/2/16.
 */
public class PathFinder {
    private static PathFinder instance = new PathFinder();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();
    private Arena virtualMap = new Arena();
    private PathFinder(){}

    //initialize the virtual map, to make surrounding cells of obstacles
    public void initializeVirtualMap(Arena arena){
        virtualMap.setStart(arena.getStart());
        virtualMap.setGoal(arena.getGoal());

        //creating virtual obstacles
        for(int i=0; i<Arena.ROW; i++) {       //for each cell in the map
            for (int j = 0; j < Arena.COL; j++) {
                if (arena.getMaze()[i][j] != Arena.mazeState.freeSpace) {   //if a cell is not empty,
                    for (int m = -1; m <= 1; m++)             //make its surrounding cells an "virtual" obstacle
                        for (int n = -1; n <= 1; n++) {
                            virtualMap.getMaze()[i + m][j + n] = Arena.mazeState.obstacle;
                        }
                }
            }
        }
    }

    public static PathFinder getInstance(){
        if(instance==null)
            instance = new PathFinder();
        return instance;
    }

    private int[][][] aStarStraight(int[] start, int[] goal, int[][] maze){
        int[][] pathCost = new int[maze.length][maze[0].length];
        int[][] heuristics = new int[maze.length][maze[0].length];

        //initializing pathCost and heuristics
        for(int i=0; i<Arena.ROW; i++) {
            for (int j = 0; j < Arena.COL; j++) {
                pathCost[i][j] = Integer.MAX_VALUE;
                heuristics[i][j] = calculateHeuristic(i, j, goal);
            }
        }

        return null;
    }

    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
