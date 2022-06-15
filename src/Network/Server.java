package Network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server extends Thread {

    Socket socket;
    //initialize socket and input stream
    private final ServerSocket server;
    private DataInputStream in;

    // constructor with port
    public Server(int port) throws IOException {
        int clientNum = 1; // assigns client number
        server = new ServerSocket(port);
        System.out.println("Server started");
        System.out.println("Waiting for a client ...");


        while (true) {
            try {
                // starts server and waits for a connection
                socket = server.accept();
                System.out.println("Client accepted");

                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                //Creates new thread when there is a new client
                Thread newClient = new EdgeHandler(socket, in, clientNum);
                newClient.start();

                clientNum++;
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
    } //end of server

    /**
     * Method to stop server.
     *
     * @param socket
     * @param in
     * @throws IOException
     */

    public void stopServer(Socket socket, DataInputStream in) throws IOException {
        System.out.println("Closing connection");
        socket.close();
        in.close();
    }
}