/**
 * Coin Change (LC 322) — One array, forward loop (also solves CSES 1634 Minimizing Coins)
 *
 * Idea: dp[a] = fewest coins that pay exactly a. Coins are unlimited, so for each coin the
 * amount loop runs FORWARD (dp[a - coin] may already use this coin — that is allowed).
 * Equivalent view: dp[a] = 1 + min over coins of dp[a - coin].
 *
 * Time complexity:  O(n * amount) — each coin sweeps the amounts once.
 *                   CSES 1634: 100 coins * 10^6 = 10^8 simple steps, fine in Java.
 * Space complexity: O(amount) — one int array.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int coinChange(int[] coins, int amount) {
        int inf = amount + 1;
        // dp[a] = fewest coins (from the coins processed so far) summing to a; inf = impossible
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, inf);
        dp[0] = 0;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {          // forward: unlimited copies of coin
                dp[a] = Math.min(dp[a], 1 + dp[a - coin]);  // inf + 1 = amount + 2: still no overflow
            }
        }
        return (dp[amount] >= inf) ? -1 : dp[amount];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("coins [1,3,4], amount 6 -> 3+3", coinChange(new int[]{1, 3, 4}, 6), 2);
        check("coins [1,2,5], amount 11 -> 5+5+1", coinChange(new int[]{1, 2, 5}, 11), 3);
        check("coins [5,3], amount 9 -> 3+3+3", coinChange(new int[]{5, 3}, 9), 3);
        check("coins [2], amount 3 (impossible)", coinChange(new int[]{2}, 3), -1);
        check("coins [1], amount 0 (edge: zero coins needed)", coinChange(new int[]{1}, 0), 0);
        check("coins [186,419,83,408], amount 6249", coinChange(new int[]{186, 419, 83, 408}, 6249), 20);
        check("CSES sample: coins [1,5,7], sum 11 -> 5+5+1", coinChange(new int[]{1, 5, 7}, 11), 3);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
