package algorithms;

import controllers.Controller;
import models.Arena;
import models.Robot;
import utilities.HeapPriorityQueue;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 5/2/16.
 */
public class PathFinder {
    private static PathFinder instance = new PathFinder();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();
    private PathFinder(){}

    public static final double COST_TO_MOVE_ONE_STEP = 1;
    public static final double COST_TO_MAKE_A_TURN = 1;



    public static PathFinder getInstance(){
        if(instance==null)
            instance = new PathFinder();
        return instance;
    }




    private int[][] aStarStraight(int[] start, int[] goal, Arena.mazeState[][] maze, boolean treatUnknownAsObstacle, int startOrientation){
        VirtualMap virtualMap = new VirtualMap(maze, treatUnknownAsObstacle);

        //initializing heuristics
        for(int i=0; i<Arena.ROW; i++) {
            for (int j = 0; j < Arena.COL; j++) {
                virtualMap.getVirtualMap()[i][j].pathCost = calculateHeuristic(i, j, goal);
            }
        }

        //virtualMap.getVirtualMap()[start[0]][start[1]].pathCost = 0;
        virtualMap.getVirtualMap()[start[0]][start[1]].visited = true;

        HeapPriorityQueue<PathNode> queue = new HeapPriorityQueue<PathNode>();
        //expand the first node with pathCost = 1, from the start node
        virtualMap.getVirtualMap()[start[0]][start[1]].expand(0, startOrientation, virtualMap, treatUnknownAsObstacle, queue);

//        error;


        return null;
    }



    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
