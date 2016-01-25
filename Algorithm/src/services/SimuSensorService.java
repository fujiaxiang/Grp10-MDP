package services;

import models.Arena;
import models.Sensor;
import static utilities.GlobalUtilities.*;

/**
 * Created by Jiaxiang on 22/1/16.
 * returns the distance between sensor and obstacle or wall
 * returns -1 if no obstacle is location within sensor range
 */


public class SimuSensorService implements SensorServiceInterface{

    @Override
    public int detectObstacle(Sensor sensor) {
        int[] location = new int[2];
        int[][] maze = Arena.getInstance().getMaze_info();

        for(int step=sensor.getMinRange(); step<=sensor.getMaxRange(); step++){
            try{
                int[] tempLocation = locationParser(sensor.getLocation(), sensor.getAbsoluteOrientation(), step);
                int obstacle = maze[tempLocation[0]][tempLocation[1]];
                if(obstacle!=0)
                    return step;
            }catch (ArrayIndexOutOfBoundsException e){
                return step;
            }
        }
        return -1;
    }
}
