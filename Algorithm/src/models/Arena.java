package models;

/**
 * Created by Jiaxiang on 22/1/16.
 */
public class Arena {

    public final int COL = 15;
    public final int ROW = 20;

    private static Arena instance = new Arena();

    private Arena(){}

    public static Arena getInstance(){
        if(instance==null)
            instance = new Arena();
        return instance;
    }

}
