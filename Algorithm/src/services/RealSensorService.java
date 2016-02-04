package services;

import models.Sensor;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class RealSensorService implements SensorServiceInterface{

    private static RealSensorService instance;

    public static RealSensorService getInstance(){
        if(instance==null)
            instance= new RealSensorService();
        return instance;
    }

    private RealSensorService(){}

    @Override
    public String detect() {
        return null;
    }

    @Override
    public int detectObstacle(Sensor sensor) {
        return 0;
    }
}
