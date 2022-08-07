package ComponentMains;

import Client.Client;
import Network.EdgeServer;

import java.io.IOException;

public class ProductionMain {

    public static void main(String[] args) throws IOException {
        String type = args[0];
        String IP = args[1];
        int ftpPort = Integer.parseInt(args[2]);

        if(args.length != 3 && type.equals("-c")) {
            System.out.println("""
                    Please use format for arguments:
                    -c [Edge IPV4] [FTP Port]
                    """);
        }
        else if (args.length != 7 && type.equals("-e")){
            System.out.println("""
                    Please use format for arguments:
                    -e [Server IPV4] [Device IPV4]
                    """);
        } else if(type.equals("help") || type.equals("-h")){
            System.out.println("""
                    Please use format for arguments:
                    [Type] [IPV4] [FTP Port]
                    Type:
                    \t-c\t Client device
                    \t-e\t Edge server device
                    Note:
                    Edge server devices need more arguments:
                    \t-e [Server IPV4] [Device IPV4]""");
        }

        switch (type){
            case "-c":
                Client client = new Client(IP, ftpPort);
            case "-e":
                String deviceIP = args[3];
                try {
                    EdgeServer edgeServer = new EdgeServer(deviceIP, IP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
    }
}
