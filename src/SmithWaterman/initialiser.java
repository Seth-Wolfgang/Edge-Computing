/**
 * Source: https://github.com/JayakrishnaThota/Sequence-Alignment
 */


package SmithWaterman;

import java.util.*;

public class initialiser {
    public static void main(String[] args) throws Exception {
        helper h = new helper();
        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(Collections.reverseOrder());
        Scanner in = new Scanner(System.in);
        int selector = 2;
        String queryFile = "src\\SmithWaterman\\query.txt";
        String databaseFile = "src\\SmithWaterman\\database.txt";
        String alphabetFile = "src\\SmithWaterman\\alphabet.txt";
        String scorematrixFile = "src\\SmithWaterman\\scoringmatrix.txt";
        String[] queryRecords = h.getrecords(queryFile);
        String[] databaseRecords = h.getrecords(databaseFile);
        String alphabets = h.getalphabet(alphabetFile);
        int[][] matrix = h.getmatrix(scorematrixFile);
        int k = 10; //these neeg args
        int m = 1;
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
            System.out.println("score = " + temp);
            System.out.println("id1 " + idmap.get(temp)[0] + " " + offsetmap.get(temp)[0] + " " + offsetmap.get(temp)[1] + " " + smap.get(temp)[0]);
            System.out.println("id2 " + idmap.get(temp)[1] + " " + offsetmap.get(temp)[2] + " " + offsetmap.get(temp)[3] + " " + smap.get(temp)[1]);
        }
    }
}
