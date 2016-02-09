package algorithms;

import controllers.Controller;
import models.Arena;
import models.Robot;

/**
 * Created by Jiaxiang on 5/2/16.
 */
public class PathFinder {
    private static PathFinder instance = new PathFinder();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();
    private PathFinder(){}

    private static final double COST_TO_MOVE_ONE_STEP = 1;
    private static final double COST_TO_MAKE_A_TURN = 1;


    private class PathNode{
        public int[] index;
        public Arena.mazeState state;
        public double pathCost;
        public int heuristics;
        public int[] previousNode;
        public boolean visited = false;

        public PathNode(){
            pathCost = Integer.MAX_VALUE;
        }

        public double getTotalCost(){
            return pathCost+heuristics;
        }

        //returns the indices of surrounding nodes
        public int[][] getSurrondingNodeIndices() throws ArrayIndexOutOfBoundsException{
            int[][] surroundingNodes = new int[9][2];

            int n = 0;
            for(int i=-1; i<=1; i++)
                for(int j=-1; j<=1; j++){
                    surroundingNodes[n] = new int[]{index[0]+i, index[1]+j};
                    n++;
                }

            return surroundingNodes;
        }

        //returns the indices of reachable nodes by a robot
        public int[][] getReachableNodeIndices() throws ArrayIndexOutOfBoundsException{
            int[][] reachableNodes = new int[9][2];

            reachableNodes[0] = new int[]{index[0]-1, index[1]};
            reachableNodes[1] = new int[]{index[0], index[1]+1};
            reachableNodes[2] = new int[]{index[0]+1, index[1]};
            reachableNodes[3] = new int[]{index[0], index[1]-1};

            return reachableNodes;
        }
    }

    private class VirtualMap{
        private PathNode[][] virtualMap;


        private VirtualMap(){
            this.virtualMap = new PathNode[Arena.ROW][Arena.COL];

            //creating objects inside each cell
            for(int i=0; i<Arena.ROW; i++) {       //for each cell in the map
                for (int j = 0; j < Arena.COL; j++) {
                    virtualMap[i][j] = new PathNode();
                }
            }
        }

        private boolean isNodeOnSide(int row, int col, Arena.mazeState[][] maze){
            return (row==0 || col ==0 || row==(maze.length - 1) || col==(maze[0].length - 1));
        }



        //initialize the virtual map, to make surrounding cells of virtual obstacles
        //in the case of treating unknown as obstacles, all unknowns nodes will remain unknown
        //in the case of treating unknown as free space, all unknowns will be converted to virtual obstacles
        public VirtualMap(Arena.mazeState[][] maze, boolean treatUnknownAsObstacle){

            this();

            //creating virtual obstacles
            for(int i=0; i<maze.length; i++) {       //for each cell in the map
                for (int j = 0; j < maze[0].length; j++) {

                    this.virtualMap[i][j].state = maze[i][j];

                    //if this node is an obstacle, or unknown in the case of treating unknown as obstacle
                    if (maze[i][j] == Arena.mazeState.obstacle || (treatUnknownAsObstacle && maze[i][j]== Arena.mazeState.unknow)) {

                        //make its surrounding cells an "virtual" obstacle
                        for (int[] surroundingNodeIndex : virtualMap[i][j].getSurrondingNodeIndices()){

                            //if this node is a free space or unknown in the case of treating unknown as free space
                            if(this.virtualMap[surroundingNodeIndex[0]][surroundingNodeIndex[1]].state == Arena.mazeState.freeSpace
                                    ||(!treatUnknownAsObstacle &&
                                    this.virtualMap[surroundingNodeIndex[0]][surroundingNodeIndex[1]].state == Arena.mazeState.unknow))

                                try {
                                    this.virtualMap[surroundingNodeIndex[0]][surroundingNodeIndex[1]].state
                                            = Arena.mazeState.virtualObstacle;
                                }catch (ArrayIndexOutOfBoundsException e){}
                                //handled exception if the cell is at the side or corner
                        }
                    }

                    //if this node is on the side and is a free space or unknown in the case of treating unknown as free space
                    if(isNodeOnSide(i, j, maze) && (this.virtualMap[i][j].state == Arena.mazeState.freeSpace)
                        || (!treatUnknownAsObstacle && this.virtualMap[i][j].state == Arena.mazeState.unknow))
                        this.virtualMap[i][j].state = Arena.mazeState.virtualObstacle;
                }
            }
        }

        public PathNode[][] getVirtualMap() {
            return virtualMap;
        }
    }


    public static PathFinder getInstance(){
        if(instance==null)
            instance = new PathFinder();
        return instance;
    }




    private int[][] aStarStraight(int[] start, int[] goal, Arena.mazeState[][] maze, boolean treatUnknownAsObstacle){
        VirtualMap virtualMap = new VirtualMap(maze, treatUnknownAsObstacle);

        //initializing heuristics
        for(int i=0; i<Arena.ROW; i++) {
            for (int j = 0; j < Arena.COL; j++) {
                virtualMap.getVirtualMap()[i][j].pathCost = calculateHeuristic(i, j, goal);
            }
        }

        //virtualMap.getVirtualMap()[start[0]][start[1]].pathCost = 0;
        virtualMap.getVirtualMap()[start[0]][start[1]].visited = true;

        //expand the first node with pathCost = 1, from the start node
        expand(virtualMap.getVirtualMap()[start[0]][start[1]], 0);

//        error;


        return null;
    }

    int expand(PathNode node, int pathCost){
        node.pathCost = pathCost;
        node.visited = true;
        for(int[] reachableNodeIndex : node.getReachableNodeIndices()){

        }
        return 0;
    }

    private int calculateHeuristic(int row, int col, int[] goal){
        return Math.abs(goal[0] - row) + Math.abs(goal[1] - col);
    }


}
