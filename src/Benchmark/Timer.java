package Benchmark; /**
 * Author: Seth Wolfgang
 * Date: 4/23/2022
 *
 * This class is a simple timer for the purpose of benchmarking programs
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Timer extends Thread {

    private long initialTime = 0; //for use in getting total time
    private long initialLapTime = 0; //for use in laps
    private long totalTime = 0;
    ArrayList<Long> laps = new ArrayList<Long>();

    /**
     * Starts the timer by setting the initialTime and
     * initialLapTime to System.nanotime()
     *
     * time is represented in nanoseconds
     */

    public void start() {
        initialTime = System.nanoTime();
        initialLapTime = initialTime;
    }

    /**
     * Stops timer by setting `initialTime` to 0.
     * This method also sets totalTime to the current system time
     * minus the inital time. This method effects lap times as well.
     *
     * Time is represented in nanoseconds
     */

    public void stopTimer(){
        if(initialTime > 0) {
            totalTime = System.nanoTime() - initialTime;
            initialTime = 0; //"stops" timer
            initialLapTime = 0;
        } else {
            System.out.println("Must start timer calling `stop` method.");
        }
    }

    /**
     * Creates a `lap` for the timer. This marks a point in time at which
     * the method is called. The timer is reset to the current system time
     * after a new lap is created.
     */

    public void newLap(){
        if(initialTime > 0) {
            laps.add(System.nanoTime() - initialLapTime);
            resetInitialLapTime();
        } else {
            System.out.println("Must start timer before calling `newLap` method");
        }
    }

    /**
     * Sets initialLapTime to System.nanotime() for use in newLap()
     */

    private void resetInitialLapTime(){
        initialLapTime = System.nanoTime();
    }

    /**
     * Clears out any values in lap
     */

    public void clearLaps(){
        laps.clear();
    }

    /**
     * Getter for initialTime
     *
     * @return long
     */

    public long getInitialTime() {
        return initialTime;
    }

    /**
     * Getter for TotalTime
     * Alternative for this is the sum of laps
     *
     * @return long
     */

    public long getTotalTime(){
        return totalTime;
    }

    /**
     * Getter for lap arraylist
     *
     * @return ArrayList
     */

    public ArrayList<Long> getLaps(){
        return laps;
    }

    /**
     * Displays all times to the console. Method can reduce nanoseconds
     * to a more readable format with the `digits` parameter.
     *
     * @param digits
     */

    public void showTable(int digits){
        System.out.println("TABLE OF LAPS AND TOTAL TIME");

        for(int i = 0; i < laps.size(); i++)
            System.out.printf("%d \t : %d \t\n", i, laps.get(i) / digits);
        System.out.printf("Total time: \t %d",totalTime / digits);
    }

    /**
     *  Displays all times to the console in nanoseconds.
     */

    //todo maybe delete? method is unfinished
    public void showTable(){
        System.out.println("TABLE OF LAPS AND TOTAL TIME");

        for(int i = 0; i < laps.size(); i++)
            System.out.printf("%d \t : %d \t", i, laps.get(i));


        System.out.printf("Total time: \t %d", totalTime);
    }

    /**
     * Prints the results of timer to a file for ease of use
     *
     * @throws IOException
     */

    public void printResults () throws IOException {
        File results = new File("Results.txt");
        PrintWriter writer = new PrintWriter(new FileWriter(results, true));

        System.out.println("Output file create: Results.txt");
        writer.append("Test performed at: " + System.currentTimeMillis() + "\n");

        for(Long time : laps)
            writer.append(String.valueOf(time) + "\t");

        writer.append("\nTotal: " + getTotalTime() + "\n");
        writer.close();
    }

    /**
     * Overload of printResults() method, used to add a descriptive name
     * before results are printed. Performes printResults() after writing
     * the tag.
     *
     * @param tag a name for the method to print before reulst are printed
     * @throws IOException
     */

    public void printResults (String tag) throws IOException {
        File results = new File("Results.txt");
        PrintWriter writer = new PrintWriter(new FileWriter(results, true));
        writer.append("Test name: " + tag + "\n");
        writer.close();
        printResults();
    }

}//end of class
