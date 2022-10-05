/**
 * Author: Seth Wolfgang
 * Date: 4/19/2022
 * This program serves as the client/gatherer of the network.
 * It sends an image from the `middle` layer
 */

package Client;

import LogisticRegression.LogRegressionInitializer;
import OCR.OCRTest;
import OCR.Timer;
import SmithWaterman.SWinitialiser;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ClientCompute {

    //Initial vars
    File dir = new File("filesToProcess");
    int size;
    Socket socket;
    Timer timer = new Timer();
    int iterations;
    int test;
    int clients;
    ArrayList<String> outputString = new ArrayList<>();

    File tests = new File("trials.txt");
    Scanner reader = new Scanner(tests);


    // constructor to put ip address, port, test, and iterations.
    public ClientCompute(String address) throws IOException {
        socket = new Socket();
        loadNextTrial(reader.nextLine());
        cleanUp();
        InetSocketAddress serverAddress = new InetSocketAddress(address, 12221);
        connectToServer(serverAddress);


        while (reader.hasNextLine()) {
            try {
                loadNextTrial(reader.nextLine());
                fileDuplicator(this.test, this.size, this.iterations);

                switch (test) {
                    case 1 -> outputString = OCRBench();
                    case 2 -> outputString = SWBench();
                    case 3 -> outputString = logRegressionBench();
                }

                timer.start();
                compactTransmission(socket, outputString);
                timer.stopAndPrint("Client Computing", test, size, iterations, 0);

            } catch (Exception e) {
                System.out.println("Connection with edge server forcibly ended");
                e.printStackTrace();
                System.exit(1);
            }
        }
    closeConnection(socket);
    }

    public void fileDuplicator(int test, int size, int iterations) throws IOException {
        File copiedFile = null;
        File file = null;
        String inputSize = "";


        //for size parameter -> used in input file names
        switch (size) {
            case 1 -> inputSize = "Small";
            case 2 -> inputSize = "Medium";
            case 3 -> inputSize = "Large";
        }

        int mult = 0;
        switch (test) {
            case 1 -> mult = 1;
            case 2 -> mult = 4;
            case 3 -> mult = 2;
            default ->
                    throw new IllegalArgumentException("Client Compute: Mult requires a value");
        }

        timer.start();
        switch (test) {
            case 1:
                for (int i = 0; i < iterations; i++) {
                    copiedFile = new File("ftpResources" + File.separator + "images" + File.separator + "woahman" + inputSize + i + ".txt");
                    file = new File("ftpResources" + File.separator + "images" + File.separator + "woahman" + inputSize + ".png");
                }
                break;

            case 2:
                String[] SWinputFiles = {"query", "database", "alphabet", "scoringmatrix"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < SWinputFiles.length; j++) {
                        copiedFile = new File("ftpResources" + File.separator + "SW" + File.separator + "" + SWinputFiles[j] + inputSize + i +".txt");
                        file = new File("ftpResources" + File.separator + "SW" + File.separator + "" + SWinputFiles[j] + inputSize + ".txt");
                    }
                }
                break;

            case 3:
                String[] LGInputFiles = {"BreastCancer", "testData"};

                for (int i = 0; i < iterations; i++) {
                    for (int j = 0; j < 2; j++) {
                        copiedFile = new File("ftpResources" + File.separator + "LogRegression" + File.separator + "" + LGInputFiles[j] + inputSize + i + ".txt");
                        file = new File("ftpResources" + File.separator + "LogRegression" + File.separator + "" + LGInputFiles[j] + inputSize + ".txt");
                    }
                }
                break;
        }//end of switch
        timer.stopAndPrint("Duplicating Files", test, size, iterations, 1);
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
        images = grabFiles("^woah", 1);
        timer.stopAndPrint("OCR Receive Files", test, size, iterations, 0);

        timer.start();
        for (int i = 0; i < iterations; i++) {
            timer.newLap();
            processedText.add(ocr.readImage(images.get(i)));
        }
        timer.stopAndPrint("OCR", test, size, iterations, 0);
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
        ArrayList<ArrayList<File>> inputFiles = new ArrayList<>();
        ArrayList<String> SWOutput = new ArrayList<>();

        //and adds them all to an array list for processing
        for (int i = 0; i < 4; i++) {
            inputFiles.add(grabFiles(inputFilesName[i], 4));
        }
        timer.stopAndPrint("SW Receive Files", test, size, iterations, 0);

        //This runs Smith Waterman and records the time for each iteration
        try {
            timer.start();
            for (int i = 0; i < iterations; i++) {
                timer.newLap();
                SWOutput.add(new SWinitialiser().run(inputFiles.get(0).get(i).getAbsolutePath(),
                        inputFiles.get(1).get(i).getAbsolutePath(),
                        inputFiles.get(2).get(i).getAbsolutePath(),
                        inputFiles.get(3).get(i).getAbsolutePath(), 1, 1));

            }//end of i loop
            timer.stopAndPrint("SW run", test, size, iterations, 0);

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

        //and adds them all to an array list for processing
        inputFiles.add(grabFiles("^Breast", 2));
        inputFiles.add(grabFiles("^test", 2));
        timer.stopAndPrint("LR Receive Files", test, size, iterations, 0);

        timer.start();
        for (int i = 0; i < iterations; i++) {
            timer.newLap();
            logRegressOutput.add(logRegress.LogRegressionInitializer(inputFiles.get(0).get(i), inputFiles.get(1).get(i)));
        }

        timer.stopAndPrint("Logistic Regression", test, size, iterations, 0);
        filteredCleanUp("[(^test)(^Breast)]");
        return logRegressOutput;
    }

    //utility for if compactTransmission() goes above 2^16 bytes
    public void individualTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

        for (String out : manyOutput) {
            timer.newLap();
            dataOutput.writeUTF(out);
        }
        timer.stopAndPrint("Individual Transmission Start", test, size, iterations, 0);
    }


    public void compactTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        ArrayList<String> bigDataOutput = new ArrayList<>();
        StringBuilder outputString = new StringBuilder(manyOutput.get(0)); //Semicolon seperated values of manyOutput
        //allows for proper formatting
        manyOutput.remove(0);
        //Converts ArrayList to semicolon seperated values
        for (String output : manyOutput) {
            outputString.append(";").append(output);
        }

        //removes unnecessary new lines
        outputString = new StringBuilder(outputString.toString().replace("\n", "").replace("\r", ""));

        //times the transmission until it is done
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


    public void loadNextTrial(String nextTrial) {
        String[] trialConfig = nextTrial.split(";");
        this.test = Integer.parseInt(trialConfig[0]);
        this.size = Integer.parseInt(trialConfig[1]);
        this.iterations = Integer.parseInt(trialConfig[2]);

        if (this.test == 0)
            System.out.println("Testing Complete!");
        else
            System.out.println("New Trial:\ntest = " + test + "\nsize = " + size + "\nIterations = " + iterations + "\nClients = " + clients);
    }

    public ArrayList<File> grabFiles(String regex, int numOfInputs) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> files = new ArrayList<>();

        //looks through the directory for files that match the regex
        //and adds them to the returned file ArrayList
        for (File file : dir.listFiles()) {
            if (pattern.asPredicate().test(file.getName())) {
                if (files.size() < iterations * numOfInputs) {
                    files.add(file);
                    //System.out.println("grabbing " + file.getAbsolutePath());
                } else {
                    break;
                }
            }
        }
        return files;
    }

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
     * this method allows for redundancy in connecting to the edge server.
     * The client may fail to connect an attempt to connect at startup.
     *
     * @param serverSocketAddress
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
}    // end of client
