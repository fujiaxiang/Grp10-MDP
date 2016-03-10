package algorithms;

import controllers.Controller;
import models.Arena;
import models.Path;
import models.Robot;
import utilities.GlobalUtilities;
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



    public Path aStarStraight(Arena.mazeState[][] maze, int[] start, int[] goal, boolean treatUnknownAsObstacle, int startOrientation){

        VirtualMap virtualMap = new VirtualMap(maze, treatUnknownAsObstacle);

        //initializing heuristics
        for(int i=0; i<Arena.ROW; i++) {
            for (int j = 0; j < Arena.COL; j++) {
                virtualMap.getVirtualMap()[i][j].heuristics = calculateHeuristic(i, j, goal);
            }
        }

        PathNode startNode = virtualMap.getPathNode(start);

        startNode.orientation = startOrientation;
        startNode.pathCost = 0;
        startNode.previousNode = null;

        HeapPriorityQueue<PathNode> queue = new HeapPriorityQueue<PathNode>();

        expand(startNode, virtualMap, queue);

        PathNode expandingNode = startNode;

        while(queue.size()>0){    //repeatedly polling from the queue and expand
            expandingNode = queue.poll();

            //*********debugging code
            //virtualMap.printExpanded();

            expand(expandingNode, virtualMap, queue);

            if(GlobalUtilities.sameLocation(expandingNode.index, goal)) {
                System.out.println("Reached goal");
                break;
            }
        }

        if(!GlobalUtilities.sameLocation(expandingNode.index, goal))
            return null;

        Path path = new Path(virtualMap, goal);

        return path;
    }


    //expand a node and mark its reachables nodes
    public void expand(PathNode thisNode, VirtualMap virtualMap, HeapPriorityQueue<PathNode> queue){
        thisNode.expanded = true;
//        System.out.println("****Expanded index: " + thisNode.index[0] + ", " + thisNode.index[1]);
        for(int[] reachableNodeIndex : thisNode.getReachableNodeIndices()){
            //trying to mark reachable nodes and ignore those that are out of index bound
            try{
                mark(virtualMap.getPathNode(reachableNodeIndex), thisNode, queue);
            }catch (ArrayIndexOutOfBoundsException e){}
        }

        queue.update();
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
                }
                thisNode.pathCostUpdated = true;
                thisNode.pathCost = previousNode.pathCost + stepCost;
                thisNode.previousNode = previousNode.index;
                thisNode.orientation = orientation;
            }
        }
    }


    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
