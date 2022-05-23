/**
 * Source: https://github.com/Agelos369/Logistic-Regression-JAVA
 */
package LogisticRegression;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
 
public class LogRegressionMain {
     
    private ArrayList<Example> readDataSet(String file){
        //ArrayList which contains the training examples;
        ArrayList<Example> dataList = new ArrayList<>();
         
        try{
            File file1 = new File(file);
            Scanner scanner = new Scanner(file1);
             
      
            //Read the 367 examples for the training
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                 
                String[] inputParts = line.split(",");
                //System.out.println(Arrays.toString(inputParts));
                //Skip first which is the id and second which is the category
                String[] attrs = Arrays.copyOfRange(inputParts, 2, 32);
                String cate = inputParts[1];
                 
                String cat = cate;
                double[] attributes = new double[attrs.length];               
                for(int i=0; i<=attrs.length-1; i++){
                    attributes[i] = Double.parseDouble(attrs[i]);
                }
                Example example = new Example(cat, attributes);
                dataList.add(example);
            }
            scanner.close();
                         
             
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return dataList;
         
    }
     
    public String LogRegressionInitializer(String inputFile1, String inputFile2){
        ArrayList<Example> examples = readDataSet(inputFile1); //should be BreastCancer.txt in ftpResources
        LogisticRegresion classifier = new LogisticRegresion(30,0.001);
        String output;
        //train the classifier
        classifier.train(examples);
         
        //Print the probabilities that the training examples (first 367 of the  dataset) are classified in category M
        System.out.println("\n The probabilities that the training examples are classified in category M are :\n");
        int o = 0;
        for(int i=0; i<examples.size(); i++){
            System.out.println("Example belongs in category " + examples.get(i).getCategory() + " " + classifier.findProbability(examples.get(i).getAttributes()));
            if(((classifier.findProbability(examples.get(i).getAttributes())>0.5) && (examples.get(i).getCategory().equals("B"))) ||(classifier.findProbability(examples.get(i).getAttributes())<0.5)&& (examples.get(i).getCategory().equals("M")) ) o++;
        }
        //Wrong classified training examples
        System.out.println("\n The number of the wrong classified training examples is: "+o);
         
        
        //Test the classifier with 100 test examples (aproximately 20% of the dataset)
        System.out.println("\n The probabilities that the test examples are classified in category M are :\n");
         try{
            File file1 = new File(inputFile2); //should be testData.txt in ftpResources
            Scanner scanner = new Scanner(file1);
             
            //Wrong classified test examples
            int a = 0;         
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                 
                String[] inputParts = line.split(",");
                //Skip first which is the id and second which is the category
                String[] attrs = Arrays.copyOfRange(inputParts, 2, 32);
                String cate = inputParts[1];
                 
                String cat = cate;
                double[] attributes = new double[attrs.length];               
                for(int i=0; i<=attrs.length-1; i++){
                    attributes[i] = Double.parseDouble(attrs[i]);
                }
                System.out.println("Example belongs to category: " +cat + " "+ classifier.findProbability(attributes));
                if((classifier.findProbability(attributes)>0.5 && cat.equals("B")) ||classifier.findProbability(attributes)<0.5 && cat.equals("M") ) a++;
                
            }
            System.out.println("\n The number of the wrong classified test examples is: "+a);
            scanner.close();
                         
             
        }catch(Exception ex){
            ex.printStackTrace();
        }


        return output;
    }
     
}
