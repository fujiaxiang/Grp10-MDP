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

    public static final String RPI_IP_ADDRESS = "192.168.10.88";
    public static final int RPI_PORT = 5182;

    private static TcpService instance;
    private Socket clientSocket;
    private PrintWriter toRPi;
    private Scanner fromRPi;

    private TcpService() {

    }

    public static TcpService getInstance() {
        if (instance == null) {
            instance = new TcpService();
        }
        return instance;
    }

    public static void main (String[] args) throws IOException {

        TcpService tcpService = TcpService.getInstance();
        try {
            tcpService.connectToHost(RPI_IP_ADDRESS, RPI_PORT);
        }catch (IOException e){
            e.printStackTrace();
            tcpService.connectToHost(RPI_IP_ADDRESS, RPI_PORT);
        }
        System.out.println("RPi successfully connected");
        int tests = 0;
        while (tests < 10) {
            tcpService.sendMessage("Message successfully sent from pc to rpi");
            String msgReceived = tcpService.readMessage();
            System.out.println("Message received: "+ msgReceived);
            tests++;
        }
        tcpService.closeConnection();
        System.out.println("Connection closed");
    }

    public void connectToHost(String IPAddress, int portNumber) throws IOException{
        clientSocket = new Socket(RPI_IP_ADDRESS, RPI_PORT);
        toRPi = new PrintWriter(clientSocket.getOutputStream());
        fromRPi = new Scanner(clientSocket.getInputStream());
    }

    public void closeConnection() throws IOException {
        if (!clientSocket.isClosed()) {
            clientSocket.close();
        }
    }

    public void sendMessage(String message) throws IOException {
        try{
            connectToHost(RPI_IP_ADDRESS, RPI_PORT);
        }catch (IOException e){
            e.printStackTrace();
            connectToHost(RPI_IP_ADDRESS, RPI_PORT);
        }
        toRPi.print(message);
        toRPi.flush();

        System.out.println("Message sent: " + message);

        closeConnection();
    }

    public String readMessage() throws IOException {

        String messageReceived = fromRPi.nextLine();
        System.out.println("Message received: " + messageReceived);

        return messageReceived;
    }

}
