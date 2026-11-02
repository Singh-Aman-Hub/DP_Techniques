/**
 * Edit Distance (LeetCode 72) — O(m) space with two rows
 *
 * Idea: Row i reads only row i-1 (diagonal and above) and row i itself (left). Keep
 *       `previous` = row i-1 and `current` = row i. The base column must be set at the
 *       start of every row: current[0] = i (delete all i characters of word1's prefix).
 *
 * Time complexity:  O(n * m) — same cells as the 2D table.
 * Space complexity: O(m) — two arrays of length m + 1.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minDistance(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        int[] previous = new int[m + 1];   // previous[j] = dp[i-1][j]
        int[] current = new int[m + 1];    // current[j]  = dp[i][j]
        for (int j = 0; j <= m; j++) previous[j] = j;   // row 0: insert j characters
        for (int i = 1; i <= n; i++) {
            current[0] = i;                               // column 0: delete i characters
            for (int j = 1; j <= m; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    current[j] = previous[j - 1];
                } else {
                    current[j] = 1 + Math.min(previous[j - 1], Math.min(previous[j], current[j - 1]));
                }
            }
            int[] swap = previous; previous = current; current = swap;
        }
        return previous[m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("horse -> ros", minDistance("horse", "ros"), 3);
        check("intention -> execution", minDistance("intention", "execution"), 5);
        check("\"\" -> abc (edge: insert all)", minDistance("", "abc"), 3);
        check("abc -> \"\" (edge: delete all)", minDistance("abc", ""), 3);
        check("abc -> abc (already equal)", minDistance("abc", "abc"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
