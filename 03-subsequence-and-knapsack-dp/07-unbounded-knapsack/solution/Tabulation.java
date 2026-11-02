/**
 * Unbounded Knapsack — Tabulation
 *
 * Idea: dp[i][c] = best value using item types 0..i-1 (each any number of times) with capacity c.
 *   dp[i][c] = max( dp[i-1][c],                          // use no (more) copies of type i-1
 *                   value[i-1] + dp[i][c - weight[i-1]] ) // one more copy: SAME row i
 * Reading the same row is the table form of "stay at index i after taking".
 *
 * Time complexity:  O(n * W) — (n + 1) * (W + 1) cells, O(1) each.
 * Space complexity: O(n * W) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long knapsack(int[] weight, long[] value, int capacity) {
        int n = weight.length;
        // dp[i][c] = best value from the first i item types (unlimited copies) with capacity c
        long[][] dp = new long[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            int w = weight[i - 1];
            for (int c = 0; c <= capacity; c++) {          // left to right: dp[i][c - w] is already final
                dp[i][c] = dp[i - 1][c];
                if (w <= c) dp[i][c] = Math.max(dp[i][c], value[i - 1] + dp[i][c - w]);
            }
        }
        return dp[n][capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] w = {1, 3, 4};
        long[] v = {15, 50, 60};
        check("W=8, (1,15)(3,50)(4,60): 3+3+1+1 -> 130", knapsack(w, v, 8), 130);
        check("W=7: 3+3+1 -> 115", knapsack(w, v, 7), 115);
        check("one item (2,3), W=7: three copies -> 9", knapsack(new int[]{2}, new long[]{3}, 7), 9);
        check("capacity 0 (edge)", knapsack(w, v, 0), 0);
        check("only item too heavy (edge)", knapsack(new int[]{5}, new long[]{10}, 4), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
