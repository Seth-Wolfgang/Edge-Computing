/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 *
 */

package Client;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Client {

    //Initial vars
    ArrayList<Long> runTimes = new ArrayList<>();
    easyFTPClient ftpClient;
    String size;

    // constructor to put ip address, port, test, and iterations.
    public Client(String address, int ftpPort, int test, int iterations, int size, int ID) throws IOException {
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

        //for size parameter -> used in input file names
        switch (size){
            case 1 -> this.size = "Small";
            case 2 -> this.size = "Medium";
            case 3 -> this.size = "Large";
        }

        switch (test) {
            case 1: //OCR Test
                    //sending image file
                    for (int i = 0; i < iterations; i++) {
                         copiedFile = new File("ftpResources\\images\\woahman" + this.size + i + "C" + ID +".png");
                         file = new File("ftpResources\\images\\woahman" + this.size + ".png");
                         copyAndSendFile(file, copiedFile);
                    }
                break; //end of OCR test

            case 2: //Smith-Waterman Test
                String[] SWinputFiles = {"query", "databaseSmall", "alphabet", "scoringmatrix"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < SWinputFiles.length; j++) {
                        copiedFile = new File("ftpResources\\SW\\" + SWinputFiles[j] + this.size + i + "C" + ID + ".txt");
                        file = new File("ftpResources\\SW\\" + SWinputFiles[j] + this.size + ".txt");
                        copyAndSendFile(file, copiedFile);
                    }//end of j loop
                }//end of i loop
                break; //End of Smith-Waterman test

            case 3: //logistic regression
                String[] LGInputFiles = {"BreastCancer", "testData"};

                for (int i = 0; i < iterations; i++) {
                    for(int j = 0; j < 2; j++) {
                        copiedFile = new File("ftpResources\\LogRegression\\" + LGInputFiles[j] + this.size + i + "C" + ID + ".txt");
                        file = new File("ftpResources\\LogRegression\\" + LGInputFiles[j] + this.size + ".txt");
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
