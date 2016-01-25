package models;
import utilities.GlobalUtilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Sensor {
    private int[] relativeLocation;
    private Orientation orientation;
    private int maxRange;
    private int minRange;

    public int[] getLocation(){
        int[] absoluteLocation= new int[2];
        absoluteLocation[0] = Robot.getInstance().getLocation()[0] + relativeLocation[0];
        absoluteLocation[1] = Robot.getInstance().getLocation()[1] + relativeLocation[1];
        return absoluteLocation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }
}
