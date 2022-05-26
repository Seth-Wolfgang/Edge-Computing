package Network;

import LogisticRegression.LogRegressionInitializer;
import OCR.OCRTest;
import OCR.Timer;
import SmithWaterman.SWinitialiser;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EdgeServer {

    File dir = new File("filesToProcess");
    Timer timer = new Timer();
    int test = 3;
    ArrayList<String> outputString = new ArrayList<>();

    public EdgeServer(String address, int port, int test, int iterations) throws IOException, InterruptedException {

        //initial networking
        Thread ftpServer = new easyFTPServer(address, 2221);
        ftpServer.start();
        Socket socket = new Socket();
        InetSocketAddress sa = new InetSocketAddress(address, port);

        //initial connection to server
        socket.connect(sa, 10000);
        System.out.println("Edge server connected to server");

        //determines which test is to be done
        switch (test) {
            case 1 -> outputString = OCRBench(iterations);
            case 2 -> outputString = SWBench(iterations);
            case 3 -> outputString = logRegressionBench(iterations);
        }
        individualTransmission(socket, outputString);
        compactTransmission(socket, outputString);
        closeConnection(socket);
    }

    /**
     * Performs the Optical Character Recognition Benchmark and sends the results
     * to the server.
     *
     * @param imagesToProcess
     * @throws IOException
     */

    public ArrayList<String> OCRBench(int imagesToProcess) throws IOException {
        OCRTest ocr = new OCRTest("tessdata");
        ArrayList<String> processedText = new ArrayList<>();
        int filesProcessed = 0;
        timer.start();
        while (filesProcessed != 10) {
            for (File image : dir.listFiles()) {
                timer.newLap();
                processedText.add(ocr.readImage(image));
                image.delete();
                filesProcessed++;

                if (filesProcessed == imagesToProcess) {
                    timer.stopAndPrint("OCR");
                    break;
                }
            }
        }
        return processedText;
    }

    /**
     * Performs the Smith-Waterman algorithm to benchmark the system.
     * This sends results to the server.
     *
     * @param iterations
     * @throws IOException
     * @throws InterruptedException
     */

    public ArrayList<String> SWBench(int iterations) throws IOException, InterruptedException {
        //NOTE: run time is affected most by query
        String[] inputFilesName = {"smallQuery", "database", "alphabet", "scoringmatrix"};
        String[] inputFileString = new String[4];
        Timer timer = new Timer();
        File[] inputFiles = new File[4];
        ArrayList<String> SWOutput = new ArrayList<>();
        int m = 1; //todo replace with args?
        int k = 1;

        //File grabbing
        TimeUnit.SECONDS.sleep(1);
        try {
            timer.start();
            for (int i = 0; i < iterations; i++) {
                timer.newLap();
                for (int j = 0; j < 4; j++) {
                    inputFiles[j] = new File("filesToProcess\\" + inputFilesName[j] + i + ".txt");
                    inputFileString[j] = "filesToProcess\\" + inputFilesName[j] + i + ".txt";
                }//end of j loop
                SWOutput.add(new SWinitialiser().run(inputFileString[0], inputFileString[1], inputFileString[2], inputFileString[3], m, k));
            }//end of i loop
            timer.stopAndPrint("SW run");

            //cleanup
            File dir = new File("filesToProcess");
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                file.delete();
            }// end of for loop

        } catch (Exception e) {
            e.printStackTrace();
        }
        return SWOutput;
    }

        /**
         *
         * @param iterations
         * @throws IOException
         * @return
         */

        public ArrayList<String> logRegressionBench (int iterations) throws IOException {
            LogRegressionInitializer logRegress = new LogRegressionInitializer();
            ArrayList<String> logRegressOutput = new ArrayList<>();
            String[] inputFilesName = {"BreastCancer", "testData"};
            String[] inputFileString = new String[2];
            File[] inputFiles = new File[2];
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timer.start();
            for (int i = 0; i < iterations; i++) {
                timer.newLap();
                for (int j = 0; j < inputFiles.length; j++) {
                    inputFiles[j] = new File("filesToProcess\\" + inputFilesName[j] + i + ".txt");
                    inputFileString[j] = "filesToProcess\\" + inputFilesName[j] + i + ".txt";
                }//end of j loop
                logRegressOutput.add(logRegress.LogRegressionInitializer(inputFileString[0], inputFileString[1]));
                inputFiles[0].delete();
                inputFiles[1].delete();
            }//end of i loop
            return logRegressOutput;
        }

        /**
         * Sends data separately to the server.
         *
         * @param socket
         * @param manyOutput
         * @throws IOException
         */

        public void individualTransmission (Socket socket, ArrayList < String > manyOutput) throws IOException {
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
            Timer timer = new Timer();

            timer.start();
            for (String out : manyOutput) {
                dataOutput.writeUTF(out);
                timer.newLap();
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

        public void compactTransmission (Socket socket, ArrayList < String > manyOutput) throws IOException {
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
            dataOutput.writeUTF(outputString);
            timer.stopAndPrint("Compact Transmission Start");
        }

    /**
     * Sends the message to close connection with server and then closes the socket
     *
     * @param socket
     */

    public void closeConnection(Socket socket){
            try {
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF("over");
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }//end of class
