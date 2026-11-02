/**
 * 1312. Minimum Insertion Steps to Make a String Palindrome — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = min insertions for s[i..j]. Row i reads row i+1 (below) and
 * the cell to its left, so i runs from n-2 down to 0 and j from i+1 up.
 * Diagonal and empty ranges stay 0 (base cases).
 *
 * Time complexity:  O(n^2) — upper triangle of the table, O(1) per cell
 * Space complexity: O(n^2) — the n x n table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minInsertions(String s) {
        int n = s.length();
        // dp[i][j] = minimum insertions that make s[i..j] a palindrome (0 when i >= j)
        int[][] dp = new int[n][n];
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = dp[i + 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
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
