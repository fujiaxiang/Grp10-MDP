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
    private boolean[][] maze;
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

    public Robot(int[] location,double speed,int orientation,boolean[][] maze){
        this.location = location;
        this.speed = speed;
        this.orientation = orientation;
        this.maze = maze;
        this.explored = new boolean[maze.length][maze[0].length];
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

    public int getOrientation(){return orientation;}

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

//    public void walk(){
//        switch(orientation){
//            case 2:location[0]--;break;
//            case 4:location[1]--;break;
//            case 6:location[1]++;break;
//            case 8:location[0]++;break;
//        }
//    }

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


}

