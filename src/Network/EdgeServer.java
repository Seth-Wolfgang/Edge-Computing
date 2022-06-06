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
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class EdgeServer {

    File dir = new File("filesToProcess");
    Timer timer = new Timer();
    int test = 3;
    int iterations;
    ArrayList<String> outputString = new ArrayList<>();

    public EdgeServer(String address, int port, int test, int iterations) throws IOException, InterruptedException {
        this.iterations = iterations;

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
        cleanUp();
        //closeConnection(socket);
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
        ArrayList<File> images;
        int filesProcessed = 0;

        waitForFiles(1);
        timer.start();
        images = grabFiles("woah.*");
        while (filesProcessed != imagesToProcess) {
                timer.newLap();
                processedText.add(ocr.readImage(images.get(0)));
                images.remove(0);
                filesProcessed++;

                if (filesProcessed == imagesToProcess) {
                    timer.stopAndPrint("OCR");
                    break;
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
        String[] inputFilesName = {"smallQuery.*", "database.*", "alphabet.*", "scoringmatrix.*"};
        Timer timer = new Timer();
        ArrayList<ArrayList<File>> inputFiles = new ArrayList();
        ArrayList<String> SWOutput = new ArrayList<>();

        waitForFiles(4);

        for (int i = 0; i < 4; i++) {
            inputFiles.add(grabFiles(inputFilesName[i]));
        }
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
     * @param iterations
     * @return
     * @throws IOException
     */

    public ArrayList<String> logRegressionBench(int iterations) throws IOException {
        LogRegressionInitializer logRegress = new LogRegressionInitializer();
        ArrayList<String> logRegressOutput = new ArrayList<>();
        ArrayList<ArrayList<File>> inputFiles = new ArrayList<>(2);

        waitForFiles(2);

        timer.start();
        inputFiles.add(grabFiles("B.*"));
        inputFiles.add(grabFiles("t.*"));
        for (int i = 0; i < iterations; i++) {
            timer.newLap();
            logRegressOutput.add(logRegress.LogRegressionInitializer(inputFiles.get(0).get(0), inputFiles.get(1).get(0)));
            //inputFiles.get(0).get(0).delete();
            //inputFiles.get(1).get(0).delete();
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

    public void individualTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
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
        dataOutput.writeUTF(outputString);
        timer.stopAndPrint("Compact Transmission Start");
    }

    /**
     * Grabs the first file to be processed. Allows easy use of regex
     *
     * @param regex
     * @return ArrayList<File>
     * @throws IOException
     */

    public ArrayList<File> grabFiles(String regex) throws IOException {
        Pattern pattern = Pattern.compile(regex);
        ArrayList<File> files = new ArrayList<>();

        //looks through the directory for files that match the regex
        //and adds them to the returned file ArrayList
        for (File file : dir.listFiles()) {
            if (pattern.asPredicate().test(file.getName())) {
                files.add(file);
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
     * <p>
     * todo (May add to this method later?)
     */

    private void cleanUp() {
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    private void waitForFiles(int numOfInputFiles){
        while(dir.listFiles().length < (this.iterations * numOfInputFiles)) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}//end of class


