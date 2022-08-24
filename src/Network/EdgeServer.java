package Network;

import LogisticRegression.LogRegressionInitializer;
import OCR.OCRTest;
import OCR.Timer;
import SmithWaterman.SWinitialiser;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class EdgeServer {

    Socket socket;
    Socket clientSocket;
    ServerSocket server;
    File dir = new File("filesToProcess");
    Timer timer = new Timer();
    int iterations;
    int clients;
    int test;
    int size;
    ArrayList<String> outputString = new ArrayList<>();
    File tests = new File("trials.txt");
    Scanner reader = new Scanner(tests);

    public EdgeServer(String deviceAddress, String address) throws IOException, InterruptedException {
       try {
           //initial networking
           Thread ftpServer = new easyFTPServer(deviceAddress, 12221);
           ftpServer.start();
           socket = new Socket();
           clientSocket = new Socket();
           InetSocketAddress serverSocketAddress = new InetSocketAddress(address, 5000);
           server = new ServerSocket(5001);
           int clientNum = 0;
           ArrayList<Thread> newClient = new ArrayList<>();
           cleanUp();

           //initial connection to server
           connectToServer(serverSocketAddress);
           loadNextTrial(reader.nextLine());

           while (reader.hasNextLine()) {    //int test, int size, int iterations, int clients
               System.out.println("Waiting for clients... " + clientNum + " currently connected");
                cleanUp();
               //Handles clients connecting
               while (clientNum < this.clients) {
                   clientSocket = server.accept();
                   clientNum++;
                   System.out.println("ES:Client accepted | " + clientNum + " connected");
                   newClient.add(new ClientHandler(clientSocket, test, size, iterations, clientNum, clients));
                   newClient.get(clientNum - 1).start();
                   //System.out.println("flag " + configDataSent + " " + clientNum);
               }
                for(Thread thread : newClient) {
                    ((ClientHandler) thread).updateConfigData(test, size, iterations);
                    ((ClientHandler) thread).sendConfigData();

                }
               //determines which test is to be done
               switch (test) {
                   case 1 -> outputString = OCRBench();
                   case 2 -> outputString = SWBench();
                   case 3 -> outputString = logRegressionBench();
               }

               individualTransmission(socket, outputString);
               timer.stopAndPrint("Individual Transmission Start");
               compactTransmission(socket, outputString);
               timer.stopAndPrint("Compact Transmission Start");
               cleanUp(); //deletes files that may be left over

               //starts the next trial. Clients remain connected between trials
               loadNextTrial(reader.nextLine());
           }
           //Program will not run until all clients connect

           closeConnection(socket);
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    /**
     * Performs the Optical Character Recognition Benchmark and sends the results
     * to the server.
     *
     * @throws IOException
     */

    public ArrayList<String> OCRBench() throws IOException {
        OCRTest ocr = new OCRTest("~/Desktop/tessdata");
        ArrayList<String> processedText = new ArrayList<>();
        ArrayList<File> images;

        //waits for files to be sent to this device
        //and adds them all to an array list for processing
        timer.start();
        waitForFiles(1);
        images = grabFiles("woah.*", 1);
        timer.stopAndPrint("OCR Receive Files");

        timer.start();
        for (int i = 0; i < iterations * clients; i++) {
            timer.newLap();
            processedText.add(ocr.readImage(images.get(i)));
        }
        timer.stopAndPrint("OCR");
        return processedText;
    }


    /**
     * Performs the Smith-Waterman algorithm to benchmark the system.
     * This sends results to the server.
     *
     * @throws IOException
     * @throws InterruptedException
     */

    public ArrayList<String> SWBench() throws IOException, InterruptedException {
        //NOTE: run time is affected most by query
        String[] inputFilesName = {"query.*", "database.*", "alphabet.*", "scoringmatrix.*"}; //regex
        Timer timer = new Timer();
        ArrayList<ArrayList<File>> inputFiles = new ArrayList<>();
        ArrayList<String> SWOutput = new ArrayList<>();

        //waits for files to be sent to this device
        //and adds them all to an array list for processing
        timer.start();
        waitForFiles(4);
        for (int i = 0; i < 4; i++) {
            inputFiles.add(grabFiles(inputFilesName[i], 4));
        }
        timer.stopAndPrint("SW Receive Files");

        //This runs Smith Waterman and records the time for each iteration
        try {
            timer.start();
            for (int i = 0; i < iterations * clients; i++) {
                timer.newLap();
                SWOutput.add(new SWinitialiser().run(inputFiles.get(0).get(i).getAbsolutePath(),
                        inputFiles.get(1).get(i).getAbsolutePath(),
                        inputFiles.get(2).get(i).getAbsolutePath(),
                        inputFiles.get(3).get(i).getAbsolutePath(), 1, 1));
            }//end of i loop
            timer.stopAndPrint("SW run");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return SWOutput;
    }

    /**
     * @return ArrayList<String>
     * @throws IOException
     */

    public ArrayList<String> logRegressionBench() throws IOException {
        LogRegressionInitializer logRegress = new LogRegressionInitializer();
        ArrayList<String> logRegressOutput = new ArrayList<>();
        ArrayList<ArrayList<File>> inputFiles = new ArrayList<>(2);

        //waits for files to be sent to this device
        //and adds them all to an array list for processing
        timer.start();
        waitForFiles(2);
        inputFiles.add(grabFiles("B.*", 2));
        inputFiles.add(grabFiles("t.*", 2));
        timer.stopAndPrint("LR Receive Files");

        timer.start();
        for (int i = 0; i < iterations * clients; i++) {
            timer.newLap();
            logRegressOutput.add(logRegress.LogRegressionInitializer(inputFiles.get(0).get(i), inputFiles.get(1).get(i)));
        }//end of i loop
        timer.stopAndPrint("Logistic Regression");
        return logRegressOutput;
    }

    /**
     * Sends data separately to the server.
     *
     * @param socket
     * @param manyOutput
     * @throws IOException
     */

    public void individualTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        Timer timer = new Timer();

        timer.start();
        for (String out : manyOutput) {
            timer.newLap();
            dataOutput.writeUTF(out);
        }
    }

    /**
     * This sends all data at the same time. Each part is seperated by semicolons.
     *
     * @param socket
     * @param manyOutput
     * @throws IOException
     */

    public void compactTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        ArrayList<String> bigDataOutput = new ArrayList<>();
        StringBuilder outputString = new StringBuilder(manyOutput.get(0)); //Semicolon seperated values of manyOutput
        Timer timer = new Timer();

        //allows for proper formatting
        manyOutput.remove(0);
        //Converts ArrayList to semicolon seperated values
        for (String output : manyOutput) {
            outputString.append(";").append(output);
        }

        //removes unnecessary new lines
        outputString = new StringBuilder(outputString.toString().replace("\n", "").replace("\r", ""));

        //times the transmission until it is done
        timer.start();
        try {
            if (outputString.length() > 65535) {
                int counter = 1;

                while (outputString.length() > 65535) {
                    bigDataOutput.add(outputString.substring(0, 65535));
                    outputString = new StringBuilder(outputString.substring(65535));

                    //loop will not run after string size is less than writeUTF byte limit
                    //this grabs the last of the output
                    if (outputString.length() < 65535) {
                        bigDataOutput.add(outputString.toString());
                        counter++;
                        break;
                    }

                    counter++;
                }
                System.out.println("Output is too big for compact transmission." +
                        "Splitting output into " + counter + " large parts.");
                individualTransmission(socket, bigDataOutput);

            } else {
                dataOutput.writeUTF(outputString.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Grabs the first file to be processed. Allows easy use of regex
     *
     * @param regex
     * @return ArrayList
     * @throws IOException
     */

    public ArrayList<File> grabFiles(String regex, int numOfInputs) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> files = new ArrayList<>();

        //looks through the directory for files that match the regex
        //and adds them to the returned file ArrayList
        for (File file : dir.listFiles()) {
            if (pattern.asPredicate().test(file.getName())) {
                if (files.size() < iterations * numOfInputs * clients) {
                    files.add(file);
                } else {
                    break;
                }
            }
        }
        return files;
    }

    /**
     * Loads next trial into testing parameters
     *
     * @param nextTrial
     */

    public void loadNextTrial(String nextTrial){
        String[] trialConfig = nextTrial.split(";");
        this.test = Integer.parseInt(trialConfig[0]);
        this.size = Integer.parseInt(trialConfig[1]);
        this.iterations = Integer.parseInt(trialConfig[2]);
        this.clients = Integer.parseInt(trialConfig[3]);

        if(this.test == 0)
            System.out.println("Testing Complete!");
        else
            System.out.println("New Trial:\ntest = " + test + "\nsize = " + size + "\nIterations = " + iterations + "\nClients = " + clients);
    }

    /**
     * Sends the message to close connection with server and then closes the socket
     *
     * @param socket
     */

    public void closeConnection(Socket socket) {
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF("over");
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes temporary files
     */

    private void cleanUp() throws IOException {
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
        }
        else {
            if(dir.createNewFile()) {
                System.out.println("Created new directory: filesToProcess");
            }
            else {
                System.out.println("Failed to create filesToProcess directory");
                System.exit(-1);
            }
        }

    }


    /**
     * Puts thread to sleep for .1 seconds if there are not
     * iterations * numOfInputFiles in the directory for
     * files to be processed.
     *
     * @param numOfInputFiles
     */

    private void waitForFiles(int numOfInputFiles) {
        if(!dir.exists()){
            new File(dir.getName()).mkdirs();
        }

        while (dir.listFiles().length < (this.iterations * this.clients * numOfInputFiles)) {
            try {
                //System.out.println("waiting for files");    //For debugging
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allows for redundancy in connecting to server.
     *
     * @param serverSocketAddress
     * @throws IOException
     */

    private void connectToServer(InetSocketAddress serverSocketAddress) throws IOException {
        int counter = 0;
        while (!socket.isConnected()) {
            try {
                TimeUnit.SECONDS.sleep(2);
                counter++;
                if (counter > 3) {
                    System.out.println("Failed to connect to server");
                    cleanUp();
                    System.exit(-1);
                }
                socket.connect(serverSocketAddress);

            } catch (SocketException | InterruptedException e) {
                System.out.println("Connection failed! Trying again");
            }
        }
        System.out.println("Edge server connected to server");
    }
}//end of class


