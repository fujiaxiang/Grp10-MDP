package utilities;


import models.Robot;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public class Messages {

    //general constants
    public static final String ANDROID_CODE = "a";

    public static final String ANDROID_END_CODE = "|";

    public static final String ARDUINO_CODE = "h";

    public static final String ARDUINO_END_CODE = "|";

    public static final String RESEND_CODE = "resend";



    //messages related to Android
    public static String startExploration(){
        return "explore";
    }

    public static String startShortestPath(){
        return "shortestPath";
    }

    public static String obstacleInfo(){
        String message = Robot.getInstance().getPerceivedArena().toObstacleInfo();
        return message;
    }

    public static String mapDescriptor(){
        String message = Robot.getInstance().getPerceivedArena().toMapDescriptor();
        return message;
    }


    //messages ralated to Arduino
    public static String moveRobotForward(int n){
        return "W" + n;
    }

    public static String robotMovedForward(int n){
        return "W" + n + "done";
    }

    public static String turnRobot(int direction){
        if(direction==Orientation.LEFT)
            return "A90";
        else if(direction==Orientation.RIGHT)
            return "D90";
        else if(direction==Orientation.BACK)
            return "D180";
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
        return "C";
    }

    public static String callibrated(){
        return "Cdone";
    }


    public static String detectObstacles(){
        return "S";
    }
}
