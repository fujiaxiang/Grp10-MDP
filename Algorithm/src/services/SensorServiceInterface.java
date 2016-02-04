package services;

import models.Sensor;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public interface SensorServiceInterface {
    int detectObstacle(Sensor sensor);

    String detect();
}
