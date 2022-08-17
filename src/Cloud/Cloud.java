package Cloud;

import LogisticRegression.LogRegressionInitializer;
import Network.ClientHandler;
import Network.easyFTPServer;
import OCR.OCRTest;
import OCR.Timer;
import SmithWaterman.SWinitialiser;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Cloud {

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

    public Cloud(String deviceAddress) throws IOException, InterruptedException {
        try {
            //initial networking
            Thread ftpServer = new easyFTPServer(deviceAddress, 12221);
            ftpServer.start();
            socket = new Socket();
            clientSocket = new Socket();
            server = new ServerSocket(5001);
            int clientNum = 0;

            cleanUp();

            //initial connection to server
            loadNextTrial(reader.nextLine());

            while (reader.hasNextLine()) {    //int test, int size, int iterations, int clients
                System.out.println("Waiting for clients...");
                boolean configDataSent = false;

                //Handles clients connecting
                while (clientNum < this.clients) {
                    clientSocket = server.accept();
                    clientNum++;
                    System.out.println("ES:Client accepted | " + clientNum + " connected");
                    System.out.println("waiting");   // for debugging
                    Thread newClient = new ClientHandler(clientSocket, test, size, iterations, clientNum, clients);
                    newClient.start();
                    ((ClientHandler) newClient).sendConfigData();
                    System.out.println("flag " + configDataSent + " " + clientNum);
                }

                //determines which test is to be done
                switch (test) {
                    case 1 -> OCRBench();
                    case 2 -> SWBench();
                    case 3 -> logRegressionBench();
                }
                cleanUp(); //deletes files that may be left over, just in case

                //starts the next trial. Clients remain connected between trials
                loadNextTrial(reader.nextLine());
                clientNum = 0;
            }
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

    public void OCRBench() throws IOException {
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
        System.out.println(processedText);
    }


    /**
     * Performs the Smith-Waterman algorithm to benchmark the system.
     * This sends results to the server.
     *
     * @throws IOException
     * @throws InterruptedException
     */

    public void SWBench() throws IOException, InterruptedException {
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
        System.out.println(SWOutput);
    }

    /**
     * @throws IOException
     */

    public void logRegressionBench() throws IOException {
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
        System.out.println(logRegressOutput);
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
     * Removes input files from the directory filesToProcess
     */

    private void cleanUp() {
        for (File file : dir.listFiles()) {
            file.delete();
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
}//end of class


