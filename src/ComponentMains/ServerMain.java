/**
 * Author: Seth Wolfgang
 * Date: 5/13/2022
 *
 * ServerMain is to be run on server devices in the network. The purpose of
 * this file is to create an easy to use .jar file for server devices.
 */

package ComponentMains;

import Network.Server;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        final int port = 5000;
        Server server = new Server(port);

    }
}
