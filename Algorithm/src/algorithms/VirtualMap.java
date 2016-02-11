package algorithms;

import models.Arena;

/**
 * Created by Jiaxiang on 11/2/16.
 */
public class VirtualMap{
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