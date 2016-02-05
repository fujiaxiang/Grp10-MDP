package controllers;

import models.Robot;

/**
 * Created by Jiaxiang on 5/2/16.
 */
public class PathFinder {
    private static PathFinder instance = new PathFinder();
    private final Controller controller = Controller.getInstance();
    private final Robot robot = Robot.getInstance();

    private PathFinder(){}

    public static PathFinder getInstance(){
        if(instance==null)
            instance = new PathFinder();
        return instance;
    }
}
