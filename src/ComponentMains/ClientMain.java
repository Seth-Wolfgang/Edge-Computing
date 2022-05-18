/**
 * Author: Seth Wolfgang
 * Date: 5/13/2022
 *
 * ClientMain is to be run on client devices in the network. The purpose of
 * this file is to create an easy to use .jar file for client devices.
 *
 */

package ComponentMains;

import Client.Client;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException, TesseractException {

        if(args.length != 1) {
            System.out.println("Please enter an IP address in args");
        }

        final int ftpPort = 2221; //todo make args?
        final String address = args[0];

        Client client = new Client(address, ftpPort);

    }
}
