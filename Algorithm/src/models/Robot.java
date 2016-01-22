package models;

import java.util.ArrayList;

/**
 * Created by Fujitsu on 20/1/2016.
 */
public class Robot {
    private int[] location;
    private double speed;
    private int direction;//2 4 6 8
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

    public Robot(int[] location,double speed,int direction,boolean[][] maze){
        this.location = location;
        this.speed = speed;
        this.direction = direction;
        this.maze = maze;
        this.explored = new boolean[maze.length][maze[0].length];
        for(int i=0;i<explored.length;i++)
            for(int j=0;j<explored[i].length;j++)
                explored[i][j] = false;

    }

    public int[] getLocation(){
        return location;
    }
    public int getDirection(){return direction;}

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

