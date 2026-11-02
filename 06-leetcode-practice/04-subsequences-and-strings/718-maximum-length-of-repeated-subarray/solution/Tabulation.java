/**
 * 718. Maximum Length of Repeated Subarray — Tabulation (bottom-up)
 *
 * Idea: dp[i][j] = longest common block ending exactly at nums1[i-1] and nums2[j-1].
 * Equal values extend the up-left diagonal by one; unequal values reset to 0.
 * Row 0 and column 0 are the empty-prefix base cases. Track the max while filling.
 *
 * Time complexity:  O(n * m) — two nested loops, O(1) work per cell
 * Space complexity: O(n * m) — the full (n+1) x (m+1) table
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findLength(int[] nums1, int[] nums2) {
        int n = nums1.length, m = nums2.length;
        // dp[i][j] = longest common block ending at nums1[i-1] and nums2[j-1]
        int[][] dp = new int[n + 1][m + 1];
        int best = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    best = Math.max(best, dp[i][j]);
                }
                // else dp[i][j] stays 0: a mismatch breaks any contiguous block
            }
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,2,3,2,1] / [3,2,1,4,7]", findLength(new int[]{1, 2, 3, 2, 1}, new int[]{3, 2, 1, 4, 7}), 3);
        check("all zeros", findLength(new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}), 5);
        check("nothing shared (edge)", findLength(new int[]{1, 2}, new int[]{3, 4}), 0);
        check("gap not allowed [1,9,2] / [1,2]", findLength(new int[]{1, 9, 2}, new int[]{1, 2}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
