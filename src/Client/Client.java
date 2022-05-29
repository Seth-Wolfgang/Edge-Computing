/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 *
 */

package Client;

import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Client {

    //Initial vars
    ArrayList<Long> runTimes = new ArrayList<>();
    int counter = 0;
    easyFTPClient ftpClient;


    // constructor to put ip address, port, test, and iterations.
    public Client(String address, int ftpPort, int test, int iterations) throws IOException, TesseractException {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Setup before connection occurs
        ftpClient = new easyFTPClient(address, ftpPort);
        System.out.println("Client connected to edge server");
        File copiedFile = null;
        File file = null;


        switch (test) {
            case 1: //OCR Test
                    //sending image file
                    for (int i = 0; i < iterations; i++) {
                         copiedFile = new File("ftpResources\\woahman" + i + ".png");
                         file = new File("ftpResources\\woahman.png");
                         copyAndSendFile(file, copiedFile);
                    }
                break; //end of OCR test

            case 2: //Smith-Waterman Test
                String[] SWinputFiles = {"smallQuery", "database", "alphabet", "scoringmatrix"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < SWinputFiles.length; j++) {
                        copiedFile = new File("ftpResources\\" + SWinputFiles[j] + i + ".txt");
                        file = new File("ftpResources\\" + SWinputFiles[j] + ".txt");
                        copyAndSendFile(file, copiedFile);
                    }//end of j loop
                }//end of i loop
                break; //End of Smith-Waterman test

            case 3: //logistic regression
                String[] LGInputFiles = {"BreastCancer", "testData"};

                for (int i = 0; i < iterations; i++) {
                    for(int j = 0; j < 2; j++) {
                        copiedFile = new File("ftpResources\\" + LGInputFiles[j] + i + ".txt");
                        file = new File("ftpResources\\" + LGInputFiles[j] + ".txt");
                        copyAndSendFile(file, copiedFile);
                    }
                }
                break; //end of logistic regression
        }//end of switch
    }

    /**
     * Method to copy and send a file. Created to reduce unnecessary lines of code
     *
     * @param file
     * @param copiedFile
     */

    private void copyAndSendFile(File file, File copiedFile){
        try {
            FileUtils.copyFile(file, copiedFile);
            this.ftpClient.sendFile(copiedFile);
            copiedFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}    // end of client
