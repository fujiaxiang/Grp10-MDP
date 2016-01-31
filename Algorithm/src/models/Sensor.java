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

    public int[] getAbsoluteLocation(){
        int[] absoluteLocation= new int[2];
        int[] rotatedRelativeLocation = Orientation.rotateCoordinates(getRelativeLocation(), robot.getOrientation());
        absoluteLocation[0] = robot.getLocation()[0] + rotatedRelativeLocation[0];
        absoluteLocation[1] = robot.getLocation()[1] + rotatedRelativeLocation[1];
        return absoluteLocation;
    }

    public void setRelativeLocation(int[] relativeLocation){
        this.relativeLocation = relativeLocation;
    }

    public int[] getRelativeLocation(){return relativeLocation;}

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
