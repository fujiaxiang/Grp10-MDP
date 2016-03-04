package algorithms;

import controllers.Controller;
import models.Arena;
import models.Path;
import models.Robot;
import services.*;
import utilities.Orientation;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Jiaxiang on 2/3/16.
 */
public class SecondRoundExploration {

    private static SecondRoundExploration instance;

    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();

    private RPiServiceInterface rpiService;
    private SensorServiceInterface sensorService;
    private AndroidServiceInterface androidService;

    public static final double MINIMUM_COVERAGE = 1;


    public static SecondRoundExploration getInstance(){
        if(instance==null)
            instance= new SecondRoundExploration();
        return instance;
    }

    private SecondRoundExploration(){}

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


    public boolean needSecondRound(){
        if(robot.getPerceivedArena().coverage()> MINIMUM_COVERAGE)
            return false;

        return true;
    }

    public void runToUnknownPlace(boolean isRealRun){
        if(!needSecondRound())
            return;

        initialiseServices(isRealRun);

        Queue<int[]> unknownBlocks = unknownBlocks();
        System.out.println("the size of unknown blocks is " + unknownBlocks.size());

        int[] block;
        while(unknownBlocks.size()>0){
            block = unknownBlocks.poll();

            //System.out.println("generating path for block "+ block[0] + " " + block[1]);

            Path path = pathToDetectUnknown(block);
            if(path!=null) {
                PathRunner.getInstance().runPath(path, Controller.isRealRun);
                rpiService.turn(Orientation.whichDirectionToTurn(Orientation.relativeOrientation(block, robot.getLocation()), robot.getOrientation()));
                MazeExplorer.getInstance().observe();
                unknownBlocks = unknownBlocks();
            }

            if(!needSecondRound())
                break;
        }
        Path path = PathFinder.getInstance().aStarStraight(robot.getPerceivedArena().getMaze(), robot.getLocation(), robot.getPerceivedArena().getStart(), true, robot.getOrientation());
        PathRunner.getInstance().runPath(path, Controller.isRealRun);

    }

    public Queue<int[]> unknownBlocks(){
        Arena.mazeState[][] maze = robot.getPerceivedArena().getMaze();
        Queue<int[]> unknowns = new LinkedBlockingDeque<>();
        for(int i=maze.length-1; i>=0; i--)
        //for(int i=0; i<maze.length; i++)
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j] == Arena.mazeState.unknown)
                    unknowns.add(new int[]{i,j});
            }
        return unknowns;
    }


    public Path pathToDetectUnknown(int[] node){

        int[][] indices = new int[4][2];
        //int[] index = new int[2];

        indices[0] = new int[]{node[0] + 2, node[1]};
        indices[1] = new int[]{node[0], node[1] - 2};
        indices[2] = new int[]{node[0]- 2, node[1]};
        indices[3] = new int[]{node[0], node[1] + 2};

        Path path;

        for(int n=0; n<4; n++){
            try{
                //System.out.println("trying path to this node: " + indices[n][0] + " " + indices[n][1]);
                path = PathFinder.getInstance().aStarStraight(robot.getPerceivedArena().getMaze(), robot.getLocation(), indices[n], true, robot.getOrientation());
                if(path!=null)
                    return path;
            }catch (ArrayIndexOutOfBoundsException aiobe){
                aiobe.printStackTrace();
            }
        }
        return null;
    }

//    public ArrayList<int[]> reachableNodeIndex(int[] node){
//        ArrayList<int[]> reachableIndices = new ArrayList<>();
//        int[][] indices = new int[4][2];
//        int[] index = new int[2];
//
//        indices[0] = new int[]{node[0] + 2, node[1]};
//        indices[1] = new int[]{node[0], node[1] - 2};
//        indices[2] = new int[]{node[0]- 2, node[1]};
//        indices[3] = new int[]{node[0], node[1] + 2};
//
//        Path path;
//
//        for(int n=0; n<4; n++){
//            try{
//                path = PathFinder.getInstance().aStarStraight(robot.getPerceivedArena().getMaze(), robot.getLocation(), index, true, robot.getOrientation());
//                if(path!=null)
//                    reachableIndices.add(indices[n]);
//            }catch (ArrayIndexOutOfBoundsException aiobe){
//                aiobe.printStackTrace();
//            }
//        }
//        return reachableIndices;
//    }

}
