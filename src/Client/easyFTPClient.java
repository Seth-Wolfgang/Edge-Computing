/**
 * Author: Seth Wolfgang
 * Date: 4/26/2022
 * <p>
 * For use in creating a standard constructor for FTP in this package
 */

package Client;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.ConnectException;

public class easyFTPClient extends FTPClient {

    /**
     * Constructor for the easy FTP Client
     * @param address IPV4 address
     * @param port number
     */
    FTPClient ftpClient = new FTPClient();

    private String address;
    private int port;

    public easyFTPClient(String address, int port) {
        try {
            //connects to EdgeServer
            ftpClient.setConnectTimeout(5000);

            //ensures connection to FTP server
            while(!ftpClient.isConnected()){
                try{
                    ftpClient.connect(address, port);
                } catch (ConnectException e){
                    //nothing needs to be done here
                }
            }

            ftpClient.login("user", "");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            System.out.println("FTP Setup Failed");
            e.printStackTrace();
        }
    }


    /**
     * simple disconnection method
     *
     * @throws IOException
     */

    public void closeConnection() throws IOException {
        ftpClient.disconnect();
    }


    /**
     * Sends file to FTP server. Very similar to .storeFile method
     * in FTPClient, but does not require the use of an input stream
     * for a method parameter.
     *
     * @param file
     * @throws IOException
     */

    public void sendFile(File file) throws IOException, InterruptedException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        boolean success = ftpClient.storeFile(file.getName(), inputStream);

        if (success) {
            //Grabs file from server and ends stream
            System.out.println("\033[1;32m" + file.getName() + " transferred \033[0m");
            inputStream.close();

        } else {
            inputStream.close();
            System.out.println("File transfer failed!");
        }
    }//end of sendFile

    /**
     * Overloaded method for sending files by using the path instead of a file object
     *
     * @param fileName file path
     * @throws IOException
     * @throws InterruptedException
     */

    public void sendFile(String fileName) throws IOException, InterruptedException {
        File file = new File(fileName);
        sendFile(file);
    }


}//end of class
