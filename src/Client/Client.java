/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 *
 */

package Client;

import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Client {

    //Initial vars
    ArrayList<Long> runTimes = new ArrayList<>();
    int test = 2; //test refers to the benchmark performed todo create a better way of handling this
    int counter = 0;
    final int iterations = 100; //controls how many times this class performs a bench


    // constructor to put ip address and port
    public Client(String address, int ftpPort) throws IOException, TesseractException {

        //Setup before connection occurs
        easyFTPClient ftpClient = new easyFTPClient(address, ftpPort);
        System.out.println("Client connected to edge server");


        switch (test) {
            case 1: //OCR Test
                try {
                    //sending image file
                    for(int i = 0; i < 10; i++) {
                        File copiedImage = new File("ftpResources\\woahman" + i + ".png");
                        File image = new File("ftpResources\\woahman.png");
                        FileUtils.copyFile(image, copiedImage);
                        ftpClient.sendFile(copiedImage);
                        copiedImage.delete();
                    }

                    //cleanup


                    break; //end of OCR test
                } catch (Exception e) {
                    e.printStackTrace();
                }

            case 2: //Smith-Waterman Test
                String[] inputFiles = {"smallQuery",
                                       "database",
                                       "alphabet",
                                       "scoringmatrix"};
                File copiedFile = null;
                File file = null;

                for(int i = 0; i < 10; i++) {
                    for(int j = 0; j < 4; j++){
                        copiedFile = new File("ftpResources\\"+ inputFiles[j] + i + ".txt");
                        file = new File("ftpResources\\" + inputFiles[j]+ ".txt");
                        FileUtils.copyFile(file, copiedFile);
                        ftpClient.sendFile(copiedFile);
                        copiedFile.delete();
                    }//end of j loop
                }//end of i loop

                //closeConnection(socket, out);
                break; //End of Smith-Waterman test


       }

    /**
     * Method for easily and cleanly closing connection
     *
     * @param out DataOutputStream
     * @param socket Socket
     */

    //public void closeConnection(Socket socket, DataOutputStream out) throws IOException {
    //    try {
    //        out.writeUTF("over"); //tells the server when to close connection for safe stop
    //        out.close();
    //        socket.close();
//
    //    } catch (IOException e) {
    //        throw new IOException();
    //    }
    //}

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

    //public void OCRBench(Socket socket, easyFTPClient ftpClient, String imageName, File image) throws IOException {
    //    ArrayList<String> manyOutput = new ArrayList<>(); //output of the image. Arraylist for many iterations of this test
    //    OCRTest ocrTest = new OCRTest("tessdata");
    //    String imageText = null;
//
    //    try {
    //        //grabs image from EdgeServer and sets it as the image for OCR to run with
    //        image = ftpClient.getFile(imageName);
    //        ocrTest.setImage(image);
    //    } catch (IOException e) {
    //        System.out.println("Grabbing image Failed!");
    //        e.printStackTrace();
    //    }
//
    //    //runs the test
    //    runTimes = ocrTest.performCompactBenchmark(iterations);
    //    manyOutput = ocrTest.getManyOutput(); // returns the output
//
    //    //individualTransmission(socket, manyOutput);
    //    compactTransmission(socket, manyOutput);
    //}

    /**
     * Runs method SWBench as many times as specified by `iterations`
     *
     * @param socket
     * @param ftpClient
     * @throws IOException
     */

   // public void iteratedSWBench(Socket socket, easyFTPClient ftpClient) throws IOException {
   //     for (int i = 0; i < iterations; i++){
   //         SWBench(socket, ftpClient);
   //         counter++;
   //     }
   // }


    /**
     * Performs the Smith-Waterman algorithm after retrieving files from ftp server.
     *
     * @param socket socket used for data transmission to server
     * @param ftpClient easyFTP client
     * @throws IOException
     */



//
    //    //cleanup
    //    for(String path : inputFiles){
    //        file = new File(path);
    //        file.delete();
    //    }
    }

}    // end of client
