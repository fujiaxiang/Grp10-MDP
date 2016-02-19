package services;

import utilities.Orientation;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class RealRPiService implements RPiServiceInterface {

    private static RealRPiService instance;

    public static RealRPiService getInstance(){
        if(instance==null)
            instance= new RealRPiService();
        return instance;
    }

    private RealRPiService(){}

    @Override
    public int moveForward(int steps) {
        return 0;
    }

    @Override
    public int turn(int direction) {
        if(direction == Orientation.FRONT)
            return -1;
        return 0;
    }

    @Override
    public int callibrate() {
        return 0;
    }

    @Override
    public void notifyUIChange() {

    }
}
