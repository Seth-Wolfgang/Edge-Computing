package Network;

import Benchmark.Timer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread {

    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;
    String line = "";
    int counter = 0;
    Timer timer = new Timer();

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

            timer.start();

            while (!socket.isClosed()) {
               try {
                   line = in.readUTF();
                   if(line.compareTo("") != 0){
                       counter++;
                       System.out.println(line);
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
            timer.printResults();
            System.out.println("Closing connection");

            // close connection


        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void stopServer(Socket socket, DataInputStream in) throws IOException {
        socket.close();
        in.close();
    }
}