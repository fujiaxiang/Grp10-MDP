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

    private static SimuSensorService instance = new SimuSensorService();

    public static SimuSensorService getInstance(){
        if(instance==null)
            instance= new SimuSensorService();
        return instance;
    }

    /**
     * returns the distance between the sensor and the obstacle or wall, should there be any within sensor range
     * if nothing detected within sensor range, returns -1
     * @param sensor
     * @return
     */
    @Override
    public int detectObstacle(Sensor sensor) {
        int[] location = new int[2];
        boolean[][] maze = Arena.getInstance().getMaze();

        for(int step=sensor.getMinRange(); step<=sensor.getMaxRange(); step++){
            try{
                int[] tempLocation = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), step);
                boolean obstacle = maze[tempLocation[0]][tempLocation[1]];
                if(obstacle)
                    return step;
            }catch (ArrayIndexOutOfBoundsException e){
                return step;
            }
        }
        return -1;
    }
}
