/**
 * Changes by Seth Wolfgang
 * Date: 5/5/2022
 * <p>
 * Slightly modified version taken from source. Returns output instead of
 * printing to console.
 * <p>
 * Source: https://github.com/JayakrishnaThota/Sequence-Alignment
 */


package SmithWaterman;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SWinitialiser {
    public String run(String queryFile, String databaseFile, String alphabetFile, String scoreMatrixFile, int k, int m) throws Exception {
        helper h = new helper();
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(Collections.reverseOrder());
        String output = null;

        String[] queryRecords = h.getrecords(queryFile);
        String[] databaseRecords = h.getrecords(databaseFile);
        String alphabets = h.getalphabet(alphabetFile);
        int[][] matrix = h.getmatrix(scoreMatrixFile);

        String[] qids = h.getids(queryFile);
        String[] dids = h.getids(databaseFile);
        Map<Integer, String[]> smap = new HashMap<Integer, String[]>();
        Map<Integer, String[]> idmap = new HashMap<Integer, String[]>();
        Map<Integer, Integer[]> offsetmap = new HashMap<Integer, Integer[]>();

        for (int i = 0; i < queryRecords.length; i++) {
            for (int j = 0; j < databaseRecords.length; j++) {
                String query = queryRecords[i];
                String sequence = databaseRecords[j];
                SmithWaterman s = new SmithWaterman(query, sequence, matrix, alphabets, m);
                int temp = s.getscores();
                smap.put(temp, s.getoutputsequences());
                idmap.put(temp, new String[]{qids[i], dids[j]});
                offsetmap.put(temp, s.getstart());
                pq.offer(temp);
            }
        }
        for (int i = 0; i < k; i++) {
            int temp = pq.poll();
            output = "score = " + temp +
                    "\n" + "id1 " + idmap.get(temp)[0] + " " + offsetmap.get(temp)[0] + " " + offsetmap.get(temp)[1] + " " + smap.get(temp)[0] +
                    "\n" + "id2 " + idmap.get(temp)[1] + " " + offsetmap.get(temp)[2] + " " + offsetmap.get(temp)[3] + " " + smap.get(temp)[1];
        }
        return output;
    }
}
