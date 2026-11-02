/**
 * 1035. Uncrossed Lines — Tabulation (bottom-up)
 *
 * Idea: classic LCS table. dp[i][j] = max segments for prefixes of length i and j.
 * Equal last values take the diagonal + 1; otherwise take the better of
 * dropping the last value of either array.
 *
 * Time complexity:  O(n * m) — nested loops, O(1) per cell
 * Space complexity: O(n * m) — the (n+1) x (m+1) table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxUncrossedLines(int[] nums1, int[] nums2) {
        int n = nums1.length, m = nums2.length;
        // dp[i][j] = max non-crossing segments using nums1[0..i-1] and nums2[0..j-1]
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[n][m];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,4,2] / [1,2,4]", maxUncrossedLines(new int[]{1, 4, 2}, new int[]{1, 2, 4}), 2);
        check("[2,5,1,2,5] / [10,5,2,1,5,2]", maxUncrossedLines(new int[]{2, 5, 1, 2, 5}, new int[]{10, 5, 2, 1, 5, 2}), 3);
        check("[1,3,7,1,7,5] / [1,9,2,5,1]", maxUncrossedLines(new int[]{1, 3, 7, 1, 7, 5}, new int[]{1, 9, 2, 5, 1}), 2);
        check("single, no match (edge)", maxUncrossedLines(new int[]{1}, new int[]{2}), 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
