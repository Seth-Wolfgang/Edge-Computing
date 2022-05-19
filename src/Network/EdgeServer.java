package Network;


import OCR.OCRTest;
import OCR.Timer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class EdgeServer {

    ArrayList<String> processedText = new ArrayList<>();
    File dir = new File("filesToProcess");
    Timer timer = new Timer();

    public EdgeServer(String address, int port) throws IOException {

        //initial networking
        easyFTPServer ftpServer = new easyFTPServer(address, 2221);
        Socket socket = new Socket();
        InetSocketAddress sa = new InetSocketAddress(address, port);

        //initial connection to server
        socket.connect(sa, 10000);
        System.out.println("Edge server connected to server");

        int filesProcessed = 0;
        int imagesToProcess = 10;


    }

    public void individualTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        Timer timer = new Timer();

        timer.start();
        for (String out : manyOutput){
            dataOutput.writeUTF(out);
            timer.newLap();
        }
        timer.stopTimer();
        timer.printResultsToFile("Individual Transmission Start");
    }

    public void compactTransmission(Socket socket, ArrayList<String> manyOutput) throws IOException {
        DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
        String outputString = manyOutput.get(0); //Semicolon seperated values of manyOutput
        Timer timer = new Timer();

        //allows for proper formatting
        manyOutput.remove(0);
        //Converts ArrayList to semicolon seperated values
        for(String output : manyOutput) {
            outputString = outputString + ";" + output;
        }

        //removes unnecessary new lines
        outputString = outputString.replace("\n", "").replace("\r", "");

        //times the transmission until it is done
        timer.start();
        dataOutput.writeUTF(outputString);
        timer.stopAndPrint("Compact Transmission Start");
    }

    public void OCRBench(Socket socket, int imagesToProcess) throws IOException {
        OCRTest ocr = new OCRTest("tessdata");
        int filesProcessed = 0;
        timer.start();
        while(filesProcessed != 10) {
            for(File image : dir.listFiles()){
                timer.newLap();
                this.processedText.add(ocr.readImage(image));
                image.delete();
                filesProcessed++;

                if(filesProcessed == imagesToProcess){
                    timer.stopAndPrint("OCR");
                    break;
                }
            }
        }
        //individualTransmission(socket, processedText);
        compactTransmission(socket, this.processedText);
    }

}//end of class
