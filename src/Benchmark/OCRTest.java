/**
 * Author: Seth Wolfgang
 * Date: 4/23/2022
 *
 * Creates a streamlined OCR class for the purpose benchmarking
 */

package Benchmark;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OCRTest extends Thread{
    Timer timer = new Timer();

    ITesseract tesseract = new Tesseract();
    private final String dataPath;
    private File image;
    private String output;
    private ArrayList<String> manyOutput = new ArrayList<String>();

    public OCRTest(String dataDir, File imagePath){
        dataPath = dataDir;
        image = imagePath;

        tesseract.setVariable("user_defined_dpi", "100");
        tesseract.setDatapath("tessdata");
    }

    public OCRTest(String dataDir){
        dataPath = dataDir;

        tesseract.setVariable("user_defined_dpi", "100");
        tesseract.setDatapath("tessdata");
    }

    /**
     * Runs a 'compact' version of the benchmark in which all data
     * is to be transmitted at once. Records the time it takes
     * and creates a lap for each iteration.
     *
     * @param iterations
     * @return
     */

    public ArrayList performCompactBenchmark(int iterations) throws IOException {
        timer.start();

        for(int i = 0; i < iterations; i++){
            manyOutput.add(doOCR());
            timer.newLap();
        }
        timer.stopTimer();
        System.out.println(manyOutput);
        timer.printResults("test");
        return timer.getLaps();
    }

    /**
     * Setter method for image. To be used when constructor does
     * not include a path to an image.
     *
     * @param imagePath
     */


    public void setImage(File imagePath){
        image = imagePath;
    }

    /**
     * Setter method to give OCR the DPI of the image
     * @param dpi
     */

    public void setDPI(int dpi){
        tesseract.setVariable("user_defined_dpi", String.valueOf(dpi));
    }

    /**
     * Performs the optical character recognition of the image provided
     * @return String
     * @throws TesseractException
     */

    public String doOCR(){
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
        return "OCR FAILED!";
    }

    /**
     * Performs the optical character recognition of the image provided
     * for as many iterations as specified
     * @return ArrayList
     * @throws TesseractException
     */

    public ArrayList<String> bulkOCR(int iteration){
        try {
            for(int i = 0; i < iteration; i++)
                manyOutput.add(tesseract.doOCR(image));
        } catch (TesseractException e) {
            System.out.println("OCR FAILED");
            e.printStackTrace();
        }
        return manyOutput;
    }

    /**
     * Getter method for the image's path
     * @return String
     */

    public String getImagePath() {
        return image.getPath();
    }

    /**
     * Getter method for manyOutput of bulkOCR
     * @return ArrayList
     */

    public ArrayList<String> getManyOutput() {
        return manyOutput;
    }

    /**
     * Getter method for the output of OCR
     * @return String
     */

    public String getOutput() {
        return output;
    }
}
