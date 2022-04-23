/**
 * Author: Seth Wolfgang
 * Date: 4/23/2022
 *
 * Creates a streamlined OCR class for the purpose benchmarking
 */

package Benchmark;

import Benchmark.Timer;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.ArrayList;

public class OCRTest {
    Timer timer = new Timer();
    ITesseract tesseract = new Tesseract();
    private final String dataPath;
    private File image;
    private String output;

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

    public ArrayList performCompactBenchmark(int iterations){
        timer.start();

        for(int i = 0; i < iterations; i++){
            doOCR();
            timer.newLap();
        }
        timer.stop();

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
     * Getter method for the image's path
     * @return String
     */

    public String getImagePath() {
        return image.getPath();
    }


}
