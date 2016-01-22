package models;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Sensor {
    private int[] relativeLocation;
    private int orientation;
    private int maxRange;
    private int minRange;

    public int[] getLocation(){
        int[] absoluteLocation= new int[2];
        absoluteLocation[0] = Robot.getInstance().getLocation()[0] + relativeLocation[0];
        absoluteLocation[1] = Robot.getInstance().getLocation()[1] + relativeLocation[1];
        return absoluteLocation;
    }

    public int getOrientation() {
        return orientation;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }
}
