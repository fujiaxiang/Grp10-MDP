package models;

import utilities.GlobalUtilities;
import utilities.Orientation;

import java.util.ArrayList;

/**
 * Created by Fujitsu on 20/1/2016.
 */
public class Robot {
    public static final int SIZE = 3;
    public static final int HALF_SIZE = SIZE/2;
    private int[] location;
    private int orientation;

    //Specify the basic information of sensors here
    private final int NUMBER_OF_SENSORS = 5;
    //Format for sensor string: LocX;LocY;orientation;maxRange;minRange
    private final String[] SENSOR_STRINGS = {"-1;-1;0;5;1", "-1;0;0;5;1", "-1;1;0;5;1", "-1;-1;3;5;1", "-1;11;1;5;1"};


    private Arena perceivedArena;
    private boolean[][] explored;
    private static Robot instance = new Robot();

//    ***********
    private Sensor[] sensors;
//    ********
    //temporary attribute
    //robot size  assume robot size = 3

    private Robot(){}

    public static Robot getInstance(){
        if(instance==null)
            instance = new Robot();
        return instance;
    }

    public void initialize(int[] location, int orientation){
        int[] start = {Arena.ROW-2,1};
        int[] goal = {1,Arena.COL-2};
        perceivedArena = new Arena(start, goal);
        perceivedArena.resetToCertainState(Arena.mazeState.unknow);
        Arena.mazeState[][] maze = getPerceivedArena().getMaze();
        
        this.location = location;
        this.orientation = orientation;
     
        this.perceivedArena = new Arena(start, goal, maze);
        this.explored = new boolean[maze.length][maze[0].length];

        this.sensors = new Sensor[1];
//        for(int i=0; i<NUMBER_OF_SENSORS; i++){               //converting the sensor strings to sensor objects
//            String sensorString = SENSOR_STRINGS[i];
//            String[] sensorStringSplits = sensorString.split(";");
//            int[] relativeLocation = {Integer.parseInt(sensorStringSplits[0]), Integer.parseInt(sensorStringSplits[1])};
//            sensors[i] = new Sensor(relativeLocation, Integer.parseInt(sensorStringSplits[2]),
//                    Integer.parseInt(sensorStringSplits[3]), Integer.parseInt(sensorStringSplits[4]));
//        }
        int[] relativeLocation = {-1, 0};
        sensors[0] = new Sensor(relativeLocation, Orientation.NORTH, 5, 1);

        for(int i=0;i<explored.length;i++)
            for(int j=0;j<explored[i].length;j++)
                explored[i][j] = false;

    }

    public Sensor[] getSensors(){
        return sensors;
    }

    public int[] getLocation(){
        return location;
    }

    public void printStatus(){
        System.out.print("Robot location: Row: " + getLocation()[0] + ", Col: " + getLocation()[1]+ ". ");
        System.out.println("Orientation: " + getOrientation());
    }

    public int getOrientation(){return orientation;}

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
    public void setLocation(int[] location) {
        this.location = location;
    }


    public int moveForward(int steps){
        try{
            setLocation(GlobalUtilities.locationParser(getLocation(), orientation, steps));
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
            System.out.println("Error inside Robot class, method moveForward");
        }
        return 0;
    }

    public int turn(int direction){
        setOrientation(Orientation.turn(getOrientation(), direction));
        return 0;
    }

    public Arena getPerceivedArena() {
        return perceivedArena;
    }
}

