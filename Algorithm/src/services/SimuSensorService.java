package services;

import controllers.Controller;
import models.Arena;
import models.Robot;
import models.Sensor;
import static utilities.GlobalUtilities.*;

/**
 * Created by Jiaxiang on 22/1/16.
 * returns the distance between sensor and obstacle or wall
 * returns -1 if no obstacle is location within sensor range
 */


public class SimuSensorService implements SensorServiceInterface{

    private static SimuSensorService instance;

    private Arena realArena = Controller.getInstance().getArena();
    private Robot robot = Robot.getInstance();

    public static SimuSensorService getInstance(){
        if(instance==null)
            instance= new SimuSensorService();
        return instance;
    }

    public String detect(){
        String result = "";
        try{
            Thread.sleep(500/Controller.simulationSpeed);
            for (Sensor sensor : robot.getSensors()){
                if(!result.isEmpty())
                    result+=":";
                result+=detectObstacle(sensor);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * returns the distance between the sensor and the obstacle or wall, should there be any within sensor range
     * if nothing detected within sensor range, returns -1
     * @param sensor
     * @return
     */
    @Override
    public int detectObstacle(Sensor sensor) {
        Arena.mazeState[][] maze = realArena.getMaze();
        for(int step=sensor.getMinRange(); step<=sensor.getMaxRange(); step++){
            try{
                int[] tempLocation = locationParser(sensor.getAbsoluteLocation(), sensor.getAbsoluteOrientation(), step);
                Arena.mazeState obstacle = maze[tempLocation[0]][tempLocation[1]];
                if(obstacle==Arena.mazeState.obstacle)
                    return step;
            }catch (ArrayIndexOutOfBoundsException e){
                return step;
            }
        }
        return -1;
    }
}
