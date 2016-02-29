package utilities;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public class Messages {

    public static final String androidCode = "a";

    public static final String arduinoCode = "h";

    public static final String androidTest = "aMessage is sent from PC, ahahaha, you have been hacked!";

    public static String moveRobotForward(int n){
        return "move " + n + " steps";
    }

    public static String robotMovedForward(int n){
        return "moved " + n + "\n";
    }

    public static String turnRobot(int direction){
        return "turned into direction " + direction;
    }

    public static String robotTurned(int direction){
        return "robot turned direction" + direction + "\n";
    }

    public static String callibrate(){
        return "callibrate";
    }

    public static String callibrated(){
        return "callibrated" + "\n";
    }

    public static String detectObstacles(){
        return "detectObstacles";
    }
}
