/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 *
 * This program serves as the client/worker of the network.
 * It receives an image from the `middle` layer, reads the text on the image,
 * and sends the text back to the middle layer.
 */

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.Socket;

public class Client
{
    // constructor to put ip address and port
    public Client(String address, int port) throws IOException, TesseractException {

        DataInputStream input;
        DataOutputStream out = null;
        Socket socket;
        String imageText;
        FTPClient ftpClient = new FTPClient();

        try {

            //sets up FTP client
            ftpClient.connect("75.128.103.105", 2221);
            ftpClient.login("user", "");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            //sets up OCR
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("tessdata");

            // establish a connection
            socket = new Socket(address, port);
            System.out.println("Connected");

            String remoteFile = "woahman.png"; //change to ftp server directory
            File image = new File("woahman.png");
            tesseract.setVariable("user_defined_dpi", "100");
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(image));
            boolean success = ftpClient.retrieveFile(remoteFile, outputStream);
            outputStream.flush();

            if (success)
                System.out.println("File transferred");

            input = new DataInputStream(System.in);

            out    = new DataOutputStream(socket.getOutputStream());
            imageText = tesseract.doOCR(image);
            out.writeUTF(imageText);
            String line = "";

            while (!line.equals("Over")) {
                try {
                    line = input.readLine();
                    out.writeUTF(line);
                } catch (IOException i) {
                    throw new IOException();
                }
            }

            // close the connection
            try {
                input.close();
                out.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
        }
    }


        public static void main(String[] args) throws TesseractException, IOException {
        Client client = new Client("35.40.254.5", 5000);
    }
}