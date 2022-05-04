package Client;
/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * <p>
 * This program serves as the client/worker of the network.
 * It receives an image from the `middle` layer, reads the text on the image,
 * and sends the text back to the middle layer.
 */

import Benchmark.OCRTest;
import Benchmark.Timer;
import SmithWaterman.SWinitialiser;
import net.sourceforge.tess4j.TesseractException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    ArrayList<Long> runTimes = new ArrayList<>();
    ArrayList<Long> transmitTimes = new ArrayList<>(); //maybe remove?
    int test = 1; //test refers to the benchmark performed

    // constructor to put ip address and port
    public Client(String address, int port, int ftpPort) throws IOException, TesseractException {
        Socket socket = new Socket(address, port);
        easyFTP ftpClient = new easyFTP(address, ftpPort);

        switch (test) {
            case 1: //OCR Test
                try {
                    //establish connection to Server.java

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Connected");

                    File image = new File("woahman.png");
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
                    //temporary -> require args in future?
                    OCRBench(socket, ftpClient, "woahman.png", image, outputStream);

                    //if test done
                    //todo create a way to know when to stop
                    closeConnection(out, socket);

                    // close the connection
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case 2: //Smith-Waterman Test

                String queryFile = "src\\SmithWaterman\\query.txt";
                String databaseFile = "src\\SmithWaterman\\database.txt";
                String alphabetFile = "src\\SmithWaterman\\alphabet.txt";
                String scorematrixFile = "src\\SmithWaterman\\scoringmatrix.txt";
                int m = 1; //todo replace with args?
                int k = 1;
                try {
                    new SWinitialiser().run(queryFile, databaseFile, alphabetFile, scorematrixFile, m, k);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break; //End of Smith-Waterman test


        }
    }// end of client

    /**
     * Method for easily and cleanly closing connection
     *
     * @param out DataOutputStream
     * @param socket Socket
     */

    public void closeConnection(DataOutputStream out, Socket socket) {
        try {
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for performing a benchmark using Optical Character Recognition. This will record
     * the time of each OCR performed and give the total time for each iteration to be performed.
     *
     *
     * @param socket Socket
     * @param ftpClient easyFTP
     * @param imageName String
     * @param image File
     * @param outputStream BufferedOutputStream
     */

    public void OCRBench(Socket socket, easyFTP ftpClient, String imageName, File image, BufferedOutputStream outputStream) throws IOException {
        ArrayList<String> manyOutput = new ArrayList<>(); //output of the image. Arraylist for many iterations of this test
        OCRTest ocrTest = new OCRTest("tessdata");
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        String imageText = null;
        Timer timer = new Timer();
        long total = 0;

        try {
            image = ftpClient.getFile("woahman.png", outputStream);
            ocrTest.setImage(image);
        } catch (IOException e) {
            System.out.println("Grabbing image Failed!");
            e.printStackTrace();
        }

        runTimes = ocrTest.performCompactBenchmark(10);
        manyOutput = ocrTest.getManyOutput(); // returns the output

        timer.start();
        for (String out : manyOutput){
            dataOutput.writeUTF(out);
            timer.newLap();
        }
        //records the start of transmission
        timer.stopTimer();
        timer.printResults("Transmission Start");
    }
}