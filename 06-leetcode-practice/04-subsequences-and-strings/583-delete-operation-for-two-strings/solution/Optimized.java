/**
 * 583. Delete Operation for Two Strings — Space-optimized DP
 *
 * Idea: each row of the table reads only the previous row. Keep one array,
 * save the diagonal before overwriting, and reset row[0] = i at the start of
 * every row (that is the base case dp[i][0]).
 *
 * Time complexity:  O(n * m) — same loops as tabulation
 * Space complexity: O(m) — one row plus a saved diagonal
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minDistance(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        // row[j] = dp[i][j] once written in this pass, dp[i-1][j] before that
        int[] row = new int[m + 1];
        for (int j = 0; j <= m; j++) row[j] = j;          // dp[0][j]
        for (int i = 1; i <= n; i++) {
            int diagonal = row[0];                        // dp[i-1][0]
            row[0] = i;                                   // dp[i][0]
            for (int j = 1; j <= m; j++) {
                int above = row[j];                       // dp[i-1][j]
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    row[j] = diagonal;
                } else {
                    row[j] = 1 + Math.min(above, row[j - 1]);
                }
                diagonal = above;
            }
        }
        return row[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example sea / eat", minDistance("sea", "eat"), 2);
        check("leetcode / etco", minDistance("leetcode", "etco"), 4);
        check("identical strings", minDistance("abc", "abc"), 0);
        check("nothing in common (edge)", minDistance("a", "b"), 2);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
