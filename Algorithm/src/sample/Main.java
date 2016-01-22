package sample;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private Controller controller;

    private final int SCENE_WIDTH = 450;
    private final int SCENE_HEIGHT = 550;
    private final String SCENE_TITLE = "MAZE";
    private final int CANVAS_WIDTH = 300;
    private final int CANVAS_HEIGHT = 400;
    private final int CANVAS_X	= 10;
    private final int CANVAS_Y  = 10;
    private final int MARGIN = 20;
    private final int MARGIN_SMALL = 10;
    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 30;
    private final int BUTTON_SIDE_X = SCENE_WIDTH-BUTTON_WIDTH-MARGIN;
    private final int BUTTON_SIDE_Y = CANVAS_Y;
    private final int SLEEP_DURATION    = 200;


    private final int DRAW_START = 0;
    private final int DRAW_GOAL = 1;
    private final int DRAW_OBSTACLE = 2;
    private final int DRAW_PATH = 3;

    private final Color COLOR_GOAL = Color.GREEN;
    private final Color COLOR_START = Color.RED;
    private final Color COLOR_PATH = Color.LIGHTGRAY;
    private final Color COLOR_OBSTACLE = Color.GRAY;
    private final Color COLOR_ROBOT = Color.YELLOW;
    private final Color COLOR_ROBOT_FACE = Color.RED;
    private final Color COLOR_GRID = Color.BLACK;
    private final Color COLOR_EXPLORED = Color.LIGHTPINK;

    private final int PATH = 0;
    private final int OBSTACLE = 1;
    private final int START = 2;
    private final int GOAL = 3;

    private final int ROBOT = 4;
    private final int EXPLORED = 5;

    private final Color[] COLOR_REF = {
            COLOR_PATH,COLOR_OBSTACLE,COLOR_START,COLOR_GOAL,COLOR_ROBOT,COLOR_EXPLORED
            //COLOR_EXPLORED
    };
    private final int COL = 15;
    private final int ROW = 20;
    private final int[] INIT_START = {ROW-2,1};//ROW,COL
    private final int[] INIT_GOAL = {1,COL-2};//ROW,COL
    private final int START_GOAL_SIZE = 3;//ODD
    private final int CELL_SIZE = CANVAS_WIDTH/COL;//assume cell is square
    private final int ROBOT_SIZE = 3;

    private Canvas canvas_robot;

    private GraphicsContext gc; //Graphic Context of Canvas
    private int draw_mode = OBSTACLE;
    private int maze_info[][];

    private int robot_loc[][];//used to store robot location

    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new Controller(ROW,COL,INIT_START, INIT_GOAL,START_GOAL_SIZE,SLEEP_DURATION);
        initializeMazeData();
        primaryStage.setTitle(SCENE_TITLE);
        primaryStage.setScene(new Scene(createGroup(), SCENE_WIDTH,SCENE_HEIGHT,Color.LIGHTGRAY));
        primaryStage.show();
    }

    //This function initialize Maze Data.
    //All slot is defined as Path
    //and Setup Start Location and Goal Location
    private void initializeMazeData(){
        maze_info = new int[ROW][COL];
        for(int i=0;i<ROW;i++)
            for(int j=0;j<COL;j++)
                maze_info[i][j] = PATH;
        for(int i:controller.getStartGoalLoc(true))
            maze_info[i/COL][i%COL] = START;
        for(int i:controller.getStartGoalLoc(false))
            maze_info[i / COL][i % COL] = GOAL;
    }

    private Group createGroup(){
        Group g = new Group();
        g.getChildren().add(createCanvas());
        g.getChildren().add(createCanvasRobot());
        for(Button b:createMazeSetupButtons())
            g.getChildren().add(b);
        for(Button b:createBottomButtons())
            g.getChildren().add(b);

        return g;
    }

    private int[] getStartIndex(){
        int[] loc = {0,0};
        for(int i=0;i<maze_info.length;i++)
            for(int j=0;j<maze_info[i].length;j++){
                if(maze_info[i][j]==START){
                    loc[0] = i;
                    loc[1] = j;
                    return loc;
                }
            }
        return loc;
    }

    private Canvas createCanvas(){
        Canvas c = new Canvas(CANVAS_WIDTH,CANVAS_HEIGHT);
        c.setLayoutX(CANVAS_X);
        c.setLayoutY(CANVAS_Y);
        EventHandler handler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int row = (int) (event.getY() / CELL_SIZE);
                int col = (int) (event.getX() / CELL_SIZE);
                if (draw_mode == PATH || draw_mode == OBSTACLE){
                    if (maze_info[row][col] == OBSTACLE || maze_info[row][col] == PATH) {
                        maze_info[row][col] = draw_mode;
                        drawMaze(gc, row, col);
                    }
                }
                else{
                    //remove previous
                    for(int i:controller.getStartGoalLoc(draw_mode==START?true:false)){
                        int updated_row = i/COL;
                        int updated_col = i%COL;
                        maze_info[updated_row][updated_col] = PATH;
                        drawMaze(gc,updated_row,updated_col);
                    }
                    //set
                    controller.setStartGoal(draw_mode==START?true:false,row,col);
                    for(int i:controller.getStartGoalLoc(draw_mode==START?true:false)){
                        int updated_row = i/COL;
                        int updated_col = i%COL;
                        maze_info[updated_row][updated_col] = draw_mode;
                        drawMaze(gc,updated_row,updated_col);
                    }
                }
            }
        };
        c.setOnMouseClicked(handler);
        gc = c.getGraphicsContext2D();

        //initialize Maze canvas
        gc.setFill(COLOR_REF[PATH]);
        gc.fillRect(0,0,CANVAS_WIDTH,CANVAS_HEIGHT);

        //initialize Grid
        gc.setFill(COLOR_GRID);
        for(int i=0;i<=COL;i++){
            gc.moveTo(i*CELL_SIZE,0);
            gc.lineTo(i*CELL_SIZE,CANVAS_HEIGHT);
            gc.stroke();
        }
        for(int i=0;i<=ROW;i++){
            gc.moveTo(0,i*CELL_SIZE);
            gc.lineTo(CANVAS_WIDTH,i*CELL_SIZE);
            gc.stroke();
        }
        //start goal
        for(int i=0;i<ROW;i++)
            for(int j=0;j<COL;j++)
                if(maze_info[i][j]!=PATH)drawMaze(gc,i,j);
        return c;
    }

    private Canvas createCanvasRobot(){
        int square = ROBOT_SIZE*CELL_SIZE;
        canvas_robot = new Canvas(square,square);
        canvas_robot.setVisible(false);

        GraphicsContext gc = canvas_robot.getGraphicsContext2D();
        gc.setFill(COLOR_ROBOT);
        gc.fillRect(0,0,square,square);
        //grid
        gc.setFill(COLOR_GRID);
        for(int i=0;i<=ROBOT_SIZE;i++){
            gc.moveTo(i*CELL_SIZE,0);
            gc.lineTo(i*CELL_SIZE,square);
            gc.stroke();
        }
        for(int i=0;i<=ROBOT_SIZE;i++){
            gc.moveTo(0,i*CELL_SIZE);
            gc.lineTo(square,i*CELL_SIZE);
            gc.stroke();
        }
        return canvas_robot;
    }

    private void drawMaze(GraphicsContext gc,int row,int col){
        int a = col*CELL_SIZE;
        int b = (col+1)*CELL_SIZE;
        int c = row*CELL_SIZE;
        int d = (row+1)*CELL_SIZE;
        //maze
        gc.setFill(COLOR_REF[maze_info[row][col]]);
        gc.fillRect(a,c,CELL_SIZE,CELL_SIZE);

        //grid
        gc.setFill(COLOR_GRID);
        //Horizon 1
        gc.moveTo(a,c);
        gc.lineTo(b,c);
        gc.stroke();
        //Horizon 2
        gc.moveTo(a,d);
        gc.lineTo(b,d);
        gc.stroke();
        //Vertical 1
        gc.moveTo(a,c);
        gc.lineTo(a,d);
        gc.stroke();
        //Vertical 2
        gc.moveTo(a,c);
        gc.lineTo(a,d);
        gc.stroke();
    }

    //Create all side buttons
    private Button[] createMazeSetupButtons(){
        String[] buttons_text = {"Obstacle","Path","Start","Goal"};
        Button[] buttons = new Button[buttons_text.length];
        EventHandler handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                String object_string = event.getSource().toString();
                if(object_string.contains(buttons_text[0]))
                    draw_mode = OBSTACLE;
                else if(object_string.contains(buttons_text[1]))
                    draw_mode = PATH;
                else if(object_string.contains(buttons_text[2]))
                    draw_mode = START;
                else if(object_string.contains(buttons_text[3]))
                    draw_mode = GOAL;
                else
                    draw_mode = PATH;
            }
        };
        //insert button
        for(int i=0;i<buttons_text.length;i++)
            buttons[i] = createButton(buttons_text[i],BUTTON_SIDE_X,BUTTON_SIDE_Y+i*BUTTON_HEIGHT,handler);
        return buttons;
    }


    //Create bottom buttons
    private Button[] createBottomButtons(){
        String[] buttons_text = {"Save Map","Load Map","Explore"};
        Button[] buttons = new Button[buttons_text.length];
        EventHandler handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                if(event.getTarget().toString().contains(buttons_text[0])){

                }
                else if(event.getTarget().toString().contains(buttons_text[1])){

                }
                else if(event.getTarget().toString().contains(buttons_text[2])){
                    robot_loc = null;
                    //canvas_robot.setVisible(true);
                    //int[] loc = getStartIndex();
                    // canvas_robot.setLayoutX(CANVAS_X+loc[1]*CELL_SIZE);
                    //canvas_robot.setLayoutY(CANVAS_Y+loc[0]*CELL_SIZE);

                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            robot_loc = controller.getRobotLocation();
                            //canvas_robot.setLayoutX(CANVAS_X+robot_loc[0][1]*CELL_SIZE);
                            //canvas_robot.setLayoutY(CANVAS_Y+robot_loc[0][0]*CELL_SIZE);
                            if(controller.getPrevious()!=null){
                                for(int[] i:controller.getPrevious()){
                                    if(i[0]<0||i[0]>=ROW)continue;
                                    if(i[1]<0||i[1]>=COL)continue;
                                    maze_info[i[0]][i[1]] = EXPLORED;
                                    drawMaze(gc,i[0],i[1]);
                                }
                            }
                            for(int[] robot_loc:controller.getRobotLocation()){
                                maze_info[robot_loc[0]][robot_loc[1]] = ROBOT;
                                drawMaze(gc,robot_loc[0],robot_loc[1]);
                            }
                        }
                    },0,SLEEP_DURATION);

                    Timer t2 = new Timer();
                    t2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            controller.startRobot();
                        }
                    },SLEEP_DURATION);
                }
            }
        };
        //insert button
        int first_y = CANVAS_Y+CANVAS_HEIGHT+MARGIN;
        for(int i=0;i<buttons_text.length;i++)
            buttons[i] = createButton(buttons_text[i],CANVAS_X+(i%2)*BUTTON_WIDTH,first_y+i/2*BUTTON_HEIGHT,handler);
        return buttons;
    }

    private Button createButton(String text, double x, double y, EventHandler listener){
        Button button = new Button(text);
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefSize(BUTTON_WIDTH,BUTTON_HEIGHT);
        button.setOnAction(listener);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
