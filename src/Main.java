/**
 * This is meant as a place holder for a real Main.java file. Used to easily
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

    public static void main(String[] args) {
        Thread eThread = null;
        Thread sThread = null;
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new EdgeServer(2221);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        new Client("127.0.0.1", 5000, 2221);
                    } catch (IOException | TesseractException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Server(5000)).start();
            sThread.start();


        } catch (Exception e) {
            //eThread.stop();
            //sThread.stop();
            e.printStackTrace();
        }
    }
}
