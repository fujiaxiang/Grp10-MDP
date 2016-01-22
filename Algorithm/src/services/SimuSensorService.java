package services;

import models.Sensor;
//import GlabalUtilities;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class SimuSensorService implements SensorServiceInterface{
    @Override
    public int detectObstacle(Sensor sensor) {
        int[] location = new int[2];

        switch(sensor.getOrientation()){
//            case(GlobalUtilities.Orientation):
//                break;

        }
        return 0;
    }
}
