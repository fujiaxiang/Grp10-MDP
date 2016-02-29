package utilities;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public class Messages {

    //general constants
    public static final String androidCode = "a";

    public static final String arduinoCode = "h";



    //messages related to Android
    public static String startExploration(){
        return "explore";
    }

    public static String startShortestPath(){
        return "shortestPath";
    }


    //messages ralated to Arduino
    public static String moveRobotForward(int n){
        return "W" + n + "|";
    }

    public static String robotMovedForward(int n){
        return "W" + n + "done";
    }

    public static String turnRobot(int direction){
        if(direction==Orientation.LEFT)
            return "A90|";
        else if(direction==Orientation.RIGHT)
            return "D90|";
        else if(direction==Orientation.BACK)
            return "D180|";
        return null;
    }

    public static String robotTurned(int direction){
        if(direction==Orientation.LEFT)
            return "A90done";
        else if(direction==Orientation.RIGHT)
            return "D90done";
        else if(direction==Orientation.BACK)
            return "D180done";
        return null;
    }

    public static String callibrate(){
        return "C|";
    }

    public static String callibrated(){
        return "Cdone";
    }


    public static String detectObstacles(){
        return "S|";
    }
}
