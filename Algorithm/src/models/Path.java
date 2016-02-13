package models;

import algorithms.PathNode;
import algorithms.VirtualMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Jiaxiang on 13/2/16.
 */
public class Path {

    private ArrayList<PathNode> pathNodes = new ArrayList<>();

    private double totalCost = 0;

    public Path(VirtualMap virtualMap, int[] goalIndex){
        PathNode pathNode = virtualMap.getPathNode(goalIndex);
        totalCost = pathNode.pathCost;
        pathNodes.add(pathNode);
        while(pathNode.previousNode != null){
            pathNode = virtualMap.getPathNode(pathNode.previousNode);
            pathNodes.add(pathNode);
        }
        Collections.reverse(pathNodes);
    }

    public void print(){
        System.out.println("The path is: ");
        for(PathNode node : pathNodes){
            System.out.println("Index: " + node.index[0] + ", " + node.index[1] + "; Orientation: " + node.orientation);
        }
    }

    public double getTotalCost(){
        return totalCost;
    }

    public int[] getGoalIndex(){
        return pathNodes.get(pathNodes.size()-1).index;
    }

    public ArrayList<PathNode> getPathNodes(){
        return pathNodes;
    }

//    public double getTotalCost(){
//        int previousOrientation = -1;
//        double totolCost = 0;
//        double stepCost;
//        for(PathNode pathNode : pathNodes){
//
//            if(previousOrientation < 0)
//                continue;
//
//            stepCost =
//        }
//        return 0;
//    }
}
