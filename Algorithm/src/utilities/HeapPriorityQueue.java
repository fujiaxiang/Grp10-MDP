package utilities;

import java.util.ArrayList;

/**
 * Created by Jiaxiang on 6/2/16.
 */
public class HeapPriorityQueue <T extends Comparable<T> & Updatable>{

    private ArrayList<T> heapNodes = new ArrayList<T>();

    public HeapPriorityQueue(T[][] tNodes){
        for(int i=0; i<tNodes.length; i++)
            for(int j=0; j<tNodes[i].length; j++){
                offer(tNodes[i][j]);
            }
    }

    public HeapPriorityQueue(){}

    public boolean offer(T tNode){
        if(tNode==null)
            return false;
        heapNodes.add(tNode);
        fixUp(heapNodes.size());

        return true;
    }

    public T peek(){
        return heapNodes.get(0);
    }

    public T poll(){
        T rootNode = heapNodes.get(0);
        heapNodes.set(0, heapNodes.get(heapNodes.size()));
        heapNodes.remove(heapNodes.size());
        fixDown(0);
        return rootNode;
    }



    private int leftChildIndex(int parentIndex){
        return 2 * parentIndex + 1;
    }

    private int rightChildIndex(int parentIndex){
        return 2 * parentIndex + 2;
    }

    private int parentIndex(int childIndex){
        return (childIndex - 1) / 2;
    }

    private void swop(int index1, int index2){
        T temp = heapNodes.get(index1);
        heapNodes.set(index1, heapNodes.get(index2));
        heapNodes.set(index2, temp);
    }

    public boolean fixUp(int index){
        if(index==0)
            return false;

        int parentIndex = parentIndex(index);

        if(heapNodes.get(index).compareTo(heapNodes.get(parentIndex))<0){
            swop(index, parentIndex);
            fixUp(parentIndex);
            return true;
        }

        return false;
    }

    public boolean fixDown(int index){
        if(parentIndex(index)>=heapNodes.size())
            return false;

        int targetIndex;

        if(heapNodes.get(leftChildIndex(index)).compareTo(heapNodes.get(rightChildIndex(index)))>0)
            targetIndex = rightChildIndex(index);
        else
            targetIndex = leftChildIndex(index);

        if(heapNodes.get(index).compareTo(heapNodes.get(targetIndex))>0) {
            swop(index, targetIndex);
            fixDown(targetIndex);
            return true;
        }

        return false;
    }

    public void update(){
        for(int i=0; i<heapNodes.size(); i++){
            T node = heapNodes.get(i);
            if(node.needUpdate())
                fixUp(i);
        }
    }

}
