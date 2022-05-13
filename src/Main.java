/**
 * This is meant as a placeholder for a real NodeMains.CloudMain.java file. Used to easily
 * run every part of the program from a single file.
 *
 * Author: Seth Wolfgang
 * Date 5/3/2022
 */

import Client.Client;
import Network.EdgeServer;
import Network.Server;
import net.sourceforge.tess4j.TesseractException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main extends Thread implements Runnable  {

    int numOfPi;
    int numOfEdgeServers;

    static int port = 5000;
    static int ftpPort = 2221;
    static String address = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        try {
            new Thread(new Runnable() { //FTP
                @Override
                public void run() {
                    new EdgeServer(ftpPort);
                }
            }).start();
            new Thread(new Runnable() { //CLIENT
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        new Client(address, port, ftpPort);
                    } catch (IOException | TesseractException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() { //CLIENT
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        new Client(address, port, ftpPort);
                    } catch (IOException | TesseractException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Server(port)).start();


        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
}
