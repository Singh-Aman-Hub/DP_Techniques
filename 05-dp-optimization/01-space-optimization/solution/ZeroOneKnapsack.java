/**
 * 0/1 Knapsack — 2D table reduced to one array with the reverse-loop rule
 *
 * Idea: dp[i][c] = max(dp[i-1][c], dp[i-1][c - w] + v). Both terms come from
 * the PREVIOUS item row, at columns c and c - w (to the left). With one array
 * we must read dp[c - w] before it is overwritten for item i, so capacities are
 * processed from high to low. Looping low to high would read an already-updated
 * cell, i.e. "use item i again", which silently turns 0/1 into unbounded knapsack.
 *
 * Time complexity:  O(n·W) for every version — n items times W+1 capacities, O(1) each.
 * Space complexity: O(n·W) for the table, O(W) for the one-array version.
 *
 * Run: java ZeroOneKnapsack.java
 */
import java.util.Random;

public class ZeroOneKnapsack {

    /** Full-table version. */
    static int knapsackTable(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        // dp[i][c] = best value using only the first i items with total weight <= c
        int[][] dp = new int[n + 1][capacity + 1];
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= capacity; c++) {
                dp[i][c] = dp[i - 1][c];                                     // skip item i
                if (weights[i - 1] <= c)
                    dp[i][c] = Math.max(dp[i][c], dp[i - 1][c - weights[i - 1]] + values[i - 1]); // take it
            }
        }
        return dp[n][capacity];
    }

    /** One-array version: capacities go DOWN so dp[c - w] is still the previous item's value. */
    static int knapsackOneArray(int[] weights, int[] values, int capacity) {
        // dp[c] = best value with weight <= c using the items processed so far
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int c = capacity; c >= weights[i]; c--) {
                dp[c] = Math.max(dp[c], dp[c - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }

    /** The classic BUG, kept for demonstration: a forward loop reuses items (unbounded knapsack). */
    static int knapsackForwardLoopBug(int[] weights, int[] values, int capacity) {
        int[] dp = new int[capacity + 1];
        for (int i = 0; i < weights.length; i++) {
            for (int c = weights[i]; c <= capacity; c++) {
                dp[c] = Math.max(dp[c], dp[c - weights[i]] + values[i]);
            }
        }
        return dp[capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] weights = {1, 3, 4};
        int[] values = {15, 20, 30};
        check("table     W=4", knapsackTable(weights, values, 4), 35);
        check("one array W=4", knapsackOneArray(weights, values, 4), 35);
        // The forward loop takes item 1 four times: 4 * 15 = 60. Wrong for 0/1 knapsack.
        check("forward-loop bug gives unbounded answer", knapsackForwardLoopBug(weights, values, 4), 60);

        int[] w2 = {1, 3, 4, 5};
        int[] v2 = {1, 4, 5, 7};
        check("table     W=7", knapsackTable(w2, v2, 7), 9);
        check("one array W=7", knapsackOneArray(w2, v2, 7), 9);

        check("capacity 0 edge", knapsackOneArray(weights, values, 0), 0);
        check("no items edge", knapsackOneArray(new int[0], new int[0], 10), 0);

        Random random = new Random(11);
        boolean allAgree = true;
        for (int trial = 0; trial < 1000 && allAgree; trial++) {
            int n = random.nextInt(7);
            int[] w = new int[n], v = new int[n];
            for (int i = 0; i < n; i++) { w[i] = 1 + random.nextInt(8); v[i] = random.nextInt(50); }
            int cap = random.nextInt(20);
            allAgree = knapsackTable(w, v, cap) == knapsackOneArray(w, v, cap);
        }
        check("1000 random instances agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
