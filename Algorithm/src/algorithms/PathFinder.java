package algorithms;

import controllers.Controller;
import models.Arena;
import models.Path;
import models.Robot;
import utilities.GlobalUtilities;
import utilities.HeapPriorityQueue;
import utilities.Orientation;

import java.util.ArrayList;

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

        virtualMap.printShortestPath(path);

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
        if(thisNode.state == Arena.mazeState.freeSpace || thisNode.state == Arena.mazeState.path) {
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

    public Path diagonalPath(Arena.mazeState[][] maze, boolean treatUnknownAsObstacle, Path path){
        VirtualMap virtualMap = new VirtualMap(maze, treatUnknownAsObstacle);
        Path diagonalPath = new Path();
        PathNode node = path.getPathNodes().get(0);
        diagonalPath.getPathNodes().add(node);

        //code to test reachable() method
//        System.out.println(reachable(virtualMap.getPathNode(new int[]{1,1}), virtualMap.getPathNode(new int[]{9,7}), virtualMap));

        int n = 0;
        while(n < path.getPathNodes().size()){
            int i = n + 1;
            while(i < path.getPathNodes().size()){
                if(reachable(node, path.getPathNodes().get(i), virtualMap))
                    n = i;
                i++;
            }
            node = path.getPathNodes().get(n);
            diagonalPath.getPathNodes().add(node);
            if(n == path.getPathNodes().size() - 1)
                break;
        }

        return diagonalPath;
    }

    public Path getDiagonalPath(Path straightShortestPath,VirtualMap virtualMap){
        Path diagonalPath = new Path();
        diagonalPath.getPathNodes().add(straightShortestPath.getPathNodes().get(0));//push first node into Path
        prepareDiagonalPath(straightShortestPath,diagonalPath,virtualMap,0);
        return diagonalPath;
    }

    private void prepareDiagonalPath(Path straightPath,Path diagonalPath,VirtualMap virtualMap,int current_index){
        int last_reachable = -1;

        PathNode currentNode = straightPath.getPathNodes().get(current_index);
        PathNode targetNode;
        //idea:
        //Assume 3 point allocate at straight shortest Path,ABC
        //and there exists 1 reachable point D between BC
        //AD+DC distance is not possible lesser than AC
        for(int i=current_index+1;i<straightPath.getPathNodes().size();i++){
            targetNode = straightPath.getPathNodes().get(i);
            if(reachable(currentNode,targetNode,virtualMap))
                last_reachable = i;
        }
        diagonalPath.getPathNodes().add(straightPath.getPathNodes().get(last_reachable));
        if(last_reachable==straightPath.getPathNodes().size()-1)//means next reachable node is actually goal
            return;
        prepareDiagonalPath(straightPath,diagonalPath,virtualMap,last_reachable);//start from next node
    }



    private boolean reachable(PathNode fromNode, PathNode toNode, VirtualMap virtualMap){
        double fX = fromNode.index[0] + 0.5;
        double fY = fromNode.index[1] + 0.5;
        double tX = toNode.index[0] + 0.5;
        double tY = toNode.index[1] + 0.5;

        final int sectionNumber = 1000;
        final double errorMargin = 0.01;

        double xSection = (tX - fX)/ sectionNumber;
        double ySection = (tY - fY)/ sectionNumber;

        for(int i = 0; i < sectionNumber; i++){
            double x = fX + i * xSection;
            double y = fY + i * ySection;
            if(!virtualMap.getPathNode(new int[]{(int) x, (int) y}).isNodeFree()
                    || !virtualMap.getPathNode(new int[]{(int) (x-errorMargin), (int) y}).isNodeFree()
                    || !virtualMap.getPathNode(new int[]{(int) x, (int) (y-errorMargin)}).isNodeFree()
                    || !virtualMap.getPathNode(new int[]{(int) (x-errorMargin), (int) (y-errorMargin)}).isNodeFree()){
                return false;
            }
        }
        return true;

//        if(fromNode.index[0] == toNode.index[0] || fromNode.index[1] == toNode.index[1])
//            return true;
//        return false;
    }

//    public static void main(String[] args){
//        System.out.println("****" + (int) 4.000);
//    }


    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
