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
        androidService.waitToStartExploration();
        while (tests < 10){

            rpiService.moveForward(1);


//            rpiService.moveForward(10);
            try{
                Thread.sleep(5000);
            }catch (InterruptedException ite){
                ite.printStackTrace();
            }
            tests++;
        }

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
            toRPi.print(message);
            toRPi.flush();
        }catch (Exception e){
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


//
//package tcpcomm;
//
//        import java.io.IOException;
//        import java.io.PrintWriter;
//        import java.net.Socket;
//        import java.net.UnknownHostException;
//        import java.util.Scanner;
//
//        import datatypes.Message;
//
//public class PCClient {
//
//    public static final String RPI_IP_ADDRESS = "192.168.2.2";
//    public static final int RPI_PORT = 3053;
//
//    private static PCClient _instance;
//    private Socket _clientSocket;
//    private PrintWriter _toRPi;
//    private Scanner _fromRPi;
//
//    private PCClient() {
//
//    }
//
//    public static PCClient getInstance() {
//        if (_instance == null) {
//            _instance = new PCClient();
//        }
//        return _instance;
//    }
//
//    public static void main (String[] args) throws UnknownHostException, IOException {
//
//        PCClient pcClient = PCClient.getInstance();
//        pcClient.setUpConnection(RPI_IP_ADDRESS, RPI_PORT);
//        System.out.println("RPi successfully connected");
//        while (true) {
//            pcClient.sendMessage(Message.READ_SENSOR_VALUES);
//            String msgReceived = pcClient.readMessage();
//            System.out.println("Message received: "+ msgReceived);
//        }
//    }
//
//    public void setUpConnection (String IPAddress, int portNumber) throws UnknownHostException, IOException{
//        _clientSocket = new Socket(RPI_IP_ADDRESS, RPI_PORT);
//        _toRPi = new PrintWriter(_clientSocket.getOutputStream());
//        _fromRPi = new Scanner(_clientSocket.getInputStream());
//    }
//
//    public void closeConnection() throws IOException {
//        if (!_clientSocket.isClosed()) {
//            _clientSocket.close();
//        }
//    }
//
//    public void sendMessage(String msg) throws IOException {
//
//        _toRPi.print(msg);
//        _toRPi.flush();
//
//        System.out.println("Message sent: " + msg);
//    }
//
//    public String readMessage() throws IOException {
//
//        String messageReceived = _fromRPi.nextLine();
//        System.out.println("Message received: " + messageReceived);
//
//        return messageReceived;
//    }
//
//}



//RPI code
//import socket
//        import string
//        import time
//        import threading
//
//
//        # Dummy client code
//
//class Test(threading.Thread):
//        def __init__(self):
//        threading.Thread.__init__(self)
//        self.ip = "192.168.10.10" # Connecting to IP address of MDPGrp2
//        self.port = 5182
//
//
//        # Create a TCP/IP socket
//        self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
//        self.client_socket.connect((self.ip, self.port))
//
//        # Send data
//        def write(self, count = 0):
//        print "\nEnter text to send: "
//        msg = raw_input()
//
//        while True:
//        self.client_socket.send(msg)
//        print "\nEnter text to send: "
//        msg = raw_input()
//        count += 1
//        print "quit write()"
//
//
//        # Receive data
//        def receive(self):
//        while True:
//        data = self.client_socket.recv(1024)
//        if len(data) == 0:
//        print "quitting..."
//        break
//        print "\nFrom rpi: %s " % data
//        print "quit receive()"
//
//        def keep_main(self):
//        while True:
//        time.sleep(0.5)
//
//
//
//        if __name__ == "__main__":
//        test = Test()
//
//        rt = threading.Thread(target = test.receive)
//        wt = threading.Thread(target = test.write)
//
//        rt.daemon = True
//        wt.daemon = True
//
//        wt.start()
//        rt.start()
//
//        print "start rt and wt"
//
//        test.keep_main()
//
//        # Close connections
//        self.client_socket.close()
//        print "End of client program"
