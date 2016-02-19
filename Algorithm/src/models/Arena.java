package models;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Arena {

    public static final int COL = 15;
    public static final int ROW = 20;
    public static final int START_GOAL_SIZE = 3;

    public enum mazeState{
        freeSpace, obstacle, unknown, virtualObstacle
    }

//    private static Arena instance = new Arena();

    private int[] start;//indicate start center
    private int[] goal;//indicate goal center
    private mazeState[][] maze; //indicate whether the grid is a obstacle

    public Arena(){}

    public Arena(int[] start,int[] goal){
        this.start = start;
        this.goal = goal;
        this.maze = new mazeState[ROW][COL];
    }
    public Arena(int[] start,int[] goal, mazeState[][] maze){
        this.start = start;
        this.goal = goal;
        this.maze = maze;
    }

    public void resetToCertainState(mazeState state){
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[0].length; j++)
                maze[i][j] = state;
        }
    }

//    public void initialize(){
//        this.start = start;
//        this.goal = goal;
//        this.maze = maze;
//    }

//    private static Arena getInstance(){
//        if(instance==null)
//            instance = new Arena();
//        return instance;
//    }

    public int[] getStart(){
        return start;
    }
    public int[] getGoal(){
        return goal;
    }

    public void setStart(int[] start){
        this.start = start;
    }

    public void setGoal(int[] goal){
        this.goal = goal;
    }

    public void setObstacle(int row,int col,mazeState state){
        maze[row][col] = state;
    }

    public mazeState[][] getMaze(){
        return maze;
    }

    public void print(){
        for(int i = 0; i < maze.length; i++) {
            for (int j = 0; j < maze[i].length; j++) {

                switch (maze[i][j]){
                    case freeSpace:
                        System.out.print("f\t");
                        break;
                    case obstacle:
                        System.out.print("o\t");
                        break;
                    case unknown:
                        System.out.print("u\t");
                        break;
                    case virtualObstacle:
                        System.out.print("v\t");
                        break;
                    default:
                        System.out.print("0\t");
                        break;
                }
            }
            System.out.println();
        }
    }

    private final int BREAKS_TO_SEPARATE_DESCRIPTOR_PARTS = 2;

    public String toMapDescriptor(){
        String descriptorPart1 = "11\n";
        String descriptorPart2 = "";
        for(int i= ROW-1; i>=0; i--) {
            for (int j = 0; j < COL; j++) {
                if (maze[i][j] == mazeState.unknown)
                    descriptorPart1 += "0";
                else {
                    descriptorPart1 += "1";
                    if(maze[i][j] == mazeState.freeSpace)
                        descriptorPart2 += "0";
                    else
                        descriptorPart2 += "1";
                }
            }
            descriptorPart1 += "\n";
            descriptorPart2 += "\n";
        }
        descriptorPart1 += "\n11";
        for(int i = 0; i< (8- (descriptorPart2.length() % 8)); i++)
            descriptorPart2 += "1";

        String separate = "";
        for(int i = 0; i< BREAKS_TO_SEPARATE_DESCRIPTOR_PARTS; i++)
            separate += "\n";
        return descriptorPart1 + separate + ";\n"+ descriptorPart2;
    }

    public void loadMapDescriptor(String descriptor){
        String[] descriptors = descriptor.split(";");
        int counter1 = 3;
        int counter2 = 1;
        for(int i= ROW-1; i>=0; i--) {
            for (int j = 0; j < COL; j++) {
                if(descriptors[0].charAt(counter1) == '1'){
                    if(descriptors[1].charAt(counter2) == '1')
                        maze[i][j] = mazeState.obstacle;
                    else
                        maze[i][j] = mazeState.freeSpace;
                }else
                    maze[i][j] = mazeState.unknown;

                counter1++;
                counter2++;
            }
            counter1++;
            counter2++;
        }
    }

    //returns the percentage of map that has been covered by detected areas
    public double coverage(){
        int cover = 0;
        for(int i=0; i<ROW; i++)
            for(int j=0; j<COL; j++){
                if(maze[i][j] != mazeState.unknown)
                    cover++;
            }
        return (double) cover / (ROW * COL);
    }
}
