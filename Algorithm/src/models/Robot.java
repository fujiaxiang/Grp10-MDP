package models;

import java.util.ArrayList;

/**
 * Created by Fujitsu on 20/1/2016.
 */
public class Robot {
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

    public void explore(){
        try {
            //+half size < - half size
            while(location[1]+2<maze[0].length){
                location[1]++;
                Thread.sleep((long)speed);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

