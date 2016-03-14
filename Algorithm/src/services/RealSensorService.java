package services;

import models.Sensor;
import utilities.Messages;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class RealSensorService implements SensorServiceInterface{

    private static RealSensorService instance;

    private TcpService tcpService = TcpService.getInstance();

    public static RealSensorService getInstance(){
        if(instance==null)
            instance= new RealSensorService();
        return instance;
    }

    private RealSensorService(){}

    @Override
    public String detect() {

        tcpService.sendMessage(Messages.ARDUINO_CODE + Messages.detectObstacles() + Messages.ARDUINO_END_CODE);

        String returnMessage = tcpService.readMessage();
        
        return returnMessage;
    }


    //this function is not used in real run
    @Override
    public int detectObstacle(Sensor sensor) {
        return 0;
    }
}
