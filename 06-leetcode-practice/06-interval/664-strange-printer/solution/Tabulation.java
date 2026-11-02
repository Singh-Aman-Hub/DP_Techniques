/**
 * 664. Strange Printer — Tabulation (bottom-up)
 *
 * Idea: same recurrence bottom-up. dp[i][j] reads rows with a larger start
 * (i+1 and every k > i), so i runs from n-1 down to 0 and j from i upward.
 * An extra row/column keeps empty ranges (value 0) inside the table.
 *
 * Time complexity:  O(n^3) — start x end x merge partner
 * Space complexity: O(n^2) — the (n+1) x (n+1) table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int strangePrinter(String s) {
        int n = s.length();
        // dp[i][j] = min turns to print s[i..j]; cells with i > j are empty ranges (0)
        int[][] dp = new int[n + 1][n + 1];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                dp[i][j] = 1 + dp[i + 1][j];                  // s[i] alone (gives 1 when i == j)
                for (int k = i + 1; k <= j; k++) {
                    if (s.charAt(k) == s.charAt(i)) {
                        dp[i][j] = Math.min(dp[i][j], dp[i + 1][k - 1] + dp[k][j]);
                    }
                }
            }
        }
        return dp[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example abab", strangePrinter("abab"), 3);
        check("aaabbb", strangePrinter("aaabbb"), 2);
        check("aba", strangePrinter("aba"), 2);
        check("tbgtgb", strangePrinter("tbgtgb"), 4);
        check("single char (edge)", strangePrinter("a"), 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
