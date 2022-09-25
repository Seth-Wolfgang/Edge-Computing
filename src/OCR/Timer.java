/**
 * Author: Seth Wolfgang
 * Date: 4/23/2022
 * <p>
 * This class is a simple timer for the purpose of benchmarking programs
 */

package OCR;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Timer extends Thread {

    ArrayList<Long> laps = new ArrayList<Long>();
    private long initialTime = 0; //for use in getting total time
    private long initialLapTime = 0; //for use in laps
    private long totalTime = 0;
    private boolean running = false;

    /**
     * Starts the timer by setting the initialTime and
     * initialLapTime to System.nanotime()
     *
     * time is represented in nanoseconds
     */

    public void start() {
        initialTime = System.nanoTime();
        initialLapTime = initialTime;
        running = true;
    }

    /**
     * Stops timer by setting `initialTime` to 0.
     * This method also sets totalTime to the current system time
     * minus the inital time. This method effects lap times as well.
     *
     * Time is represented in nanoseconds
     */

    public void stopTimer() {
        if (running) {
            totalTime = System.nanoTime() - initialTime;
            initialTime = 0; //"stops" timer
            initialLapTime = 0;
            running = false;
        } else {
            System.out.println("Must start timer calling `stop` method.");
        }
    }

    /**
     * Creates a `lap` for the timer. This marks a point in time at which
     * the method is called. The timer is reset to the current system time
     * after a new lap is created.
     */

    public void newLap() {
        if (running) {
            laps.add(System.nanoTime() - initialLapTime);
            resetInitialLapTime();
        } else {
            System.out.println("Must start timer before calling `newLap` method");
        }
    }

    /**
     * Sets initialLapTime to System.nanotime() for use in newLap()
     */

    private void resetInitialLapTime() {
        initialLapTime = System.nanoTime();
    }

    /**
     * Clears out any values in lap
     */

    public void clearLaps() {
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

    public long getTotalTime() {
        return totalTime;
    }

    /**
     * Getter for lap arraylist
     *
     * @return ArrayList
     */

    public ArrayList<Long> getLaps() {
        return laps;
    }

    /**
     * Prints the results of the timer in nanoseconds to a text file called
     * Results.txt.
     *
     * @param tag a name for the method to print before results are printed
     * @throws IOException
     */

    public void printResultsToFile(String tag) throws IOException {
        File results = new File("Results\\" + tag + ".csv");

        if (results.createNewFile()) {
            System.out.println("Created " + results.getPath());
        }

        PrintWriter writer = new PrintWriter(new FileWriter(results, true));
        writer.append(tag).append(",");
        for (Long time : laps)
            writer.append(String.valueOf(time)).append(",");

        writer.append(",").append(String.valueOf(getTotalTime())).append("\n");
        writer.close();
    }

    /**
     * Quality of life method to stop timer and run printResultsToFile() method
     *
     * @param tag a name for the method to print before results are printed
     * @throws IOException
     */

    public void stopAndPrint(String tag) throws IOException {
        if (running) {
            stopTimer();
        }
        printResultsToFile(tag);

        this.laps.clear();
        this.initialTime = 0;
        this.initialLapTime = 0;
        this.totalTime = 0;
        this.running = false;

    }

}//end of class
