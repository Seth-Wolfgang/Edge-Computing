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

import java.io.*;

public class easyFTPClient extends FTPClient{

    /**
     * Constructor for the easy FTP Client
     * @param address IPV4 address
     * @param port number
     */
    FTPClient ftpClient = new FTPClient();

    private String address;
    private int port;

    public easyFTPClient(String address, int port){
        try{
            //connects to EdgeServer
            ftpClient.setConnectTimeout(5000);
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

    public void sendFile(String fileName) throws IOException {
        File file = new File(fileName);
        sendFile(file);
    }


    /**
     * Sends file to FTP server. Very similar to .storeFile method
     * in FTPClient, but does not require the use of an input stream
     * for a method parameter.
     *
     * @param file
     * @throws IOException
     */

    public void sendFile(File file) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        boolean success = ftpClient.storeFile(file.getName(), inputStream);

        if (success){
            //Grabs file from server and ends stream
            System.out.println("\033[1;32m" + file.getName() + " transferred \033[0m");
            inputStream.close();

        } else{
            inputStream.close();
            System.out.println("File transfer failed!");
        }
    }//end of sendFile

    /**
     * Returns a file from the FTP server using FTPClient's retrieveFile method
     * This method is a fix for a NullPointerException thrown by this class when
     * using retrieveFile in Client.java
     *
     * @param fileName name of file you want
     * @return file from FTP server
     * @throws IOException
     */

    public File getFile(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
        boolean success = ftpClient.retrieveFile(fileName, outputStream);

        if (success){
            //Grabs file from server and ends stream
            System.out.println("\033[1;32m" + fileName + " transferred \033[0m");
            outputStream.flush();
            outputStream.close();
            return file;

        } else{
            outputStream.close();
            System.out.println("File transfer failed!");
            return null;
        }
    }//end of getFile

}//end of class
