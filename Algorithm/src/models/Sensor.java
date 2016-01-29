package models;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Sensor {
    private int[] relativeLocation;
    private int relativeOrientation;
    private int maxRange;
    private int minRange;

    private Robot robot = Robot.getInstance();

    public Sensor(int[] relativeLocation, int relativeOrientation, int maxRange, int minRange) {
        this.relativeLocation = relativeLocation;
        this.relativeOrientation = relativeOrientation;
        this.maxRange = maxRange;
        this.minRange = minRange;
        this.robot = Robot.getInstance();
    }

    public int[] getLocation(){
        int[] absoluteLocation= new int[2];
        absoluteLocation[0] = robot.getLocation()[0] + relativeLocation[0];
        absoluteLocation[1] = robot.getLocation()[1] + relativeLocation[1];
        return absoluteLocation;
    }

    public int getrelativeOrientation() {
        return relativeOrientation;
    }

    public int getAbsoluteOrientation() {
        return Orientation.turn(robot.getOrientation(), relativeOrientation);
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }
}
