package Network;

import Benchmark.Timer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread {

    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       = null;
    String line = "";
    int counter = 0;
    Timer timer = new Timer();
    ArrayList<String> input = new ArrayList<>();

    // constructor with port
    public Server(int port) throws Exception {


        // starts server and waits for a connection
        try {

            server = new ServerSocket(port);
            System.out.println("Server started");
            System.out.println("Waiting for a client ...");
            socket = server.accept();
            System.out.println("Client accepted");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            boolean notRunning = true;

            while (!socket.isClosed()) { //Replace with lambda
               try {
                   if(notRunning){
                       timer.start();
                       notRunning = false;
                   }
                   line = in.readUTF();
                   if(line.compareTo("") != 0){
                       counter++;
                       timer.newLap();
                       if(counter == 10){ //todo: this is a placeholder (replace with args)
                           timer.stopTimer();
                           stopServer(socket, in);
                       }
                   }

               } catch (IOException e) {
                   stopServer(socket, in);
                   throw new Exception();
               }
            }
            File results = new File("Results.txt");
            System.out.println("Closing connection");

            // close connection


        } catch (Exception e) {
            throw new Exception();
        }
        timer.printResults("Transmission Received");
    }

    public void stopServer(Socket socket, DataInputStream in) throws IOException {
        socket.close();
        in.close();
    }
}