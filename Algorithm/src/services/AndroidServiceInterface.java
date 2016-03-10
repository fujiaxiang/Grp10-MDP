package services;

/**
 * Created by Jiaxiang on 29/2/16.
 */
public interface AndroidServiceInterface {
    int waitToStartExploration();

    int waitToRunShortestPath();

    int sendMapDescriptor();

    int sendObstacleInfo();
}
