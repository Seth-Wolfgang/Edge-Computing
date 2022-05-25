/**
 * Author: Seth Wolfgang
 * Date: 5/13/2022
 *
 * EdgeServerMain is to be run on edge server devices in the network. The purpose of
 * this file is to create an easy to use .jar file for edge server devices.
 *
 */

package ComponentMains;

import Network.EdgeServer;

import java.io.IOException;

public class EdgeServerMain {
    public static void main(String[] args) throws IOException, InterruptedException {

        if(args.length != 1) {
            System.out.println("Please enter an IP address in args");
        }

        final int ftpPort = 2221; //todo make arg?
        EdgeServer edgeServer = new EdgeServer(args[0], ftpPort, 1);

    }
}
