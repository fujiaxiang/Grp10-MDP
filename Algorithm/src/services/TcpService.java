package services;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


/**
 * Created by Jiaxiang on 19/2/16.
 */
public class TcpService {

    public static final String RPI_IP_ADDRESS = "192.168.10.10";
    public static final int RPI_PORT = 5182;

    private static TcpService instance;
    private Socket clientSocket;
    private PrintWriter toRPi;
    private Scanner fromRPi;
    private final int DELAY_IN_SENDING_MESSAGE = 2;

    private final int TIME_TO_RETRY = 1000;       //wait for 1 second to retry

    private TcpService(){}

    public static TcpService getInstance(){
        if (instance == null) {
            instance = new TcpService();
        }
        return instance;
    }

    public static void main (String[] args){

        TcpService tcpService = TcpService.getInstance();
        tcpService.connectToHost();
        AndroidServiceInterface androidService = RealAndroidService.getInstance();
        RPiServiceInterface rpiService = RealRPiService.getInstance();

        int tests = 0;

        try{
            Thread.sleep(3000);
        }catch (InterruptedException ite){
            ite.printStackTrace();
        }
//        while(tests < 20) {
//            TcpService.getInstance().sendMessage("hA10|");
//            TcpService.getInstance().readMessage();
//            TcpService.getInstance().sendMessage("hD10|");
//            TcpService.getInstance().readMessage();
//            tests++;
//        }

//
//        ****Message sent: hF632|****
//        ****Message sent: hA79|****
//        ****Message sent: hF1529|****
//        ****Message sent: hD0|****
//        ****Message sent: hF300|****
//
//        TcpService.getInstance().sendMessage("hA90|");
//        TcpService.getInstance().readMessage();
//        TcpService.getInstance().sendMessage("hW4|");
//



//        androidService.waitToRunShortestPath();
//
//        rpiService.turn(Orientation.LEFT);
//
//        rpiService.turn(Orientation.RIGHT);
//
//        rpiService.turn(Orientation.BACK);

        try{
            Thread.sleep(1000000);
        }catch (InterruptedException ite){
            ite.printStackTrace();
        }

        tcpService.closeConnection();
    }

    public void connectToHost(){
        try {
            clientSocket = new Socket(RPI_IP_ADDRESS, RPI_PORT);
            toRPi = new PrintWriter(clientSocket.getOutputStream());
            fromRPi = new Scanner(clientSocket.getInputStream());
        }catch (IOException ioe){
            ioe.printStackTrace();
            try{
                Thread.sleep(TIME_TO_RETRY);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            connectToHost();
        }
        System.out.println("RPi successfully connected");
    }

    public void closeConnection(){
        try {
            if (!clientSocket.isClosed()){
                clientSocket.close();
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            try{
                Thread.sleep(TIME_TO_RETRY);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            closeConnection();
        }
        System.out.println("Connection closed");
    }

    public void sendMessage(String message){
        try {
            Thread.sleep(DELAY_IN_SENDING_MESSAGE);
            toRPi.print(message);
            toRPi.flush();
        }catch (InterruptedException ite){
            ite.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
            try{
                Thread.sleep(TIME_TO_RETRY);
            }catch (InterruptedException ite){
                ite.printStackTrace();
            }
            connectToHost();
            sendMessage(message);
        }

        System.out.println("Message sent: ****" + message + "****");
    }

    public String readMessage(){

        String messageReceived = "";
        try {
            messageReceived = fromRPi.nextLine();
            System.out.println("Message received: ****" + messageReceived + "****");

        }catch (Exception e){
            e.printStackTrace();
            try{
                Thread.sleep(TIME_TO_RETRY);
            }catch (InterruptedException ite){
                ite.printStackTrace();
            }
            connectToHost();
            readMessage();
        }

        return messageReceived;
    }

}

