/**
 * 712. Minimum ASCII Delete Sum for Two Strings — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = cheapest ASCII cost to make the two prefixes equal. Row 0 and
 * column 0 are running ASCII sums. Equal chars copy the diagonal; otherwise
 * take the cheaper of "delete s1's char + up" and "delete s2's char + left".
 *
 * Time complexity:  O(n * m) — nested loops, O(1) per cell
 * Space complexity: O(n * m) — the full table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minimumDeleteSum(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        // dp[i][j] = min ASCII sum of deletions making s1[0..i-1] equal to s2[0..j-1]
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) dp[i][0] = dp[i - 1][0] + s1.charAt(i - 1);
        for (int j = 1; j <= m; j++) dp[0][j] = dp[0][j - 1] + s2.charAt(j - 1);
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                char last1 = s1.charAt(i - 1), last2 = s2.charAt(j - 1);
                if (last1 == last2) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(last1 + dp[i - 1][j], last2 + dp[i][j - 1]);
                }
            }
        }
        return dp[n][m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example sea / eat", minimumDeleteSum("sea", "eat"), 231);
        check("delete / leet", minimumDeleteSum("delete", "leet"), 403);
        check("identical strings", minimumDeleteSum("abc", "abc"), 0);
        check("nothing in common (edge)", minimumDeleteSum("a", "b"), 195);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
