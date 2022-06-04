/**
 * This is meant as a placeholder for a real ComponentMains.CloudMain.java file. Used to easily
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

public class Main extends Thread implements Runnable  {

    int numOfPi;
    int numOfEdgeServers;
    static int test = 3;
    static int iterations = 20;

    static int port = 5000;
    static int ftpPort = 2221;
    static String address = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        try {

            new Thread(new Runnable() { //Edge
                @Override
                public void run() {
                    try {
                        new EdgeServer(address , port, test, iterations);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() { //CLIENT
                @Override
                public void run() {
                    try {
                        new Client(address, ftpPort,  test, iterations, 1);
                    } catch (IOException | TesseractException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
           new Thread(new Runnable() { //CLIENT
               @Override
               public void run() {
                   try {
                       new Client(address, ftpPort,  test, iterations, 2);
                   } catch (IOException | TesseractException e) {
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
