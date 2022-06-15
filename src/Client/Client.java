/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 */

package Client;

import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class Client {

    //Initial vars
    easyFTPClient ftpClient;
    String size;
    Socket socket;
    String address;

    // constructor to put ip address, port, test, and iterations.
    public Client(String address, int ftpPort) throws IOException {
        this.address = address;
        socket = new Socket();
        InetSocketAddress edgeServerSocketAddress = new InetSocketAddress(this.address, 5001);
        socket.connect(edgeServerSocketAddress, 1000);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Setup before connection occurs
        ftpClient = new easyFTPClient(address, ftpPort);
        System.out.println("FTP:Client connected to edge server");
        File copiedFile = null;
        File file = null;

        int[] configData = receiveMessage();
        int test = configData[0];
        int size = configData[1];
        int iterations = configData[2];
        int ID = configData[3];

        //for size parameter -> used in input file names
        switch (size) {
            case 1 -> this.size = "Small";
            case 2 -> this.size = "Medium";
            case 3 -> this.size = "Large";
        }

        switch (test) {
            case 1: //OCR Test
                //sending image file
                for (int i = 0; i < iterations; i++) {
                    copiedFile = new File("ftpResources\\images\\woahman" + this.size + i + "C" + ID + ".png");
                    file = new File("ftpResources\\images\\woahman" + this.size + ".png");
                    copyAndSendFile(file, copiedFile);
                }
                break; //end of OCR test

            case 2: //Smith-Waterman Test
                String[] SWinputFiles = {"query", "database", "alphabet", "scoringmatrix"};

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
                    for (int j = 0; j < 2; j++) {
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

    private void copyAndSendFile(File file, File copiedFile) {
        try {
            FileUtils.copyFile(file, copiedFile);
            this.ftpClient.sendFile(copiedFile);
            copiedFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] receiveMessage() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
        int[] configData = new int[4];
        String[] configDataString = new String[4];
        boolean messageReceived = false;

        while(!messageReceived){
            configDataString = dataInputStream.readUTF().split(";");
            messageReceived = true;
        }

        for(int i = 0; i < configDataString.length; i++){
            configData[i] = Integer.parseInt(configDataString[i]);
        }

        return configData;
    }
}    // end of client
