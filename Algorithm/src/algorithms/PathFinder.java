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

        PathNode startNode = virtualMap.getPathNode(start);

        //virtualMap.getVirtualMap()[start[0]][start[1]].pathCost = 0;
        startNode.orientation = startOrientation;
        startNode.pathCost = 0;

        HeapPriorityQueue<PathNode> queue = new HeapPriorityQueue<PathNode>();

        expand(startNode, virtualMap, queue);

        //repeatedly polling from the queue and expand
        PathNode previousNode = startNode;
        while(queue.size()>0){
            PathNode expandingNode = queue.poll();
            expand(expandingNode, virtualMap, queue);
            if(expandingNode.index.equals(start))
                break;
        }
//        error;


        return null;
    }

    public void expand(PathNode thisNode, VirtualMap virtualMap, HeapPriorityQueue<PathNode> queue){
        thisNode.expanded = true;
        for(int[] reachableNodeIndex : thisNode.getReachableNodeIndices()){
            //trying to mark reachable nodes and ignore those that are out of index bound
            try{
                mark(virtualMap.getPathNode(reachableNodeIndex), thisNode, queue);
            }catch (ArrayIndexOutOfBoundsException e){}

        }
    }



    private void mark(PathNode thisNode, PathNode previousNode, HeapPriorityQueue<PathNode> queue){
        if(thisNode.state == Arena.mazeState.freeSpace) {
            int orientation = Orientation.relativeOrientation(thisNode.index, previousNode.index);
            double stepCost = PathFinder.COST_TO_MOVE_ONE_STEP;

            //if previous orientation is the same as relative orientation, then no need to turn
            if(orientation == previousNode.orientation){}

            //if orienation is opposite, turn twice
            else if(orientation == Orientation.oppositeOrientation(previousNode.orientation))
                stepCost += 2 * PathFinder.COST_TO_MAKE_A_TURN;

            //otherwise, turn once
            else
                stepCost += PathFinder.COST_TO_MAKE_A_TURN;

            if (previousNode.pathCost + stepCost < thisNode.pathCost) {
                if(thisNode.pathCost == Integer.MAX_VALUE){
                    queue.offer(thisNode);
                }else {
                    thisNode.pathCostUpdated = true;
                }

                thisNode.pathCost = previousNode.pathCost + stepCost;
                thisNode.orientation = orientation;
            }
        }
    }

    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
