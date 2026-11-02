/**
 * Distinct Subsequences (LeetCode 115) — Tabulation (bottom-up DP)
 *
 * Idea: dp[i][j] = ways to form t[0..j) as a subsequence of s[0..i).
 *       Column 0 is all 1 (empty target), row 0 is 0 for j > 0 (empty source).
 *       dp[i][j] = dp[i-1][j] + (s[i-1] == t[j-1] ? dp[i-1][j-1] : 0).
 *       Each row depends only on the row above, so fill rows top to bottom.
 *
 * Overflow: values in cells that do NOT feed the final answer can be astronomically
 *       large (C(1000, 500) for 1000 identical characters). long is used so moderate
 *       intermediates are exact; Java wraps silently on overflow, and since only
 *       additions are involved and every cell that feeds the answer is <= the answer,
 *       the final value is still exact whenever it fits (see the walkthrough).
 *
 * Time complexity:  O(n * m) — (n+1)(m+1) cells, O(1) each.
 * Space complexity: O(n * m) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long numDistinct(String s, String t) {
        int n = s.length(), m = t.length();
        long[][] dp = new long[n + 1][m + 1];   // dp[i][j] = ways to form t[0..j) from s[0..i)
        for (int i = 0; i <= n; i++) dp[i][0] = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                dp[i][j] = dp[i - 1][j];                                         // skip s[i-1]
                if (s.charAt(i - 1) == t.charAt(j - 1)) dp[i][j] += dp[i - 1][j - 1];   // use it
            }
        }
        return dp[n][m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("rabbbit / rabbit", numDistinct("rabbbit", "rabbit"), 3);
        check("babgbag / bag", numDistinct("babgbag", "bag"), 5);
        check("abc / \"\" (edge: empty t appears once)", numDistinct("abc", ""), 1);
        check("ab / abc (edge: t longer than s)", numDistinct("ab", "abc"), 0);
        check("30 a's / 15 a's = C(30,15)", numDistinct("a".repeat(30), "a".repeat(15)), 155117520);
        // An intermediate cell holds C(40,20) = 137,846,528,820 (> Integer.MAX_VALUE),
        // but it never feeds the final cell, whose true value is 0.
        check("b + 40 a's / 20 a's + b (big intermediate, answer 0)",
                numDistinct("b" + "a".repeat(40), "a".repeat(20) + "b"), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
