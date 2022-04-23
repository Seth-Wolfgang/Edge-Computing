package Network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.ftpserver.ftplet.FtpException;


public class Server
{
    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;

    // constructor with port
    public Server(int port) throws Exception {


        // starts server and waits for a connection
        try {

            server = new ServerSocket(port);
            System.out.println("Network.Server started");
            System.out.println("Waiting for a client ...");
            socket = server.accept();
            System.out.println("Network.Client accepted");

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            String line = "";

            // reads message from client until "Over" is sent
            while (!socket.isClosed()) {
                try {
                    line = in.readUTF();
                    System.out.println(line);
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
            System.out.println("Closing connection");

            // close connection

            socket.close();
            in.close();
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(5000);
    }
}