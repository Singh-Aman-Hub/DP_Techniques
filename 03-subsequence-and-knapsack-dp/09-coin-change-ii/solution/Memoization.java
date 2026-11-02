/**
 * Coin Change II (LeetCode 518) — Memoization (top-down DP)
 *
 * Idea: The brute force recomputes countWays(index, remaining) many times. There are
 *       only n * (amount + 1) different (index, remaining) pairs, so we store each
 *       answer the first time we compute it and return the stored value afterwards.
 *
 * Time complexity:  O(n * amount) — each of the n * (amount + 1) states is solved once
 *                   with O(1) work (two lookups and an addition).
 * Space complexity: O(n * amount) for the memo table + O(n + amount) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[index][remaining] = number of combinations of coins[index..] summing to remaining;
    // -1 means "not computed yet". long because intermediate counts can exceed int.
    private static long[][] memo;

    static long change(int amount, int[] coins) {
        memo = new long[coins.length][amount + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return countWays(coins, 0, amount);
    }

    private static long countWays(int[] coins, int index, int remaining) {
        if (remaining == 0) return 1;
        if (index == coins.length) return 0;
        if (memo[index][remaining] != -1) return memo[index][remaining];   // cache hit

        long skip = countWays(coins, index + 1, remaining);
        long take = 0;
        if (coins[index] <= remaining) {
            take = countWays(coins, index, remaining - coins[index]);
        }
        memo[index][remaining] = skip + take;
        return memo[index][remaining];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,2,5], amount 5", change(5, new int[]{1, 2, 5}), 4);
        check("coins [1,2], amount 3", change(3, new int[]{1, 2}), 2);
        check("coins [2], amount 3 (impossible)", change(3, new int[]{2}), 0);
        check("coins [10], amount 10", change(10, new int[]{10}), 1);
        check("amount 0 (edge: empty combination)", change(0, new int[]{7}), 1);
        check("coins [1,2,5], amount 100", change(100, new int[]{1, 2, 5}), 541);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
