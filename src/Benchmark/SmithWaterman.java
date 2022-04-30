/**
 * Source https://github.com/JayakrishnaThota/Sequence-Alignment/blob/master/SmithWaterman.java
 */

package Benchmark;

public class SmithWaterman
{
    String seq1;
    String seq2;
    int[][] matrix;
    String alphabet;
    int gap_penalty;
    int[][] dp;
    int score;
    String[] finaloutput;
    int[] start = new int[2];
    public SmithWaterman(String seq1, String seq2, int[][] matrix, String alphabet, int gap_penalty)
    {
        this.seq1 = seq1.toUpperCase();
        this.seq2 = seq2.toUpperCase();
        this.matrix = matrix;
        this.alphabet = alphabet;
        this.gap_penalty = gap_penalty;
        this.dp = dynamic();
        this.score = getmaxscore(dp);
        this.finaloutput = getsequences();
    }

    //The main method to calculate the matrix scores for the sequences
    public int[][] dynamic()
    {
        int[][] dp = new int[seq1.length()+1][seq2.length()+1];
        //Fill the first column with zeroes
        for(int i = 1; i <= seq1.length(); i++)
        {
            dp[i][0] = 0;
        }
        //Fill the first row with zeroes
        for(int j = 1; j <= seq2.length(); j++)
        {
            dp[0][j] = 0;
        }
        //Recursive Formulation for all other cells according to smith-waterman algorithm
        for(int i = 1; i < seq1.length() + 1; i++)
        {
            for(int j = 1; j < seq2.length() + 1; j++)
            {
                dp[i][j] = Math.max(Math.max(Math.max(0,dp[i][j-1] + gap_penalty),dp[i-1][j] + gap_penalty),
                        dp[i-1][j-1] + getValues(matrix,seq1.charAt(i-1),seq2.charAt(j-1),alphabet));
            }
        }
        return dp;
    }
    //Finds the path and gets the output modified sequences
    public String[] getsequences()
    {
        String outputseq1 = "";
        String outputseq2 = "";
        int i = getmaxrow(dp);
        int j = getmaxcolumn(dp);
        while (i != 0  && j != 0 && dp[i][j]!=0)
        {
            if (dp[i-1][j-1] == dp[i][j] - getValues(matrix,seq1.charAt(i-1),seq2.charAt(j-1),alphabet))
            {
                outputseq1 = seq1.charAt(i-1) + outputseq1;
                outputseq2 = seq2.charAt(j-1) + outputseq2;
                i -= 1;
                j -= 1;
            }
            else if (dp[i][j-1] == dp[i][j] - gap_penalty)
            {
                outputseq1 = "-" + outputseq1;
                outputseq2 = seq2.charAt(j-1) + outputseq2;
                j -= 1;
            }
            else {
                outputseq1 = seq1.charAt(i-1) + outputseq1;
                outputseq2 = "-" + outputseq2;
                i -= 1;
            }
            start[0] = i;
            start[1] = j;
        }
        return new String[] {outputseq1, outputseq2};
    }
    //Helper function for returning the starting position of column
    public int getmaxcolumn(int[][] dp) {
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp[0].length;j++){
                if(score==dp[i][j])
                    return j;
            }
        }
        return -1;
    }
    //Helper function for returning the starting position of row
    public int getmaxrow(int[][] dp) {
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp[0].length;j++){
                if(score==dp[i][j])
                    return i;
            }
        }
        return -1;
    }
    //Helper function for returning the max score in the matrix
    public int getmaxscore(int[][] dp) {
        int max = 0;
        for(int i=0;i<dp.length;i++){
            for(int j=0;j<dp[0].length;j++){
                max = Math.max(max,dp[i][j]);
            }
        }
        return max;
    }
    //Helper functions for returning the score
    public int getscores(){
        return score;
    }
    //Helper function for returning the starting and ending sequence position in the local alignment for 2 sequeneces
    public Integer[] getstart(){
        Integer[] ret = new Integer[4];
        ret[0] = start[0];
        ret[1] = getmaxrow(dp);
        ret[2] = start[1];
        ret[3] = getmaxcolumn(dp);
        return ret;
    }
    //Helper function for returning the aligned strings
    public String[] getoutputsequences()
    {
        return finaloutput;
    }
    //Returns the appropriate value from the matrix for the alphabet pair
    public int getValues(int[][] matrix, char a, char b, String s)
    {
        int index1 = s.indexOf(a);
        int index2 = s.indexOf(b);
        return matrix[index1][index2];
    }
}