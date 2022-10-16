import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class TestScriptWriter {
    public static void main(String[] args) throws IOException {
        String testFileName = "trials.txt";

        File edgeTestScript = new File(testFileName);

        edgeTestScript.createNewFile();

        PrintWriter edgeWriter  = new PrintWriter(new FileWriter(edgeTestScript, true));

        for(int size = 1; size <= 3; size++){
            for(int iterations = 25; iterations <= 25; iterations+=25){
                for(int clients = 1; clients <= 3; clients++){
                    edgeWriter.append("1"+ ";" + size + ";" + iterations + ";" + clients + "\n");
                }
            }
        }

        for(int size = 1; size <= 3; size++){
            for(int iterations = 100; iterations <= 100; iterations+=100){
                for(int clients = 1; clients <= 3; clients++){
                    edgeWriter.append(2 + ";" + size + ";" + iterations + ";" + clients + "\n");
                }
            }
        }

        for(int size = 1; size <= 3; size++){
            for(int iterations = 100; iterations <= 100; iterations+=100){
                for(int clients = 1; clients <= 3; clients++){
                    edgeWriter.append(3 + ";" + size + ";" + iterations + ";" + clients + "\n");
                }
            }
        }




        edgeWriter.append("0;0;0;0");
        edgeWriter.close();
    }
}