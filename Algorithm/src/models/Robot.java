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
    private double speed;
    private int orientation;

 

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

    public void initialize(int[] location, double speed, int orientation, Arena.mazeState[][] maze){
        this.location = location;
        this.speed = speed;
        this.orientation = orientation;
        int[] start = {Arena.ROW-2,1};
        int[] goal = {1,Arena.COL-2};
        this.perceivedArena = new Arena(start, goal, maze);
        this.explored = new boolean[maze.length][maze[0].length];
        this.sensors = new Sensor[1];
        int[] relativeLocation = {-1,0};
        this.sensors[0] = new Sensor(relativeLocation, Orientation.FRONT, 5, 1);
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
        //Temporary Turns  , Change Sensor Relative Location when turned

        /*
        double different = (this.orientation - orientation)/2.0;
        double rad = different*Math.PI;
        double[][] mat = {{Math.cos(rad),-Math.sin(rad)},{Math.sin(rad),Math.cos(rad)}};
        for(Sensor s:getSensors()){
            if(s==null)break;
            int[] relative = s.getRelativeLocation();
            int r = relative[0]*-1;//either make this negative or angle negative
            int c = relative[1];
            for(int i=0;i<2;i++)
                relative[i] = (int)Math.round(c*mat[1-i][0]+r*mat[1-i][1]);
            relative[0]*=-1;
        }
        */

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

