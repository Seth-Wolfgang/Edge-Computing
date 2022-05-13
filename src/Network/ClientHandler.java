package Network;

import Benchmark.Timer;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

class ClientHandler extends Thread {
    final Timer timer = new Timer();
    final DataInputStream in;
    final Socket socket;
    final int clientNum;

    // Constructor
    public ClientHandler(Socket s, DataInputStream dis, int clientNum) {
        this.socket = s;
        this.in = dis;
        this.clientNum = clientNum;
    }

    @Override
    public void run() {
        String line;
        int counter = 0;
        boolean notRunning = true;

        while (!socket.isClosed()) {
            try {
                if (notRunning) {
                    timer.start();
                    notRunning = false;
                }

                //reads input from Client.java
                line = in.readUTF();

                /*if statements break loop if they receive a certain
                number of inputs or receive an input reading 'over' */
                if (line.compareTo("") != 0 && !line.equals("over")) {
                    counter++;
                    System.out.println("\033[1;34mClient "+ this.clientNum +" iteration # " + counter + " done.\033[0m");
                    timer.newLap();
                    if (counter == 10) { //todo: this is a placeholder (replace with args)
                        timer.stopTimer();
                        break;
                    }
                } else if (line.equals("over")) {
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } // end of while loop
        //closes resources and prints to result file
        try {
            this.in.close();
            File results = new File("Results.txt");
            System.out.println("Transmission received from: " + this.socket);
            timer.printResults("Transmission Received");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}