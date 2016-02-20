package services;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
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

        int tests = 0;
        while (tests < 10){
            tcpService.sendMessage("Message successfully sent from pc to rpi");
            String msgReceived = tcpService.readMessage();
            System.out.println("Message received: "+ msgReceived);
            tests++;
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
            closeConnection();
        }
        System.out.println("Connection closed");
    }

    public void sendMessage(String message){
        try {
            toRPi.print(message);
            toRPi.flush();
        }catch (Exception e){
            e.printStackTrace();
            connectToHost();
            sendMessage(message);
        }

        System.out.println("Message sent: " + message);
    }

    public String readMessage(){

        String messageReceived = "";

        try {
            messageReceived = fromRPi.nextLine();
            System.out.println("Message received: " + messageReceived);

        }catch (Exception e){
            e.printStackTrace();
            closeConnection();
            readMessage();
        }

        return messageReceived;
    }

}
