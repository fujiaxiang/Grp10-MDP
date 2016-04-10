package utilities;


import com.sun.org.apache.xpath.internal.operations.Or;
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
        return "F" + n * 100;
    }

    public static String robotMovedForward(int n){
        return "F" + (n * 100) + "done";
    }

//    public static String moveRobotForward(int n){
//        return "W" + n;
//    }
//
//    public static String robotMovedForward(int n){
//        return "W" + n + "done";
//    }


    public static String moveDistance(int distance){
        return "F" + distance;
    }

    public static String robotMovedDistance(int distance){
        return "F" + distance + "done";
    }

    public static String turnDegree(int centiDegree, int direction){
        final int smallDegreeeLimit = 20;
        if(direction == Orientation.LEFT) {
//            if (centiDegree < smallDegreeeLimit)
//                return "a" + centiDegree;
            return "A" + centiDegree;
        }
        else if (direction == Orientation.RIGHT) {
//            if (centiDegree < smallDegreeeLimit)
//                return "d" + centiDegree;
            return "D" + centiDegree;
        }
        else{
            System.out.println("In class Messages, method turnDegree, entering default case");
            return null;
        }
    }

    public static String robotTurnedDegree(int centiDegree, int direction){
        final int smallDegreeeLimit = 20;
        if(direction == Orientation.LEFT) {
//            if (centiDegree < smallDegreeeLimit)
//                return "a" + centiDegree + "done";
            return "A" + centiDegree + "done";
        }
        else if (direction == Orientation.RIGHT) {
//            if (centiDegree < smallDegreeeLimit)
//                return "d" + centiDegree + "done";
            return "D" + centiDegree + "done";
        }
        else{
            System.out.println("In class Messages, method robotTurnedDegree, entering default case");
            return null;
        }
    }

    public static String turnRightDegree(int degree){
        return "D" + degree;
    }

    public static String turnRobot(int direction){
        if(direction==Orientation.LEFT)
            return "A9000";
        else if(direction==Orientation.RIGHT)
            return "D9000";
        else if(direction==Orientation.BACK)
            return "D18000";
        return null;
    }

    public static String robotTurned(int direction){
        if(direction==Orientation.LEFT)
            return "A9000done";
        else if(direction==Orientation.RIGHT)
            return "D9000done";
        else if(direction==Orientation.BACK)
            return "D18000done";
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
