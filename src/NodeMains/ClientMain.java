/**
 * Author: Seth Wolfgang
 * Date: 5/13/2022
 *
 * ClientMain is to be run on client devices in the network. The purpose of
 * this file is to create an easy to use .jar file for client devices.
 *
 */

package NodeMains;

import Client.Client;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException, TesseractException {

        final int port = 5000;
        final int ftpPort = 2221; //todo make args?
        final String address = "127.0.0.1";

        Client client = new Client(address, port, ftpPort);

    }
}
