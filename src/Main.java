/**
 * This is meant as a placeholder for a real ComponentMains.CloudMain.java file. Used to easily
 * run every part of the program from a single file.
 *
 * Author: Seth Wolfgang
 * Date 5/3/2022
 */

import Client.Client;
import Network.EdgeServer;

import java.io.IOException;

public class Main extends Thread implements Runnable  {

    static int test = 3;
    static int iterations = 10;
    static int size = 3;

    static int port = 5000;
    static int ftpPort = 2221;
    static String address = "127.0.0.1"; //likely should NOT change

    public static void main(String[] args) throws Exception {
        try {

            //Edge
            new Thread(() -> {
                try {
                    new EdgeServer(address , port, test, iterations);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            //CLIENT
            new Thread(() -> {
                try {
                    new Client(address, ftpPort,  test, iterations*10, size,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            //CLIENT
            new Thread(() -> {
                try {
                    new Client(address, ftpPort,  test, iterations*20, size,2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
           //new Thread(new Server(port)).start();


        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
}
