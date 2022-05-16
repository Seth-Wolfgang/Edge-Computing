import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ComputeResults {

    public static void main(String[] args){
        File results = new File("Results");

        //adds all files from Results folder to arraylist
        try {
            ArrayList<File> resultsFiles = new ArrayList<>(Arrays.asList(Objects.requireNonNull(results.listFiles())));
            if(resultsFiles.size() == 0){
                System.out.println("Results folder contains no files. Please run Main or " +
                        "Main files in \\ComponentMains to create results files");
            }
        } catch(NullPointerException e) {
            System.out.println("Results folder does not exist!");
        }



    }
}
