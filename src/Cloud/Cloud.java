package Cloud;

import LogisticRegression.LogRegressionInitializer;
import Network.ClientHandler;
import Network.easyFTPServer;
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
            clientSocket = new Socket();
            server = new ServerSocket(5001);
            int clientNum = 0;
            ArrayList<Thread> newClient = new ArrayList<>();

            //initial connection to server
            loadNextTrial(reader.nextLine());
            cleanUp();

            while (reader.hasNextLine()) {    //int test, int size, int iterations, int clients
                System.out.println("Waiting for clients... " + clientNum + " currently connected");
                cleanUp();
                //Handles clients connecting
                while (newClient.size() < this.clients) {
                    clientSocket = server.accept();
                    System.out.println("ES:Client accepted | " + newClient.size() + " connected");
                    newClient.add(new ClientHandler(clientSocket, test, size, iterations, newClient.size() + 1, clients));

                    if(!newClient.get(newClient.size() - 1).isAlive()){
                        newClient.get(newClient.size() - 1).start();
                    }
                }
                //sends message to clients so they know what to send back to the server
                for (Thread thread : newClient) {
                    ((ClientHandler) thread).updateConfigData(test, size, iterations, clients);
                    ((ClientHandler) thread).sendConfigData();
                }

                boolean allTrue = false;
                while(!allTrue){
                    int counter = 0;
                    for(Thread thread : newClient){
                        if(!((ClientHandler) thread).getStatus()) {
                            counter = 0;
                        }
                        else {
                            counter++;
                        }
                    }
                    if(counter == newClient.size()){
                        break;
                    }
                    //System.out.println(counter + " " + newClient.size());
                }

                //determines which test is to be done
                switch (test) {
                    case 1 -> outputString = OCRBench();
                    case 2 -> outputString = SWBench();
                    case 3 -> outputString = logRegressionBench();
                }

                individualTransmission(outputString);
                compactTransmission(outputString);

                //starts the next trial. Clients remain connected between trials
                loadNextTrial(reader.nextLine());
            }
            //Program will not run until all clients connect
            

        } catch (Exception e) {
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
        images = grabFiles("^woah", 1);
        timer.stopAndPrint("OCR Receive Files");

        timer.start();
        for (int i = 0; i < iterations * clients; i++) {
            timer.newLap();
            processedText.add(ocr.readImage(images.get(i)));
        }
        timer.stopAndPrint("OCR");
        filteredCleanUp("^woah");
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
        String[] inputFilesName = {"^query", "^database", "^alphabet", "^scoringmatrix"}; //regex
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
        filteredCleanUp("[(^query)(^database)(^alphabet)(^scoringmatrix)]");
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
        inputFiles.add(grabFiles("^Breast", 2));
        inputFiles.add(grabFiles("^test", 2));
        timer.stopAndPrint("LR Receive Files");

        timer.start();
        for (int i = 0; i < iterations * clients; i++) {
            timer.newLap();
            logRegressOutput.add(logRegress.LogRegressionInitializer(inputFiles.get(0).get(i), inputFiles.get(1).get(i)));
        }//end of i loop
        timer.stopAndPrint("Logistic Regression");
        filteredCleanUp("[(^test)(^Breast)]");
        return logRegressOutput;
    }

    /**
     * Sends data separately to the server.
     *
     * @param manyOutput
     * @throws IOException
     */

    public void individualTransmission(ArrayList<String> manyOutput) throws IOException {
        Timer timer = new Timer();

        timer.start();
        for (String out : manyOutput) {
            timer.newLap();
            System.out.println(out);
        }
        timer.stopAndPrint("Individual Transmission Start");
    }

    /**
     * prints all data at once
     *
     * @param manyOutput
     * @throws IOException
     */
    public void compactTransmission(ArrayList<String> manyOutput) throws IOException {
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
        System.out.println(outputString);
        timer.stopAndPrint("Cloud bulk print");
    }

    public ArrayList<File> grabFiles(String regex, int numOfInputs) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> files = new ArrayList<>();

        //looks through the directory for files that match the regex
        //and adds them to the returned file ArrayList
        for (File file : dir.listFiles()) {
            if (pattern.asPredicate().test(file.getName())) {
                if (files.size() < iterations * numOfInputs * clients) {
                    files.add(file);
                    //System.out.println("grabbing " + file.getAbsolutePath());
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

    public void loadNextTrial(String nextTrial) {
        String[] trialConfig = nextTrial.split(";");
        this.test = Integer.parseInt(trialConfig[0]);
        this.size = Integer.parseInt(trialConfig[1]);
        this.iterations = Integer.parseInt(trialConfig[2]);
        this.clients = Integer.parseInt(trialConfig[3]);

        if (this.test == 0)
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
        int counter = 0;
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (file.delete()) {
                    counter++;
                }
            }
            //System.out.println("Deleted " + counter + " files");
        } else {
            if (dir.createNewFile()) {
                System.out.println("Created new directory: filesToProcess");
            } else {
                System.out.println("Failed to create filesToProcess directory");
                System.exit(-1);
            }
        }
    }

    /**
     * Implemenets regex to remove files
     *
     * @param regex
     * @throws IOException
     */

    private void filteredCleanUp(String regex) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> files = new ArrayList<>();
        int counter = 0;

        //looks through the directory for files that match the regex
        //and adds them to the returned file ArrayList
        if (dir.exists()) {
            for (File file : dir.listFiles()) {
                if (pattern.asPredicate().test(file.getName())) {
                    //System.out.println("Deleting" + file.getAbsolutePath());
                    if (file.delete()) {
                        counter++;
                    }
                }
            }
        } else {
            if (dir.createNewFile()) {
                System.out.println("Created new directory: filesToProcess");
            } else {
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
        if (!dir.exists()) {
            new File(dir.getName()).mkdirs();
        }

        while (dir.listFiles().length != (this.iterations * this.clients * numOfInputFiles)) {
            try {
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
                    //cleanUp();
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



