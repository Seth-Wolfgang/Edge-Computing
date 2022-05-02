/**
 * Author: Seth Wolfgang
 * Date: 4/26/2022
 *
 * For use in creating a standard constructor for FTP in this package
 */

package Client;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class easyFTP extends FTPClient{

    /**
     * Constructor for the easy FTP Client
     * @param address IPV4 address
     * @param port number
     */
    FTPClient ftpClient = new FTPClient();

    public easyFTP(String address, int port){

        try{
            ftpClient.connect(address, port);
            ftpClient.login("user", "");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        } catch (IOException e) {
            System.out.println("FTP Setup Failed");
            e.printStackTrace();
        }

    }

    /**
     * Returns a file from the FTP server using FTPClient's retrieveFile method
     * This method is a fix for a NullPointerException thrown by this class when
     * using retrieveFile in Client.java
     *
     * @param fileName name of file you want
     * @param outputStream output stream of the socket
     * @return file from FTP server
     * @throws IOException
     */

    public File getFile(String fileName, BufferedOutputStream outputStream) throws IOException {
        File file = new File("woahman.png");
        boolean success = ftpClient.retrieveFile(fileName, outputStream);

        if (success){
            System.out.println("File transferred");
            outputStream.flush();
            return file;

        } else{
            System.out.println("File transfer failed!");
            return null;
        }
    }




}
