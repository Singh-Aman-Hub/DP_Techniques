/**
 * LeetCode 312 Burst Balloons — Tabulation (fill by interval length)
 *
 * Idea: dp[i][j] over the padded array (open interval: i and j are walls, not burst).
 * dp[i][j] reads dp[i][k] and dp[k][j], which are shorter, so we fill by increasing gap j - i.
 *
 * Time complexity:  O(n^3) — about n^2/2 intervals x up to n choices of the last balloon.
 * Space complexity: O(n^2) — the (n+2) x (n+2) table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxCoins(int[] nums) {
        int n = nums.length;
        int[] padded = new int[n + 2];
        padded[0] = 1;
        padded[n + 1] = 1;
        for (int idx = 0; idx < n; idx++) padded[idx + 1] = nums[idx];

        // dp[i][j]: max coins from bursting all balloons strictly between i and j
        int[][] dp = new int[n + 2][n + 2];
        for (int gap = 2; gap <= n + 1; gap++) {        // gap = j - i; gap 1 means empty interval
            for (int i = 0; i + gap <= n + 1; i++) {
                int j = i + gap;
                for (int k = i + 1; k < j; k++) {
                    int coins = dp[i][k] + dp[k][j] + padded[i] * padded[k] * padded[j];
                    dp[i][j] = Math.max(dp[i][j], coins);
                }
            }
        }
        return dp[0][n + 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("nums=[3,1,5,8]", maxCoins(new int[]{3, 1, 5, 8}), 167);
        check("nums=[1,5]", maxCoins(new int[]{1, 5}), 10);
        check("nums=[2,4,3]", maxCoins(new int[]{2, 4, 3}), 33);
        check("edge: one balloon", maxCoins(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
