/**
 * 583. Delete Operation for Two Strings — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = fewest deletions to make the two prefixes equal. Base cases
 * dp[i][0] = i and dp[0][j] = j (delete everything). Equal chars copy the
 * diagonal; otherwise 1 + the cheaper of up and left.
 *
 * Time complexity:  O(n * m) — nested loops, O(1) per cell
 * Space complexity: O(n * m) — the full table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minDistance(String word1, String word2) {
        int n = word1.length(), m = word2.length();
        // dp[i][j] = min deletions so that word1[0..i-1] equals word2[0..j-1]
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[n][m];
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
