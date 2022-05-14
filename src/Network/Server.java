package Network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;


public class Server extends Thread {

    //initialize socket and input stream
    private ServerSocket server = null;
    private DataInputStream in = null;

    // constructor with port
    public Server(int port) throws IOException {
        int clientNum = 1; // assigns client number

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

                Thread newClient = new ClientHandler(socket, in, clientNum);
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

    public void allowNewClient(Socket socket, DataInputStream in, int clientNum) {
        try {
            Thread clientThread = new Thread(new ClientHandler(socket, in, clientNum));
            clientThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}