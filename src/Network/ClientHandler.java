/**
 * This lovely, short class should be a method, instead it needs to run on a
 *  separate thread for the project to allow multiple clients.
 *
 * Method sends a string of parameters to each client.
 */

package Network;

import OCR.Timer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    boolean isConfirmed = false;
    Socket socket;
    int test;
    int size;
    int iterations;
    int clientNum;
    int activeClients;
    Timer timer = new Timer();
    //constructor with port
    public ClientHandler(Socket socket, int test, int size, int iterations, int clientNum, int activeClients) throws IOException {
        this.socket = socket;
        this.test = test;
        this.size = size;
        this.iterations = iterations;
        this.clientNum = clientNum;
        this.activeClients = activeClients;
        System.out.println("ES:Client connected");

    } //end of ClientHandler

    public boolean getStatus(){
        return this.isConfirmed;
    }

    public void sendConfigData() throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(this.socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());

        String message = this.test + ";" + this.size + ";" + this.iterations + ";" + this.clientNum + ";" + this.activeClients;

        dataOutput.writeUTF(message);
        dataOutput.flush();

        System.out.println("Sent configuration data to client");

        while(!this.isConfirmed){
            this.isConfirmed = dataInputStream.readBoolean();
        }

    }

    public void updateConfigData(int test, int size, int iterations, int activeClients) {
        this.test = test;
        this.size = size;
        this.iterations = iterations;
        this.activeClients = activeClients;
    }
}
