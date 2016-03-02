package algorithms;

import controllers.Controller;
import models.Arena;
import models.Path;
import models.Robot;
import services.AndroidServiceInterface;
import services.RPiServiceInterface;
import services.SensorServiceInterface;

import java.util.ArrayList;

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

    public static final double MINIMUM_COVERAGE = 0.95;


    public static SecondRoundExploration getInstance(){
        if(instance==null)
            instance= new SecondRoundExploration();
        return instance;
    }

    private SecondRoundExploration(){}


    public boolean needSecondRound(){
        if(robot.getPerceivedArena().coverage()> MINIMUM_COVERAGE)
            return false;

        return true;
    }

    public void runToUnknownPlace(){
        if(!needSecondRound())
            return;
        ArrayList<int[]> unknownBlocks = unknownBlocks();
        System.out.println("the size of unknown blocks is " + unknownBlocks.size());
        for(int[] block : unknownBlocks){
            Path path = pathToDetectUnknown(block);
            if(path!=null)
                PathRunner.getInstance().runPath(path, Controller.isRealRun);

            else
                System.out.println("hahahahaha NULL");
            if(!needSecondRound())
                break;
        }
        Path path = PathFinder.getInstance().aStarStraight(robot.getPerceivedArena().getMaze(), robot.getLocation(), Controller.getInstance().getStartGoalLoc(true), true, robot.getOrientation());
        PathRunner.getInstance().runPath(path, Controller.isRealRun);
    }

    public ArrayList<int[]> unknownBlocks(){
        Arena.mazeState[][] maze = robot.getPerceivedArena().getMaze();
        int size = (int) Math.ceil(Arena.COL * Arena.ROW * robot.getPerceivedArena().coverage());
        ArrayList<int[]> unknowns = new ArrayList<int[]>();
        for(int i=maze.length-1; i>=0; i--)
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j] == Arena.mazeState.unknown)
                    unknowns.add(new int[]{i,j});
            }
        return unknowns;
    }


    public Path pathToDetectUnknown(int[] node){

        int[][] indices = new int[4][2];
        int[] index = new int[2];

        indices[0] = new int[]{node[0] + 2, node[1]};
        indices[1] = new int[]{node[0], node[1] - 2};
        indices[2] = new int[]{node[0]- 2, node[1]};
        indices[3] = new int[]{node[0], node[1] + 2};

        Path path;

        for(int n=0; n<4; n++){
            try{
                path = PathFinder.getInstance().aStarStraight(robot.getPerceivedArena().getMaze(), robot.getLocation(), index, true, robot.getOrientation());
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
