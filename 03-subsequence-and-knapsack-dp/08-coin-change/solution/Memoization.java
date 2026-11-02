/**
 * Coin Change (LC 322) — Memoization
 *
 * Idea: cache minCoins(i, amount). We need a "not computed" marker that differs from both
 * real answers (>= 0) and INF, so the memo is filled with -1.
 *
 * Time complexity:  O(n * amount) — n * (amount + 1) states, O(1) each.
 * Space complexity: O(n * amount) for the memo, plus O(n + amount / smallestCoin) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int INF = Integer.MAX_VALUE;
    // memo[i][a] = fewest coins from coins[i..] summing to a (INF = impossible); -1 = not computed
    private static int[][] memo;

    static int coinChange(int[] coins, int amount) {
        memo = new int[coins.length][amount + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        int result = minCoins(coins, 0, amount);
        return (result == INF) ? -1 : result;
    }

    private static int minCoins(int[] coins, int i, int amount) {
        if (amount == 0) return 0;
        if (i == coins.length) return INF;
        if (memo[i][amount] != -1) return memo[i][amount];
        int skip = minCoins(coins, i + 1, amount);
        int take = INF;
        if (coins[i] <= amount) {
            int rest = minCoins(coins, i, amount - coins[i]);
            if (rest != INF) take = 1 + rest;
        }
        memo[i][amount] = Math.min(skip, take);
        return memo[i][amount];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,3,4], amount 6 -> 3+3", coinChange(new int[]{1, 3, 4}, 6), 2);
        check("coins [1,2,5], amount 11 -> 5+5+1", coinChange(new int[]{1, 2, 5}, 11), 3);
        check("coins [5,3], amount 9 -> 3+3+3", coinChange(new int[]{5, 3}, 9), 3);
        check("coins [2], amount 3 (impossible)", coinChange(new int[]{2}, 3), -1);
        check("coins [1], amount 0 (edge: zero coins needed)", coinChange(new int[]{1}, 0), 0);
        check("coins [186,419,83,408], amount 6249", coinChange(new int[]{186, 419, 83, 408}, 6249), 20);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
