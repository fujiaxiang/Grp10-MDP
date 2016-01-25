package utilities;

/**
 * Created by Jiaxiang on 25/1/16.
 */
public class Orientation {

    public static final int NORTH = 0;

    public static final int EAST = 1;

    public static final int SOUTH = 2;

    public static final int WEST = 3;


    public static final int FRONT = 0;

    public static final int RIGHT = 1;

    public static final int BACK = 2;

    public static final int LEFT = 3;

    public static int turn(int orientation, int direction){
        return (orientation + direction) % 4;
    }

    public static int rotateLeft(int orientation){
        return (orientation - 1) % 4;
    }

    public static int rotateRight(int orientation){
        return (orientation + 1) % 4;
    }
}
