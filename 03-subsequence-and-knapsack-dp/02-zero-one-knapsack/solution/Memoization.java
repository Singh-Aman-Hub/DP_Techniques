/**
 * 0/1 Knapsack — Memoization (top-down DP)
 *
 * Idea: the brute-force function best(i, capacity) only depends on (i, capacity), and there
 * are only n * (W + 1) such pairs, while there are 2^n paths. Many paths arrive at the same
 * pair (for example "took item 0 of weight 3" vs "took items 1 and 2 of weights 1 and 2"),
 * so we cache each pair's answer the first time we compute it.
 *
 * Time complexity:  O(n * W) — n * (W + 1) states, each solved once with O(1) work.
 * Space complexity: O(n * W) for the memo, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][c] = best value from items i..n-1 with remaining capacity c; -1 = not computed
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
            take = value[i] + best(weight, value, i + 1, capacity - weight[i]);
        }
        memo[i][capacity] = Math.max(skip, take);
        return memo[i][capacity];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("AtCoder sample 1: W=8, (3,30)(4,50)(5,60)",
                knapsack(new int[]{3, 4, 5}, new long[]{30, 50, 60}, 8), 90);
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
