package models;

import algorithms.PathNode;
import algorithms.VirtualMap;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import utilities.GlobalUtilities;

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

    public Path(){};

    public void print(){
        System.out.println("The path is: ");
        for(PathNode node : pathNodes){
            System.out.println("Index: " + node.index[0] + ", " + node.index[1] + "; Orientation: " + node.orientation);
        }
    }

    public double getTotalCost(){
        return totalCost;
    }

    public double getDiagonalPathCost(){
        final double costToMakeATurn = 0.8;
        final double costToRunOneMiliMeter = 0.01;
        double distance = 0;
        for(int i=0; i<this.getPathNodes().size() - 1; i++){
            distance += GlobalUtilities.relativeDistance(this.getPathNodes().get(i).index, this.getPathNodes().get(i+1).index);
        }
        return  costToMakeATurn * (getPathNodes().size()-2) + distance * costToRunOneMiliMeter;
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
