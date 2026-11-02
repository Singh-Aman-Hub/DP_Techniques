/**
 * 0/1 Knapsack — Tabulation (bottom-up DP), the version that solves AtCoder dp_d
 *
 * Idea: dp[i][c] = the best value using only the FIRST i items with a bag of capacity c.
 * Item i-1 is either left out (dp[i-1][c]) or put in, if it fits
 * (value[i-1] + dp[i-1][c - weight[i-1]]). Row 0 (no items) is all zeros.
 *
 * Time complexity:  O(n * W) — (n + 1) * (W + 1) cells, O(1) each. For dp_d: 100 * 100001 ≈ 10^7.
 * Space complexity: O(n * W) — the whole table (about 80 MB of longs for dp_d's limits,
 *                   which is why Optimized.java keeps one row).
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long knapsack(int[] weight, long[] value, int capacity) {
        int n = weight.length;
        // dp[i][c] = best value from the first i items with capacity c
        long[][] dp = new long[n + 1][capacity + 1];       // row 0 stays all 0: no items, no value
        for (int i = 1; i <= n; i++) {
            int w = weight[i - 1];
            long v = value[i - 1];
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c];                      // skip item i-1
                if (w <= c) {
                    dp[i][c] = Math.max(dp[i][c], v + dp[i - 1][c - w]);   // take it (once)
                }
            }
        }
        return dp[n][capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("AtCoder sample 1: W=8, (3,30)(4,50)(5,60)",
                knapsack(new int[]{3, 4, 5}, new long[]{30, 50, 60}, 8), 90);
        check("AtCoder sample 2: W=10^9 is NOT tabulated here; small huge-value case instead",
                knapsack(new int[]{1}, new long[]{1_000_000_000L}, 1), 1_000_000_000L);
        check("AtCoder sample 3: W=15, six items",
                knapsack(new int[]{6, 5, 6, 6, 3, 7}, new long[]{5, 6, 4, 6, 5, 2}, 15), 17);
        check("capacity 0 (edge)", knapsack(new int[]{3, 4, 5}, new long[]{30, 50, 60}, 0), 0);
        check("single item too heavy (edge)", knapsack(new int[]{10}, new long[]{99}, 9), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
