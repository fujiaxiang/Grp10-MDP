package algorithms;

import controllers.Controller;
import models.Path;
import models.Robot;
import org.omg.PortableInterceptor.ORBInitInfo;
import services.*;
import utilities.GlobalUtilities;
import utilities.Orientation;

/**
 * Created by Jiaxiang on 13/2/16.
 */
public class PathRunner {
    private static PathRunner instance = new PathRunner();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();

    private RPiServiceInterface rpiService;
    private SensorServiceInterface sensorService;
    private AndroidServiceInterface androidService;

    private PathRunner(){}

    public static PathRunner getInstance(){
        if(instance==null)
            instance = new PathRunner();
        return instance;
    }

    //initialises the two types of services based on simulation or real run
    private void initialiseServices(boolean isRealRun){
        if(isRealRun){
            rpiService = RealRPiService.getInstance();
            sensorService = RealSensorService.getInstance();
            androidService = RealAndroidService.getInstance();
        }
        else{
            rpiService = SimuRPiService.getInstance();
            sensorService = SimuSensorService.getInstance();
            androidService = SimuAndroidService.getInstance();
        }
    }

    public void runShortestPath(Path path, boolean isRealRun){
        initialiseServices(isRealRun);
        androidService.waitToRunShortestPath();
        //controller.startTimer();
        runPath(path, isRealRun);
    }

    public void runPath(Path path, boolean isRealRun){
        initialiseServices(isRealRun);

        //if starting orientation is different from starting orientation, turn to that orientation
        if(path==null){
            System.out.println("The path is null...");
            return;
        }
        while(robot.getOrientation() != path.getPathNodes().get(0).orientation)
            rpiService.turn(Orientation.LEFT);

        int stepsToMove = 0;
        //start from the second node
        int i=1;

        while(i<path.getPathNodes().size()){
            stepsToMove = 0;
            while(i< path.getPathNodes().size() &&
                    robot.getOrientation() == path.getPathNodes().get(i).orientation){
                stepsToMove++;
                i++;
            }

            rpiService.moveForward(stepsToMove);

            //if robot has reached the destination, break out
            if(i==path.getPathNodes().size())
                break;

            //make a turn to the relative orientation of this node as compared to last node
            int turnDirection = Orientation.whichDirectionToTurn(path.getPathNodes().get(i).orientation, robot.getOrientation());
            rpiService.turn(turnDirection);
        }
        System.out.println("Shortest path completed");
    }

    public void runDiagonalPath(Path path, boolean isRealRun){
        initialiseServices(isRealRun);
        androidService.waitToRunShortestPath();
        int i = 0;

        if(!(robot.getLocation()[0]==path.getPathNodes().get(i).index[0] && robot.getLocation()[1]==path.getPathNodes().get(i).index[1])){
            System.out.println("In PathRunner class, runDiagonaPath method, robot position is not at first node");
            return;
        }

        robot.updateFullOrientation();

        while(i < path.getPathNodes().size() -1){
            double nextDegree = Orientation.relativeDegree(path.getPathNodes().get(i).index, path.getPathNodes().get(i+1).index);
            rpiService.turnDegree(Orientation.degreeToTurn(robot.getFullOrientation(), nextDegree));
            rpiService.moveDistance(GlobalUtilities.relativeDistance(path.getPathNodes().get(i).index, path.getPathNodes().get(i+1).index));
            i++;
        }

    }


}
