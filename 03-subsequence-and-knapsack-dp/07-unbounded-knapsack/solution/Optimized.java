/**
 * Unbounded Knapsack — One array with a FORWARD capacity loop
 *
 * Idea: same as 0/1 knapsack's one-row version, but capacities go from LOW to HIGH. When we
 * compute dp[c], the cell dp[c - w] was already updated for this item type, so it may
 * already contain copies of it — which is exactly what "unlimited copies" means.
 * The forward loop that was a BUG for 0/1 knapsack is the CORRECT loop here.
 *
 * Time complexity:  O(n * W) — every (type, capacity) pair once.
 * Space complexity: O(W) — one array.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long knapsack(int[] weight, long[] value, int capacity) {
        // dp[c] = best value with capacity c using the item types processed so far (unlimited copies)
        long[] dp = new long[capacity + 1];
        for (int i = 0; i < weight.length; i++) {
            for (int c = weight[i]; c <= capacity; c++) {          // forward: reuse is allowed
                dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
            }
        }
        return dp[capacity];
    }

    /** 0/1 knapsack on the same data, for contrast: the only difference is the loop direction. */
    static long zeroOneKnapsack(int[] weight, long[] value, int capacity) {
        long[] dp = new long[capacity + 1];
        for (int i = 0; i < weight.length; i++) {
            for (int c = capacity; c >= weight[i]; c--) {          // backward: each item at most once
                dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
            }
        }
        return dp[capacity];
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
        check("same items as 0/1 knapsack (each at most once): 1+3+4 -> 125", zeroOneKnapsack(w, v, 8), 125);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
