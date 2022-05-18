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

    public EdgeServer(String address, int port) throws IOException {
        easyFTPServer ftpServer = new easyFTPServer(address, 2221);

        Socket socket = new Socket();
        InetSocketAddress sa = new InetSocketAddress(address, port);

        //initial connection to server
        socket.connect(sa, 50000);
        System.out.println("Edge server connected to server");

        //connects to Server
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        int filesProcessed = 0;
        int imagesToProcess = 1000;
        File dir = new File("filesToProcess");
        String text = null;

        OCRTest ocr = new OCRTest("tessdata");
        while(filesProcessed != 1000) {
            for(File image : dir.listFiles()){

                text = ocr.readImage(image);
                text = text.replace("\n", "").replace("\r", "");
                image.delete();
                filesProcessed++;
                System.out.println(text + " " + filesProcessed);

                if(filesProcessed == imagesToProcess){
                    break;
                }
            }
        }



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

}//end of class
