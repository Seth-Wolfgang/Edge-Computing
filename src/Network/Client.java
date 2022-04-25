package Network; /**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 *
 * This program serves as the client/worker of the network.
 * It receives an image from the `middle` layer, reads the text on the image,
 * and sends the text back to the middle layer.
 */

import Benchmark.OCRTest;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client
{
    // constructor to put ip address and port
    public Client(String address, int port) throws IOException, TesseractException {

        OCRTest ocrTest = new OCRTest("tessdata");
        ArrayList<Long> runTimes = new ArrayList<>();
        ArrayList<Long> transmitTimes = new ArrayList<>();
        Socket socket = new Socket(address, port);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String imageText = null;
        FTPClient ftpClient = new FTPClient();

        try {

            //sets up FTP client
            ftpClient.connect("127.0.0.1", 2221);
            ftpClient.login("user", "");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //sets up OCR


            // establish a connection

            System.out.println("Connected");

            String remoteFile = "woahman.png"; //change to ftp server directory
            File image = new File("woahman.png");
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.flush();

            if (success){
                System.out.println("File transferred");
                ocrTest.setImage(image);
            }


            runTimes = ocrTest.performCompactBenchmark(1);
            
            long total = 0;
            for(int i = 0; i < runTimes.size(); i++){
                System.out.println(runTimes.get(i) / 1000000000.0);
                total = total + runTimes.get(i);

            }
            System.out.println(total / 1000000000.0);

            out.writeUTF(ocrTest.doOCR());

            // close the connection
            try {

                out.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        public static void main(String[] args) throws TesseractException, IOException {
        //todo replace client with separate classes for each benchmark
        Client client = new Client("127.0.0.1", 5000);
    }
}