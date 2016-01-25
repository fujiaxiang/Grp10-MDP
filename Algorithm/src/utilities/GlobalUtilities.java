package utilities;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class GlobalUtilities {

    public enum Orientation {
        NORTH, SOUTH, EAST, WEST
    };


    public static int[] locationParser(int[] origin, Orientation orientation, int steps){

        switch(orientation){
            case NORTH:
                origin[0] -= steps;
                break;
            case SOUTH:
                origin[0] += steps;
                break;
            case EAST:
                origin[1] += steps;
                break;
            case WEST:
                origin[1] -= steps;
                break;
            default:
                System.out.println("In class SimuSensorService, case does not exist");
                origin[0] = -1;
                origin[1] = -1;
                break;
        }
        return origin;
    }

}
