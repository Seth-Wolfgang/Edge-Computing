package LogisticRegression;

import java.util.ArrayList;

public class LogisticRegresion {

    //the number of epochs
    private final int ITERATIONS = 100;
    //the learning step
    private double h;
    //the regularization parameter
    private double L;
    //the weights to be learned
    private double[] weights;


    public LogisticRegresion(int n, double L) {
        this.weights = new double[n];
        this.h = 0.001;
        this.L = L;
    }

    public LogisticRegresion(int n, double h, double L) {
        this.weights = new double[n];
        this.h = h;
        this.L = L;
    }

    public LogisticRegresion() {
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public void setH(double h) {
        this.h = h;
    }

    public void train(ArrayList<Example> trExamples) {
        //Epochs
        for (int e = 0; e <= ITERATIONS; e++) {

            //initialize the weights with random values between 0.0 and 1.0
            //for(int p=0; p<this.weights.length; p++){
            //   this.weights[p] = Math.random();
            // }

            //the log likelihood
            double llh = 0;
            for (int i = 0; i < trExamples.size(); i++) {
                double[] x = trExamples.get(i).getAttributes();
                double output = findProbability(x);   //the output of the sigmoid funcion
                String cat = trExamples.get(i).getCategory();   //the category that the training example belongs to
                double c;
                if (cat.equals("M")) {
                    c = 1;
                } else {
                    c = 0;
                }
                for (int j = 0; j < this.weights.length; j++) {
                    if (j >= x.length) break;
                    this.weights[j] = (1 - 2 * this.L * this.h) * this.weights[j] + this.h * (c - output) * x[j];
                }
                llh += getLogLikelihood(c, x);
            }
            //System.out.println("Epoch " + e + " weights " + Arrays.toString(this.weights) + " LogLikelihood " + llh);
        }
    }


    //Calculate the sum of w*x for each weight and attribute
    //call the sigmoid function with that s
    public double findProbability(double[] x) {
        double s = 0;
        for (int i = 0; i < this.weights.length; i++) {
            if (i >= x.length) break;
            s += this.weights[i] * x[i];
        }
        return sigmoid(s);
    }

    //Sigmoid with overflow check
    private double sigmoid(double s) {
        if (s > 20) {
            s = 20;
        } else if (s < -20) {
            s = -20;
        }
        double exp = Math.exp(s);
        return exp / (1 + exp);
    }

    //Calculate log likelihood on given data
    private double getLogLikelihood(double cat, double[] x) {
        return cat * Math.log(findProbability(x)) + (1 - cat) * Math.log(1 - findProbability(x));
    }

}
