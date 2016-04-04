package services;

import models.Robot;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public interface RPiServiceInterface {

    int moveForward(int steps);

    int turn(int direction);

    int callibrate();

    int turnDegree(double degree);

    int moveDistance(double distance);

    void notifyUIChange();

}
