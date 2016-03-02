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
        return (orientation + 3) % 4;
    }

    public static int rotateRight(int orientation){
        return (orientation + 1) % 4;
    }

    /**
     * This function returns the coordinates of a point after rotated along the originate (0, 0).
     * @param coordinates
     * @param orientation
     * @return
     */
    public static int[] rotateCoordinates(int[] coordinates, int orientation){
        int x, y;
        try{
            x = ((orientation/2)*(-2)+1)*coordinates[orientation%2];
            y = ((((orientation+1)/2)%2)*(-2)+1)*coordinates[(orientation+1)%2];
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Handled exception in class Orientation, rotateCoordinates method");
            return null;
        }
        return new int[] {x, y};
    }

    //returns the relative orientation of destination relative to origin
    public static int relativeOrientation(int[] destinationIndex, int[] originIndex){
        int xDifference = destinationIndex[0] - originIndex[0];
        int yDifference = destinationIndex[1] - originIndex[1];
        //return yDifference==0 ? (1+xDifference) : (2-yDifference);
        return yDifference==0 ? (xDifference>0? 2 : 0) : (yDifference>0?1:3);
    }

    public static int oppositeOrientation(int orientation){
        return (orientation + 2) % 4;
    }

    public static int whichDirectionToTurn(int targetOrientation, int initialOrientation){
        return (targetOrientation - initialOrientation + 4) % 4;
    }
}
