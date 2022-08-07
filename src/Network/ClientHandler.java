/**
 * This lovely, short class should be a method, instead it needs to run on a
 *  separate thread for the project to allow multiple clients.
 *
 */

package Network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {

    Socket socket;
    // constructor with port
    public ClientHandler(Socket socket, int test, int size, int iterations, int clientNum, int activeClients) throws IOException {
        this.socket = socket;
        DataOutputStream dataOutput = new DataOutputStream(this.socket.getOutputStream());
        System.out.println("ES:Client connected");
        String message = test + ";" + size + ";" + iterations + ";" + clientNum + ";" + activeClients;
        dataOutput.writeUTF(message);
        dataOutput.flush();
    } //end of ClientHandler
}
