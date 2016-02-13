package algorithms;

import models.Arena;
import utilities.HeapPriorityQueue;
import utilities.Orientation;
import utilities.Updatable;

/**
 * Created by Jiaxiang on 11/2/16.
 */
public class PathNode implements Comparable<PathNode>, Updatable{
    public int[] index;
    public Arena.mazeState state;
    public double pathCost;
    public int heuristics;
    public int[] previousNode;
    public boolean expanded = false;
    public int orientation;
    public boolean pathCostUpdated = false;

    public PathNode(){
        pathCost = Integer.MAX_VALUE;
    }

    public double getTotalCost(){
        return pathCost+heuristics;
    }

    //returns the indices of surrounding nodes
    public int[][] getSurrondingNodeIndices() throws ArrayIndexOutOfBoundsException{
        int[][] surroundingNodes = new int[9][2];

        int n = 0;
        for(int i=-1; i<=1; i++)
            for(int j=-1; j<=1; j++){
                surroundingNodes[n] = new int[]{index[0]+i, index[1]+j};
                n++;
            }

        return surroundingNodes;
    }

    //returns the indices of reachable nodes by a robot
    public int[][] getReachableNodeIndices() throws ArrayIndexOutOfBoundsException{
        int[][] reachableNodes = new int[4][2];

        reachableNodes[0] = new int[]{index[0]-1, index[1]};
        reachableNodes[1] = new int[]{index[0], index[1]+1};
        reachableNodes[2] = new int[]{index[0]+1, index[1]};
        reachableNodes[3] = new int[]{index[0], index[1]-1};

        return reachableNodes;
    }

    @Override
    public int compareTo(PathNode pathNode) {
        if(this.getTotalCost() > pathNode.getTotalCost())
            return 1;
        else if(this.getTotalCost() < pathNode.getTotalCost())
            return -1;
        else
            return 0;
    }

    public int expand(double pathCost, int orientation, VirtualMap virtualMap, boolean treatUnknownAsObstacle, HeapPriorityQueue<PathNode> queue){
        this.pathCost = pathCost;
        this.expanded = true;
        this.orientation = orientation;

        double stepCost;
        PathNode reachableNode;

        //trying to expand the node to the north
        reachableNode = virtualMap.getVirtualMap()[this.index[0]-1][this.index[1]];
        //if this is a empty node
        if(reachableNode.state == Arena.mazeState.freeSpace
                || (!treatUnknownAsObstacle && reachableNode.state == Arena.mazeState.freeSpace)) {
            stepCost = PathFinder.COST_TO_MOVE_ONE_STEP;
            if(orientation == Orientation.EAST || orientation == Orientation.WEST)
                stepCost += PathFinder.COST_TO_MAKE_A_TURN;
            if (pathCost + stepCost < reachableNode.pathCost) {
                if(reachableNode.pathCost == Integer.MAX_VALUE){
                    queue.offer(reachableNode);
                }else {
                    pathCostUpdated = true;
                }

                reachableNode.pathCost = pathCost + stepCost;
                reachableNode.orientation = Orientation.NORTH;
            }
        }

        //trying to expand the node to the east
        reachableNode = virtualMap.getVirtualMap()[this.index[0]][this.index[1]+1];
        //if this is a empty node
        if(reachableNode.state == Arena.mazeState.freeSpace
                || (!treatUnknownAsObstacle && reachableNode.state == Arena.mazeState.freeSpace)) {
            stepCost = PathFinder.COST_TO_MOVE_ONE_STEP;
            if(orientation == Orientation.NORTH || orientation == Orientation.SOUTH)
                stepCost += PathFinder.COST_TO_MAKE_A_TURN;
            if (pathCost + stepCost < reachableNode.pathCost) {
                if(reachableNode.pathCost == Integer.MAX_VALUE){
                    queue.offer(reachableNode);
                }else {
                    pathCostUpdated = true;
                }

                reachableNode.pathCost = pathCost + stepCost;
                reachableNode.orientation = Orientation.EAST;
            }

        }

        //trying to expand the node to the south
        reachableNode = virtualMap.getVirtualMap()[this.index[0]+1][this.index[1]];
        //if this is a empty node
        if(reachableNode.state == Arena.mazeState.freeSpace
                || (!treatUnknownAsObstacle && reachableNode.state == Arena.mazeState.freeSpace)) {
            stepCost = PathFinder.COST_TO_MOVE_ONE_STEP;
            if(orientation == Orientation.EAST || orientation == Orientation.WEST)
                stepCost += PathFinder.COST_TO_MAKE_A_TURN;
            if (pathCost + stepCost < reachableNode.pathCost) {
                if(reachableNode.pathCost == Integer.MAX_VALUE){
                    queue.offer(reachableNode);
                }else {
                    pathCostUpdated = true;
                }

                reachableNode.pathCost = pathCost + stepCost;
                reachableNode.orientation = Orientation.SOUTH;
            }

        }

        //trying to expand the node to the east
        reachableNode = virtualMap.getVirtualMap()[this.index[0]][this.index[1] + 1];
        //if this is a empty node
        if(reachableNode.state == Arena.mazeState.freeSpace
                || (!treatUnknownAsObstacle && reachableNode.state == Arena.mazeState.freeSpace)) {
            stepCost = PathFinder.COST_TO_MOVE_ONE_STEP;
            if(orientation == Orientation.NORTH || orientation == Orientation.SOUTH)
                stepCost += PathFinder.COST_TO_MAKE_A_TURN;
            if (pathCost + stepCost < reachableNode.pathCost) {
                if(reachableNode.pathCost == Integer.MAX_VALUE){
                    queue.offer(reachableNode);
                }else {
                    pathCostUpdated = true;
                }

                reachableNode.pathCost = pathCost + stepCost;
                reachableNode.orientation = Orientation.WEST;
            }

        }

        queue.update();
        return 0;
    }

    @Override
    public boolean needUpdate() {
        return pathCostUpdated;
    }

    @Override
    public String toString() {
        String str = "";
        switch (state) {
            case freeSpace:
                str += "f\t";
                break;
            case obstacle:
                str += "o\t";
                break;
            case unknown:
                str += "u\t";
                break;
            case virtualObstacle:
                str += "v\t";
                break;
            default:
                str += "0\t";
                break;

        }
        return str;
    }
}
