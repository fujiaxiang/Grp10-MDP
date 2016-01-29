package sample;

import controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

import models.Arena;
import utilities.Orientation;

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
    private final int BUTTON_WIDTH = 100;
    private final int BUTTON_HEIGHT = 30;
    private final int BUTTON_SIDE_X = SCENE_WIDTH-BUTTON_WIDTH-MARGIN;
    private final int BUTTON_SIDE_Y = CANVAS_Y;
    private final int BUTTON_BOTTOM_X = CANVAS_X;
    private final int BUTTON_BOTTOM_Y = CANVAS_Y+CANVAS_HEIGHT+MARGIN;
    private final int LABEL_TIMER_WIDTH = 50;
    private final int SLEEP_DURATION    = 10;

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

    private final Color[] COLOR_REF = {
            COLOR_PATH,COLOR_OBSTACLE,COLOR_START,COLOR_GOAL,COLOR_ROBOT,COLOR_EXPLORED
            //COLOR_EXPLORED
    };
    private final int CELL_SIZE = CANVAS_WIDTH/Arena.COL;//assume cell is square
    private GraphicsContext gc; //Graphic Context of Canvas
    private ComboBox<String> ddl;
    private Label text_timer;
    private int draw_mode = OBSTACLE;
    private int[][] robot_loc_array;
    private long time;

    @Override
    public void start(Stage primaryStage) throws Exception{
        controller = new Controller();
        primaryStage.setTitle(SCENE_TITLE);
        primaryStage.setScene(new Scene(createGroup(), SCENE_WIDTH,SCENE_HEIGHT,Color.LIGHTGRAY));
        primaryStage.show();
    }

    private Group createGroup(){
        Group g = new Group();
        g.getChildren().add(createCanvas());
        for(Button b:createMazeSetupButtons())
            g.getChildren().add(b);
        for(Button b:createBottomButtons())
            g.getChildren().add(b);
        g.getChildren().add(getDropdownlist());
        g.getChildren().add(getTextTimer());
        g.getChildren().add(createTextLabel());
        return g;
    }

    private ComboBox<String> getDropdownlist(){
        if(ddl==null){
            String[] list = {"Simutation","Real"};
            ddl = new ComboBox<String>();
            ddl.getItems().addAll(list);
            ddl.setPrefSize(BUTTON_WIDTH*2,BUTTON_HEIGHT);
            ddl.setLayoutX(CANVAS_X);
            ddl.setLayoutY(BUTTON_BOTTOM_Y+2*BUTTON_HEIGHT);
            ddl.setValue(list[0]);
        }
        return ddl;
    }
    private Label createTextLabel(){
        Label label = new Label("Timer  : ");
        label.setLayoutX(CANVAS_X+5);
        label.setLayoutY(getDropdownlist().getLayoutY()+BUTTON_HEIGHT);
        label.setPrefSize(LABEL_TIMER_WIDTH,BUTTON_HEIGHT);
        return label;
    }
    private Label getTextTimer(){
        if(text_timer == null){
            text_timer = new Label("0 ");
            text_timer.setPrefSize(BUTTON_WIDTH,BUTTON_HEIGHT);
            text_timer.setLayoutX(CANVAS_X+LABEL_TIMER_WIDTH+5);
            text_timer.setLayoutY(getDropdownlist().getLayoutY()+BUTTON_HEIGHT);
        }
        return text_timer;
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
                if (draw_mode == PATH){
                    controller.setFree(row,col);
                    if(!controller.isInStartGoalArea(row,col))
                        drawGrid(gc,row,col,COLOR_PATH);
                }
                else if(draw_mode == OBSTACLE){
                    if(controller.setObstacle(row,col))
                        drawGrid(gc, row, col,COLOR_OBSTACLE);
                }
                else{
                    //remove previous
                    for(int i:controller.getStartGoalLoc(draw_mode==START)){
                        int updated_row = i/Arena.COL;
                        int updated_col = i%Arena.COL;
                        controller.setFree(updated_row,updated_col);
                        drawGrid(gc,updated_row,updated_col,COLOR_PATH);
                    }
                    //set
                    controller.setStartGoal(draw_mode==START,row,col);
                    for(int i:controller.getStartGoalLoc(draw_mode==START)){
                        int updated_row = i/Arena.COL;
                        int updated_col = i%Arena.COL;
                        drawGrid(gc,updated_row,updated_col,draw_mode==START?COLOR_START:COLOR_GOAL);
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
        for(int i=0;i<=Arena.COL;i++){
            gc.moveTo(i*CELL_SIZE,0);
            gc.lineTo(i*CELL_SIZE,CANVAS_HEIGHT);
            gc.stroke();
        }
        for(int i=0;i<=Arena.ROW;i++){
            gc.moveTo(0,i*CELL_SIZE);
            gc.lineTo(CANVAS_WIDTH,i*CELL_SIZE);
            gc.stroke();
        }
        //start goal
        for(int j=0;j<2;j++)
            for(int i:controller.getStartGoalLoc(j%2==0)){
                int row = i/Arena.COL;
                int col = i%Arena.COL;
                drawGrid(gc,row,col,j%2==0?COLOR_START:COLOR_GOAL);
            }
        return c;
    }

    private void drawGrid(GraphicsContext gc,int row,int col,Color color){
        int a = col*CELL_SIZE;
        //int b = (col+1)*CELL_SIZE;
        int c = row*CELL_SIZE;
        //int d = (row+1)*CELL_SIZE;

        //grid
        gc.setFill(COLOR_GRID);
        gc.fillRect(a,c,CELL_SIZE,CELL_SIZE);
        //maze
        gc.setFill(color);
        gc.fillRect(a+1,c+1,CELL_SIZE-2,CELL_SIZE-2);

        /*
        Slow, involved array creation, removed
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
        gc.stroke();*/
    }

    //Create all side buttons
    private Button[] createMazeSetupButtons(){
        String[] buttons_text = {"Obstacle","Path","Start","Goal"};
        Button[] buttons = new Button[buttons_text.length];
        EventHandler handler = (event)->{
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
        time = 0;
        EventHandler handler = (event)-> {
                if(event.getTarget().toString().contains(buttons_text[0])){

                }
                else if(event.getTarget().toString().contains(buttons_text[1])){

                }
                else if(event.getTarget().toString().contains(buttons_text[2])){
                    Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            if(controller.needUpdate()) {
                                robot_loc_array = controller.getRobotLocation();
                                int orientation;
                                switch(controller.getRobotOrientation()){
                                    case Orientation.NORTH:orientation = 1;break;
                                    case Orientation.WEST:orientation = 3;break;
                                    case Orientation.EAST:orientation = 5;break;
                                    case Orientation.SOUTH:orientation = 7;break;
                                    default : orientation = 0;
                                }
                                for (int i = 0; i < controller.getPrevious().length; i++) {
                                    if (controller.getPrevious()[i][0] < 0)//Shoult noe happen || controller.getPrevious()[i][0] >= Arena.ROW)
                                        break;
                                    drawGrid(gc, controller.getPrevious()[i][0], controller.getPrevious()[i][1], COLOR_EXPLORED);
                                }
                                for (int i = 0; i < robot_loc_array.length; i++)
                                    drawGrid(gc, robot_loc_array[i][0], robot_loc_array[i][1], i==orientation?COLOR_ROBOT_FACE:COLOR_ROBOT);


                                controller.updated();
                            }
                            if(controller.isDone()){
                                System.out.println("Done");
                                timer.cancel();
                                //add in stop for timer
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

                    Timer t3 = new Timer();//for update label
                    t3.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            time+=100;
                            Platform.runLater(()->{
                                getTextTimer().setText(Long.toString(time));
                            });
                        }
                    },0,100);
                }
            };
        //insert button
        for(int i=0;i<buttons_text.length;i++)
            buttons[i] = createButton(buttons_text[i],BUTTON_BOTTOM_X+(i%2)*BUTTON_WIDTH,BUTTON_BOTTOM_Y+i/2*BUTTON_HEIGHT,handler);
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
