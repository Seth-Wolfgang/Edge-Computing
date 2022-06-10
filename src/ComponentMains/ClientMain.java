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

        if(args.length != 3) {
            System.out.println("Please use format for arguments\n" +
                               "[IPV4] [Test #] [# of Test Iterations] [size]\n" +
                               "Test #1 Tesseract OCR\n" +
                               "Test #2 Smith-Waterman\n" +
                               "Test #3 Logistic Regression\n" +
                               "sizes are 1-3, 1 is smallest");
        }
        //todo add size argument
        final int ftpPort = 2221;
        final String address = args[0];
        final int test = Integer.parseInt(args[1]);
        final int iterations = Integer.parseInt(args[2]);
        final int size = Integer.parseInt(args[3]);

        Client client = new Client(address, ftpPort, 1, 10, 1, 1);

    }
}
