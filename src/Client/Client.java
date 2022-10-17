/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 */

package Client;

import OCR.Timer;
import org.apache.commons.io.FileUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    Timer timer = new Timer();

    // constructor to put ip address, port, test, and iterations.
    public Client(String address) throws IOException {
        socket = new Socket();
        socket.setKeepAlive(true);
        InetSocketAddress edgeServerSocketAddress = new InetSocketAddress(address, 5001);

        //connects to edge server
        connectToEdgeServer(edgeServerSocketAddress);

        //Setup before connection occurs
        ftpClient = new easyFTPClient(address, 12221);
        //System.out.println("FTP:Client connected to edge server");


        /*
        Each case duplicates a specific file for each iteration in the config
         */

        while(true){
            try{
                fileSendHelper();
            } catch (Exception e){
                System.out.println("Connection with edge server forcibly ended");
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public void fileSendHelper() throws IOException, InterruptedException {
        File copiedFile = null;
        File file = null;
        File dir = new File("toSend");

        //sets the parameters for what the client does
        int[] configData = receiveParameters();
        int test = configData[0];
        int size = configData[1];
        int iterations = configData[2];
        int ID = configData[3];
        int activeClients = configData[4];
        ftpClient.refreshConnection();

        /*
        Test to see if this needs to send data to edge server
        If more than the necessary amount of clients connect to the server,
        this will return from this method
        */

        if(activeClients < ID) {
            System.out.println("\033[1;30mClient not needed on this trial \033[0m");
            return;
        }

        //for size parameter -> used in input file names
        switch (size) {
            case 1 -> this.size = "Small";
            case 2 -> this.size = "Medium";
            case 3 -> this.size = "Large";
        }

        timer.start();
        switch (test) {
            case 1: //OCR Test
                //sending image file
                for (int i = 0; i < iterations; i++) {
                    copiedFile = new File("toSend" + File.separator + "image" +this.size + i + "C" + ID + ".jpg");
                    file = new File("ftpResources" + File.separator + "images" + File.separator + "image" + this.size + ".jpg");
                    copyAndSendFile(file, copiedFile);
                }
                break; //end of OCR test

            case 2: //Smith-Waterman Test
                String[] SWinputFiles = {"query", "database", "alphabet", "scoringmatrix"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < SWinputFiles.length; j++) {
                        copiedFile = new File("toSend" + File.separator + SWinputFiles[j] + this.size + i + "C" + ID + ".txt");
                        file = new File("ftpResources" + File.separator + "SW" + File.separator + "" + SWinputFiles[j] + this.size + ".txt");
                        copyAndSendFile(file, copiedFile);
                    }//end of j loop
                }//end of i loop
                break; //End of Smith-Waterman test

            case 3: //logistic regression
                String[] LGInputFiles = {"BreastCancer", "testData"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < 2; j++) {
                        copiedFile = new File("toSend"  + File.separator + LGInputFiles[j] + this.size + i + "C" + ID + ".txt");
                        file = new File("ftpResources" + File.separator + "LogRegression" + File.separator + "" + LGInputFiles[j] + this.size + ".txt");
                        copyAndSendFile(file, copiedFile);
                    }
                }
                break; //end of logistic regression


        }//end of switch
        while(dir.listFiles().length != 0){
            TimeUnit.MILLISECONDS.sleep(10);
        }
        sendCompletionStatus();
        timer.stopAndPrint("Sending input files", test, size, iterations, activeClients);

    }

    /**
     *
     * @throws IOException
     */
    public void sendCompletionStatus() throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(this.socket.getOutputStream());
        dataOutput.writeBoolean(true);
        dataOutput.flush();
        System.out.println("Sent confirmation of all files being sent");
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
            //System.out.println("Sending " + copiedFile.getAbsolutePath()); //debugging
            this.ftpClient.sendFile(copiedFile);
            copiedFile.delete();
            timer.newLap();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method allows the client to receive the parameters of what
     * is sent and how many times to send a file to the edge server.
     * Data comes in a format of
     * test + ";" + size + ";" + iterations + ";" + clientNum
     *
     * @return
     * @throws IOException
     */

    private int[] receiveParameters() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
        int[] configData = new int[5];
        String[] configDataString = new String[5];
        boolean messageReceived = false;

        //formats data from edge server
        while(!messageReceived){
            configDataString = dataInputStream.readUTF().split(";");
            messageReceived = true;
        }
        //System.out.println(Arrays.toString(configDataString));

        if(configDataString[0].equals("0")){
            System.out.println("Client parameters set to stop.");
            this.socket.close();
            System.exit(1);
        }

        //parses the data sent from the edge server
        for(int i = 0; i < configDataString.length; i++){
            configData[i] = Integer.parseInt(configDataString[i]);
        }
        System.out.println("New parameters received!");
        return configData;
    }


    /**
     * this method allows for redundancy in connecting to the edge server.
     * The client may fail to connect an attempt to connect at startup.
     *
     * @param edgeServerSocketAddress
     */
    private void connectToEdgeServer(InetSocketAddress edgeServerSocketAddress){
        while (!socket.isConnected()) {
            try {
                socket = new Socket();
                socket.connect(edgeServerSocketAddress); // try the connection
            } catch (IOException e) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}    // end of client
