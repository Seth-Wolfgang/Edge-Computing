//import net.sourceforge.tess4j.*;
//import net.sourceforge.tess4j.util.ImageHelper;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//public class OCR {
//    public static void main(String[] args) throws FileNotFoundException {
//        long startTime = System.nanoTime();
//        ITesseract tesseract = new Tesseract();
//        BufferedImage image = null;
//        try {
//            image = ImageIO.read(new File("C:\\Users\\wolfg\\Downloads\\woahman.png"));
//            image = ImageHelper.convertImageToGrayscale(image);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//
//            tesseract.setDatapath("C:\\Users\\wolfg\\IdeaProjects\\EdgeComputing\\tessdata");
//            tesseract.setLanguage("eng");
//
//            // the path of your tess data folder
//            // inside the extracted file
//            String text = tesseract.doOCR(image);
//
//            // path of your image file
//            System.out.println(text);
//        } catch (TesseractException e) {
//            e.printStackTrace();
//        }
//        long endTime = System.nanoTime();
//        System.out.println((endTime - startTime) / 1000000000.0);
//
//    }
//}