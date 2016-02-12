package utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class GlobalUtilities {


    public static int[] locationParser(int[] origin, int orientation, int steps){

        switch(orientation){
            case Orientation.NORTH:
                origin[0] -= steps;
                break;
            case Orientation.SOUTH:
                origin[0] += steps;
                break;
            case Orientation.EAST:
                origin[1] += steps;
                break;
            case Orientation.WEST:
                origin[1] -= steps;
                break;
            default:
                System.out.println("In class GlobalUtilities, case does not exist. The current orientation is: " + orientation);
                origin[0] = -1;
                origin[1] = -1;
                break;
        }
        return origin;
    }

    public static HashMap<String, int[] > relativeLocation = new HashMap<String, int[]>(){{
        put("topLeft", new int[]{-1, -1});
        put("topCenter", new int[]{-1, 0});
        put("topRight", new int[]{-1, 1});
        put("middleLeft", new int[]{0, -1});
        put("middleCenter", new int[]{0, 0});
        put("middleRight", new int[]{0, 1});
        put("bottomLeft", new int[]{1, -1});
        put("bottomCenter", new int[]{1, 0});
        put("bottomRight", new int[]{1, 1});
    }};

    public static boolean sameLocation(int[] location1, int[] location2){
        return location1[0]==location2[0] && location1[1]==location2[1];
    }

}
