/**
 * This is meant as a placeholder for a real ComponentMains.CloudMain.java file. Used to easily
 * run every part of the program from a single file.
 *
 * Author: Seth Wolfgang
 * Date 5/3/2022
 */

import Client.Client;
import Cloud.Cloud;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main extends Thread implements Runnable {

    static int clients = 1;
    static int port = 5000;
    static int ftpPort = 12221;
    static String address = "127.0.0.1"; //likely should NOT change
    static String deviceAddress = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        //CLOUD
        try {
            new Thread(() -> {
                try {
                    new Cloud(deviceAddress);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();


            //SERVER
//            new Thread(() -> {
//                try {
//                    new Server(port);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }).start();
//
//            //EDGE
//            new Thread(() -> {
//                try {
//                    new EdgeServer(deviceAddress, address);
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }).start();

            //CLIENT
                TimeUnit.SECONDS.sleep(2);
                for(int j = 0; j < clients; j++) {
                    new Thread(() -> {
                        try {
                            new Client(address);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }


        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception();
        }
    }
}
