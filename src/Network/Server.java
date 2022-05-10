package Network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread {

    //initialize socket and input stream
    private ServerSocket server = null;
    private DataInputStream in = null;

    // constructor with port
    public Server(int port) throws IOException {

        server = new ServerSocket(port);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");


        while (true) {
            // starts server and waits for a connection
            Socket socket = null;

            try {

                socket = server.accept();
                System.out.println("Client accepted");
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                Thread newClient = new ClientHandler(socket, in);
                newClient.start();

            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    } //end of server

    public void stopServer(Socket socket, DataInputStream in) throws IOException {
        System.out.println("Closing connection");
        socket.close();
        in.close();
    }

    public void allowNewClient(Socket socket, DataInputStream in) {
        try {
            Thread clientThread = new Thread(new ClientHandler(socket, in));
            clientThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}