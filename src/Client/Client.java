/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/worker of the network.
 * It receives an image from the `middle` layer, reads the text on the image,
 * and sends the text back to the middle layer.
 */

package Client;

import Benchmark.OCRTest;
import Benchmark.Timer;
import SmithWaterman.SWinitialiser;
import net.sourceforge.tess4j.TesseractException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    ArrayList<Long> runTimes = new ArrayList<>();
    ArrayList<Long> transmitTimes = new ArrayList<>(); //maybe remove?
    int test = 1; //test refers to the benchmark performed
    int counter = 0;
    final int iterations = 1; //controls how many times this class performs a bench


    // constructor to put ip address and port
    public Client(String address, int port, int ftpPort) throws IOException, TesseractException {
        Socket socket = new Socket();
        InetSocketAddress sa = new InetSocketAddress(address, port);
        socket.connect(sa, 5000);

        easyFTP ftpClient = new easyFTP(address, ftpPort);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());


        switch (test) {
            case 1: //OCR Test
                try {
                    System.out.println("Connected");
                    File image = new File("woahman.png");
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
                    //temporary -> require args in future?
                    iteratedOCRBench(socket, ftpClient, "woahman.png", image);

                    //if test done
                    outputStream.close();
                    closeConnection(socket, out);

                    //cleanup
                    File file = new File(image.getAbsolutePath());
                    file.delete();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break; //end of OCR test

            case 2: //Smith-Waterman Test
                iteratedSWBench(socket, ftpClient);
                closeConnection(socket, out);
                break; //End of Smith-Waterman test


        }
    }// end of client

    /**
     * Method for easily and cleanly closing connection
     *
     * @param out DataOutputStream
     * @param socket Socket
     */

    public void closeConnection(Socket socket, DataOutputStream out) throws IOException {
        try {
            out.writeUTF("over"); //tells the server when to close connection for safe stop
            out.close();
            socket.close();

        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * Runs method OCRBench as many times as specified by `iterations`
     *
     * @param socket
     * @param ftpClient
     * @throws IOException
     */

    public void iteratedOCRBench(Socket socket, easyFTP ftpClient, String imageName, File image) throws IOException {
        for(int i = 0; i < iterations; i++) {
            OCRBench(socket, ftpClient, imageName, image);
            counter++;
        }
    }

    /**
     * Method for performing a benchmark using Optical Character Recognition. This will record
     * the time of each OCR performed and give the total time for each iteration to be performed.
     *
     *
     * @param socket Socket used to connect to Server.java
     * @param ftpClient easyFTP class client
     * @param imageName String
     * @param image File
     */

    public void OCRBench(Socket socket, easyFTP ftpClient, String imageName, File image) throws IOException {
        ArrayList<String> manyOutput = new ArrayList<>(); //output of the image. Arraylist for many iterations of this test
        OCRTest ocrTest = new OCRTest("tessdata");
        String imageText = null;
        long total = 0;

        try {
            image = ftpClient.getFile(imageName);
            ocrTest.setImage(image);
        } catch (IOException e) {
            System.out.println("Grabbing image Failed!");
            e.printStackTrace();
        }

        runTimes = ocrTest.performCompactBenchmark(iterations);
        manyOutput = ocrTest.getManyOutput(); // returns the output

        //individualTransmission(socket, manyOutput);
        compactTransmission(socket, manyOutput);
    }

    /**
     * Runs method SWBench as many times as specified by `iterations`
     *
     * @param socket
     * @param ftpClient
     * @throws IOException
     */

    public void iteratedSWBench(Socket socket, easyFTP ftpClient) throws IOException {
        for (int i = 0; i < iterations; i++){
            SWBench(socket, ftpClient);
            counter++;
        }
    }


    /**
     * Performs the Smith-Waterman algorithm after retrieving files from ftp server.
     *
     * @param socket socket used for data transmission to server
     * @param ftpClient easyFTP client
     * @throws IOException
     */


    public void SWBench(Socket socket, easyFTP ftpClient) throws IOException {
        //NOTE: run time is affected most by query
        String[] inputFiles = {"smallQuery.txt","database.txt","alphabet.txt","scoringmatrix.txt"};
        Timer timer = new Timer();
        File file;
        ArrayList<String> SWOutput = new ArrayList<>();
        int m = 1; //todo replace with args?
        int k = 1;

        timer.start();
        for(String path : inputFiles){
            ftpClient.getFile(path);
            file = new File(path);
            file.deleteOnExit();
        }
        timer.stopAndPrint("SW File Requests");

        try {
            timer.start();
            SWOutput.add(new SWinitialiser().run(inputFiles[0], inputFiles[1], inputFiles[2], inputFiles[3], m, k));
            timer.stopAndPrint("SW run");

            timer.start();
            compactTransmission(socket, SWOutput);
            timer.stopAndPrint("SW Transmission");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //cleanup
        for(String path : inputFiles){
            file = new File(path);
            file.delete();
        }
    }

    /**
     * Individual transmission sends each value of the ArrayList input separately
     * from one another.
     *
     * @param socket
     * @param manyOutput
     * @throws IOException
     */

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

    /**
     * Compact transmission sends all data at once by inputting an ArrayList and
     * turning the ArrayList into a semicolon seperated string.
     *
     * @param socket socket to connect to Server.java
     * @param manyOutput ArrayList<String>
     * @throws IOException
     */

    public void compactTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        String outputString = manyOutput.get(0); //Semicolon seperated values of manyOutput
        Timer timer = new Timer();

        //allows for proper formatting
        manyOutput.remove(0);
        //Converts ArrayList to semicolon seperated values
        for(String output : manyOutput) {
            outputString = outputString + ";" + output;
        }

        //removes unnecessary new lines
        outputString = outputString.replace("\n", "").replace("\r", "");

        //times the transmission until it is done
        timer.start();
        dataOutput.writeUTF(outputString);
        timer.stopAndPrint("Transmission Start: Compact");
    }
}