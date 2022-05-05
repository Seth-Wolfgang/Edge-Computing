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
    int test = 2; //test refers to the benchmark performed

    // constructor to put ip address and port
    public Client(String address, int port, int ftpPort) throws IOException, TesseractException {
        Socket socket = new Socket(address, port);
        easyFTP ftpClient = new easyFTP(address, ftpPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());


        switch (test) {
            case 1: //OCR Test
                try {
                    //establish connection to Server.java
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
                SWBench(socket, ftpClient);

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
        String imageText = null;
        long total = 0;

        try {
            image = ftpClient.getFile("woahman.png");
            ocrTest.setImage(image);
        } catch (IOException e) {
            System.out.println("Grabbing image Failed!");
            e.printStackTrace();
        }

        runTimes = ocrTest.performCompactBenchmark(10);
        manyOutput = ocrTest.getManyOutput(); // returns the output

        individualTransmission(socket, manyOutput);
        //todo compactTransmission(socket, manyOutput);
        //records the start of transmission

        //cleanup
        image.delete();
    }

    /**
     * Performs the Smith-Waterman algorithm after retrieving files from ftp server.
     *
     * @param socket socket used for data transmission to server
     * @param ftpClient easyFTP client
     * @throws IOException
     */


    public void SWBench(Socket socket, easyFTP ftpClient) throws IOException {
        String[] inputFiles = {"query.txt","database.txt","alphabet.txt","scoringmatrix.txt"};
        File file;

        for(String path : inputFiles){
            ftpClient.getFile(path);
        }

        int m = 1; //todo replace with args?
        int k = 1;
        try {
            new SWinitialiser().run(inputFiles[0], inputFiles[1], inputFiles[2], inputFiles[3], m, k);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //cleanup
        for(String path : inputFiles){
            file = new File(path);
            file.delete();
        }
    }

    public void individualTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        Timer timer = new Timer();
        timer.start();
        for (String out : manyOutput){
            dataOutput.writeUTF(out);
            timer.newLap();
        }
        timer.stopTimer();
        timer.printResults("Transmission Start: Individual");
    }

    public void compactTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        ObjectOutputStream dataOutput = new ObjectOutputStream(socket.getOutputStream());
        Timer timer = new Timer();
        timer.start();
        dataOutput.writeObject(manyOutput);
        timer.stopTimer();
        timer.printResults("Transmission Start: Compact");
    }
}