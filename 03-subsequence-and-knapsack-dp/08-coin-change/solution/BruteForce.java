/**
 * Coin Change (LC 322) — Brute force
 *
 * Idea: minCoins(i, amount) = fewest coins from denominations i..n-1 that add up to exactly
 * amount, with unlimited copies of each. Skip denomination i (move to i+1), or use one coin
 * of it and STAY at i (unbounded knapsack). "Impossible" is represented by INF.
 *
 * Time complexity:  exponential — like unbounded knapsack, the take branch can repeat
 *                   amount / coin times per denomination. Tiny inputs only.
 * Space complexity: O(n + amount) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    // "Infinity" = impossible. Integer.MAX_VALUE would overflow on 1 + INF, so we guard every addition.
    private static final int INF = Integer.MAX_VALUE;

    static int coinChange(int[] coins, int amount) {
        int result = minCoins(coins, 0, amount);
        return (result == INF) ? -1 : result;
    }

    private static int minCoins(int[] coins, int i, int amount) {
        if (amount == 0) return 0;                  // nothing left to pay: zero more coins
        if (i == coins.length) return INF;          // still owe money but no denominations left
        int skip = minCoins(coins, i + 1, amount);
        int take = INF;
        if (coins[i] <= amount) {
            int rest = minCoins(coins, i, amount - coins[i]);
            if (rest != INF) take = 1 + rest;       // only add 1 to a REAL answer, never to INF
        }
        return Math.min(skip, take);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,3,4], amount 6 -> 3+3", coinChange(new int[]{1, 3, 4}, 6), 2);
        check("coins [1,2,5], amount 11 -> 5+5+1", coinChange(new int[]{1, 2, 5}, 11), 3);
        check("coins [5,3], amount 9 -> 3+3+3", coinChange(new int[]{5, 3}, 9), 3);
        check("coins [2], amount 3 (impossible)", coinChange(new int[]{2}, 3), -1);
        check("coins [1], amount 0 (edge: zero coins needed)", coinChange(new int[]{1}, 0), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
