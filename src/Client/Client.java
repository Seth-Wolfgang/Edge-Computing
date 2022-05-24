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

public class Client {

    //Initial vars
    ArrayList<Long> runTimes = new ArrayList<>();
    int test = 3; //test refers to the benchmark performed todo create a better way of handling this
    int counter = 0;
    final int iterations = 10; //controls how many times this class performs a bench


    // constructor to put ip address and port
    public Client(String address, int ftpPort) throws IOException, TesseractException {

        //Setup before connection occurs
        easyFTPClient ftpClient = new easyFTPClient(address, ftpPort);
        System.out.println("Client connected to edge server");
        File copiedFile = null;
        File file = null;

        switch (test) {
            case 1: //OCR Test
                try {
                    //sending image file
                    for (int i = 0; i < iterations; i++) {
                        File copiedImage = new File("ftpResources\\woahman" + i + ".png");
                        File image = new File("ftpResources\\woahman.png");
                        FileUtils.copyFile(image, copiedImage);
                        ftpClient.sendFile(copiedImage);
                        copiedImage.delete();
                    }
                    break; //end of OCR test
                } catch (Exception e) {
                    e.printStackTrace();
                }

            case 2: //Smith-Waterman Test
                String[] SWinputFiles = {"smallQuery",
                        "database",
                        "alphabet",
                        "scoringmatrix"};


                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < SWinputFiles.length; j++) {
                        copiedFile = new File("ftpResources\\" + SWinputFiles[j] + i + ".txt");
                        file = new File("ftpResources\\" + SWinputFiles[j] + ".txt");
                        FileUtils.copyFile(file, copiedFile);
                        ftpClient.sendFile(copiedFile);
                        copiedFile.delete();
                    }//end of j loop
                }//end of i loop

                //closeConnection(socket, out);
                break; //End of Smith-Waterman test

            case 3: //logistic regression
                String[] LGInputFiles = {"BreastCancer", "testData"};

                for (int i = 0; i < iterations; i++) {
                    for(int j = 0; j < 2; j++) {
                        copiedFile = new File("ftpResources\\" + LGInputFiles[j] + i + ".txt");
                        file = new File("ftpResources\\" + LGInputFiles[j] + ".txt");
                        FileUtils.copyFile(file, copiedFile);
                        ftpClient.sendFile(copiedFile);
                        copiedFile.delete();
                    }
                }
                break;
        }
    }

}    // end of client
