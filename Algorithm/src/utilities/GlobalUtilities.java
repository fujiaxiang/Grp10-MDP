package utilities;

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

}
