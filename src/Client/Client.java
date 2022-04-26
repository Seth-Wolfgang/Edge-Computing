package Client;
/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 *
 * This program serves as the client/worker of the network.
 * It receives an image from the `middle` layer, reads the text on the image,
 * and sends the text back to the middle layer.
 */

import Benchmark.OCRTest;
import net.sourceforge.tess4j.TesseractException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client
{

    // constructor to put ip address and port
    public Client(String address, int port, int ftpPort) throws IOException, TesseractException {

        OCRTest ocrTest = new OCRTest("tessdata");
        ArrayList<Long> runTimes = new ArrayList<>();
        ArrayList<Long> transmitTimes = new ArrayList<>();
        String imageText = null;
        easyFTP ftpClient = new easyFTP(address, ftpPort);
        String remoteFile = "woahman.png";

        try {
            //establish connection to Server.java
            Socket socket = new Socket(address, port);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected");

            //grabs file for OCR
            File image = new File("woahman.png");
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.flush();

            if (success){
                System.out.println("File transferred");
                ocrTest.setImage(image);
            }

            //performs and calculates times for benchmark (not transmission times)
            runTimes = ocrTest.performCompactBenchmark(1);
            
            long total = 0;
            for (Long runTime : runTimes) {
                System.out.println(runTime / 1000000000.0);
                total = total + runTime;

            }
            System.out.println(total / 1000000000.0);
            out.writeUTF(ocrTest.doOCR());

            //if test done
            //todo create a way to know when to stop
            closeConnection(out, socket);

            // close the connection
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void closeConnection(DataOutputStream out, Socket socket){
        try {
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws TesseractException, IOException {
        //todo replace client with separate classes for each benchmark
        Client client = new Client("127.0.0.1", 5000, 2221);
    }

}