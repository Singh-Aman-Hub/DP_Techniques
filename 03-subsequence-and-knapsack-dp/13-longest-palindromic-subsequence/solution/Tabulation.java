/**
 * Longest Palindromic Subsequence (LeetCode 516) — Tabulation (interval form)
 *
 * Idea: dp[i][j] = LPS length of s[i..j]. A window depends on windows that are one
 *       character shorter: (i+1, j-1), (i+1, j) and (i, j-1). Filling i from n-1 DOWN
 *       to 0 and, inside, j from i UP to n-1 guarantees those are ready (row i+1 is
 *       complete and dp[i][j-1] was just written).
 *
 * Time complexity:  O(n^2) — n(n+1)/2 cells, O(1) each.
 * Space complexity: O(n^2) — the table (only the upper triangle is used).
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int longestPalindromeSubseq(String s) {
        int n = s.length();
        int[][] dp = new int[n][n];      // dp[i][j] = LPS of s[i..j]; cells with i > j stay 0 (empty window)
        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;                 // single character
            for (int j = i + 1; j < n; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 2 + dp[i + 1][j - 1];   // when j == i+1 this reads dp[i+1][i] = 0 (empty)
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[0][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("bbbab", longestPalindromeSubseq("bbbab"), 4);
        check("cbbd", longestPalindromeSubseq("cbbd"), 2);
        check("agbdba", longestPalindromeSubseq("agbdba"), 5);
        check("abcd (no repeats)", longestPalindromeSubseq("abcd"), 1);
        check("a (edge: single char)", longestPalindromeSubseq("a"), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
