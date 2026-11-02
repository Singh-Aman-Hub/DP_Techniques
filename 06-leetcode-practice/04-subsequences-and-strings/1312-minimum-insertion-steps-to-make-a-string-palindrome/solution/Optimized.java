/**
 * 1312. Minimum Insertion Steps to Make a String Palindrome — Space-optimized DP
 *
 * Idea: keep one row. Before row[j] is overwritten it still holds dp[i+1][j]
 * (below); row[j-1] already holds dp[i][j-1] (left); the diagonal
 * dp[i+1][j-1] is carried in a variable.
 *
 * Time complexity:  O(n^2) — same loops as tabulation
 * Space complexity: O(n) — one row plus a saved diagonal
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minInsertions(String s) {
        int n = s.length();
        // row[j] = dp[i][j] once written in this pass, dp[i+1][j] before that
        int[] row = new int[n];
        for (int i = n - 2; i >= 0; i--) {
            int diagonal = 0;                                 // dp[i+1][i]: empty range
            for (int j = i + 1; j < n; j++) {
                int below = row[j];                           // dp[i+1][j]
                if (s.charAt(i) == s.charAt(j)) {
                    row[j] = diagonal;
                } else {
                    row[j] = 1 + Math.min(below, row[j - 1]);
                }
                diagonal = below;                             // dp[i+1][j] is the next j's diagonal
            }
        }
        return row[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example mbadm", minInsertions("mbadm"), 2);
        check("already a palindrome zzazz", minInsertions("zzazz"), 0);
        check("leetcode", minInsertions("leetcode"), 5);
        check("two different chars ab", minInsertions("ab"), 1);
        check("single char (edge)", minInsertions("a"), 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
