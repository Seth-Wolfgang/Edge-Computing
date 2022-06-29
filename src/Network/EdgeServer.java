package Network;

import LogisticRegression.LogRegressionInitializer;
import OCR.OCRTest;
import OCR.Timer;
import SmithWaterman.SWinitialiser;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.*;
import java.util.ArrayList;
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
    ArrayList<String> outputString = new ArrayList<>();

    public EdgeServer(String address, int port, int test, int size, int iterations, int clients) throws IOException, InterruptedException {
        this.iterations = iterations;
        this.clients = clients;


        //initial networking
        Thread ftpServer = new easyFTPServer(address, 2221);
        ftpServer.start();
        socket = new Socket();
        clientSocket = new Socket();
        InetSocketAddress serverSocketAddress = new InetSocketAddress(address, port);
        server = new ServerSocket(5001);
        int clientNum = 0;

        //initial connection to server
        connectToServer(serverSocketAddress);

        //Program will not run until all clients connect
        while(clientNum < clients){
            clientSocket = server.accept();
            System.out.println("ES:Client accepted");
            clientNum++;
            Thread newClient = new ClientHandler(clientSocket, test, size, iterations, clientNum);
            newClient.start();
        }

        //determines which test is to be done
        switch (test) {
            case 1 -> outputString = OCRBench();
            case 2 -> outputString = SWBench();
            case 3 -> outputString = logRegressionBench();
        }

        individualTransmission(socket, outputString);
        compactTransmission(socket, outputString);
        cleanUp();
        closeConnection(socket);
    }

    /**
     * Performs the Optical Character Recognition Benchmark and sends the results
     * to the server.
     *
     * @throws IOException
     */

    public ArrayList<String> OCRBench() throws IOException {
        OCRTest ocr = new OCRTest("tessdata");
        ArrayList<String> processedText = new ArrayList<>();
        ArrayList<File> images;

        //waits for files to be sent to this device
        //and adds them all to an array list for processing
        timer.start();
        waitForFiles(1);
        images = grabFiles("woah.*", 1);
        timer.stopAndPrint("OCR Receive Files");

        timer.start();
        for (int i = 0; i < iterations; i++) {
            timer.newLap();
            processedText.add(ocr.readImage(images.get(i)));
        }
        timer.stopAndPrint("OCR");
        return processedText;
    }


    /**
     * Performs the Smith-Waterman algorithm to benchmark the system.
     * This sends results to the server.
     * @throws IOException
     * @throws InterruptedException
     */

    public ArrayList<String> SWBench() throws IOException, InterruptedException {
        //NOTE: run time is affected most by query
        String[] inputFilesName = {"query.*", "database.*", "alphabet.*", "scoringmatrix.*"};
        Timer timer = new Timer();
        ArrayList<ArrayList<File>> inputFiles = new ArrayList();
        ArrayList<String> SWOutput = new ArrayList<>();

        //waits for files to be sent to this device
        //and adds them all to an array list for processing
        timer.start();
        waitForFiles(4);
        for (int i = 0; i < 4; i++) {
            inputFiles.add(grabFiles(inputFilesName[i], 4));
        }
        timer.stopAndPrint("SW Receive Files");

        try {
            timer.start();
            for (int i = 0; i < iterations; i++) {
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
        for (int i = 0; i < iterations; i++) {
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
        timer.stopAndPrint("Individual Transmission Start");
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
        String outputString = manyOutput.get(0); //Semicolon seperated values of manyOutput
        Timer timer = new Timer();

        //allows for proper formatting
        manyOutput.remove(0);
        //Converts ArrayList to semicolon seperated values
        for (String output : manyOutput) {
            outputString = outputString + ";" + output;
        }

        //removes unnecessary new lines
        outputString = outputString.replace("\n", "").replace("\r", "");

        //times the transmission until it is done
        timer.start();
        try {
            dataOutput.writeUTF(outputString);
        } catch (UTFDataFormatException e) {
            throw new UTFDataFormatException("\033[1;30mOutput too big!\033[0m");
        }
        timer.stopAndPrint("Compact Transmission Start");
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
                if (files.size() < iterations * numOfInputs) {
                    files.add(file);
                } else {
                    break;
                }
            }
        }
        return files;
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
        while (dir.listFiles().length < (this.iterations * this.clients * numOfInputFiles)) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void connectToServer(InetSocketAddress serverSocketAddress) throws IOException {
        int counter = 0;
        while (!socket.isConnected()) {
            try {
                counter++;
                if (counter > 100) {
                    System.out.println("Failed to connect to server");
                    cleanUp();
                    System.exit(-1);
                }
                socket.connect(serverSocketAddress);

            } catch (SocketException e) {
                System.out.println("Connection failed! Trying again");
            }
        }
        System.out.println("Edge server connected to server");
    }
}//end of class


