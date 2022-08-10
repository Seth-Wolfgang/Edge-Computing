package ComponentMains;

import Client.Client;
import Network.EdgeServer;

import java.io.IOException;

public class ProductionMain {

    public static void main(String[] args) throws IOException {
        String type = args[0];
        String IP = args[1];

        //prints out instructions for client if typed wrong
        if(args.length != 3 && type.equals("-c")) {
            System.out.println("""
                    Please use format for arguments:
                    -c [Edge IPV4] [FTP Port]
                    """);
        }
        //instructions for edge server
        else if (args.length != 7 && type.equals("-e")){
            System.out.println("""
                    Please use format for arguments:
                    -e [Server IPV4] [Device IPV4]
                    """);
        }
        //Prints out help when run
        else if(type.equals("help") || type.equals("-h")){
            System.out.println("""
                    Type:
                    \t-c\t Client device
                    \t-e\t Edge server device
                    Please use format for client arguments:
                    \t-c [IPV4] [FTP Port]
                    Edge server devices needs different arguments:
                    \t-e [Server IPV4] [Device IPV4]""");
        }

        switch (type){
            case "-c":
                int ftpPort = Integer.parseInt(args[2]);
                Client client = new Client(IP, ftpPort);
            case "-e":
                String deviceIP = args[2];
                try {
                    EdgeServer edgeServer = new EdgeServer(deviceIP, IP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
}
