/**
 * Author: Seth Wolfgang
 * Date: 4/26/2022
 *
 * For use in creating a standard constructor for FTP in this package
 */

package Client;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class easyFTP extends FTPClient{

    /**
     * Constructor for the easy FTP Client
     * @param address IPV4 address
     * @param port number
     */

    public easyFTP(String address, int port){
        FTPClient ftpClient = new FTPClient();

        try{
            ftpClient.connect(address, port);
            ftpClient.login("user", "");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        } catch (IOException e) {
            System.out.println("FTP Setup Failed");
            e.printStackTrace();
        }
    }
}
