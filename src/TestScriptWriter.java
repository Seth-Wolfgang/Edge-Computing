import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class TestScriptWriter {
    public static void main(String[] args) throws IOException {
        String cFileName = "client";
        String eFileName = "trials.txt";

        File edgeTestScript = new File(eFileName);
        File clientTestScript = new File(cFileName + ".sh");

        edgeTestScript.createNewFile();
        clientTestScript.createNewFile();

        PrintWriter edgeWriter  = new PrintWriter(new FileWriter(edgeTestScript, true));
        PrintWriter clientWriter = new PrintWriter(new FileWriter(clientTestScript, true));

        clientWriter.append("#!/bin/sh\n");

        for(int test = 1; test <= 3; test++){
            for(int size = 1; size <= 3; size++){
                for(int iterations = 10; iterations <= 30; iterations+=10){
                    for(int clients = 1; clients <= 3; clients++){
                        edgeWriter.append(test + ";" + size + ";" + iterations + ";" + clients + "\n");
                        clientWriter.append("java -jar ProductionMain.jar -c $1 2221 &&\n");
                    }
                }
            }
        }
        edgeWriter.append("0;0;0;0");
        edgeWriter.close();
        clientWriter.close();
    }
}