/**
 * 0/1 Knapsack — Space optimization: two rows, then one row with a REVERSE capacity loop
 *
 * Idea: row i of the table reads only row i-1, so two arrays suffice (prev, curr).
 * Going further, one array works if we update capacities from HIGH to LOW: when we write
 * dp[c], the cell dp[c - w] we read is to the LEFT and has not been overwritten yet, so it
 * still holds the "previous item" value. Looping LOW to HIGH would read an already-updated
 * cell and silently let the same item be taken twice (see forwardLoopBug below).
 *
 * Time complexity:  O(n * W) — unchanged; we still visit every (item, capacity) pair once.
 * Space complexity: O(W) — two rows of W + 1 longs, then just one.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    /** Two arrays: prev = row i-1, curr = row i. */
    static long knapsackTwoRows(int[] weight, long[] value, int capacity) {
        long[] prev = new long[capacity + 1];             // row 0: no items, all zeros
        for (int i = 0; i < weight.length; i++) {
            long[] curr = new long[capacity + 1];
            for (int c = 0; c <= capacity; c++) {
                curr[c] = prev[c];
                if (weight[i] <= c) curr[c] = Math.max(curr[c], value[i] + prev[c - weight[i]]);
            }
            prev = curr;
        }
        return prev[capacity];
    }

    /** One array, capacities processed from high to low. */
    static long knapsackOneRow(int[] weight, long[] value, int capacity) {
        // dp[c] = best value with capacity c using the items processed so far
        long[] dp = new long[capacity + 1];
        for (int i = 0; i < weight.length; i++) {
            // Stop at c = weight[i]: smaller capacities cannot fit this item, so they keep the old value.
            for (int c = capacity; c >= weight[i]; c--) {
                // dp[c - weight[i]] is still the value WITHOUT item i, because we have not reached it yet
                dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
            }
        }
        return dp[capacity];
    }

    /** WRONG on purpose: forward loop lets item i be used again. Kept to demonstrate the bug. */
    static long forwardLoopBug(int[] weight, long[] value, int capacity) {
        long[] dp = new long[capacity + 1];
        for (int i = 0; i < weight.length; i++) {
            for (int c = weight[i]; c <= capacity; c++) {
                // dp[c - weight[i]] may ALREADY include item i (it was updated earlier in this loop)
                dp[c] = Math.max(dp[c], value[i] + dp[c - weight[i]]);
            }
        }
        return dp[capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] w1 = {3, 4, 5};
        long[] v1 = {30, 50, 60};
        int[] w3 = {6, 5, 6, 6, 3, 7};
        long[] v3 = {5, 6, 4, 6, 5, 2};

        check("two rows, sample 1", knapsackTwoRows(w1, v1, 8), 90);
        check("two rows, sample 3", knapsackTwoRows(w3, v3, 15), 17);
        check("one row, sample 1", knapsackOneRow(w1, v1, 8), 90);
        check("one row, sample 3", knapsackOneRow(w3, v3, 15), 17);
        check("one row, capacity 0 (edge)", knapsackOneRow(w1, v1, 0), 0);
        check("one row, single heavy item (edge)", knapsackOneRow(new int[]{10}, new long[]{99}, 9), 0);
        // The forward loop takes the weight-4 item twice: 50 + 50 = 100, which is NOT a legal 0/1 answer.
        check("forward-loop bug really gives the wrong 100 on sample 1", forwardLoopBug(w1, v1, 8), 100);
        // Value-heavy, capacity-light input in the style of AtCoder sample 2
        check("one row, one item of weight 1 and value 10^9",
                knapsackOneRow(new int[]{1}, new long[]{1_000_000_000L}, 1), 1_000_000_000L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
