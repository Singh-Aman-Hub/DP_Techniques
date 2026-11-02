/**
 * Unbounded Knapsack — Memoization
 *
 * Idea: cache best(i, capacity). The only change from 0/1 knapsack is that "take" recurses
 * on (i, capacity - weight[i]) instead of (i + 1, ...). The state count is the same.
 *
 * Time complexity:  O(n * W) — n * (W + 1) states, O(1) work each (two choices).
 * Space complexity: O(n * W) for the memo, plus O(n + W) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][c] = best value from item types i..n-1 with capacity c; -1 = not computed
    private static long[][] memo;

    static long knapsack(int[] weight, long[] value, int capacity) {
        memo = new long[weight.length][capacity + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return best(weight, value, 0, capacity);
    }

    private static long best(int[] weight, long[] value, int i, int capacity) {
        if (i == weight.length) return 0;
        if (memo[i][capacity] != -1) return memo[i][capacity];
        long skip = best(weight, value, i + 1, capacity);
        long take = Long.MIN_VALUE;
        if (weight[i] <= capacity) {
            take = value[i] + best(weight, value, i, capacity - weight[i]);
        }
        memo[i][capacity] = Math.max(skip, take);
        return memo[i][capacity];
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
