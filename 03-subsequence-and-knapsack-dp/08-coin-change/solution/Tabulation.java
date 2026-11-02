/**
 * Coin Change (LC 322) — Tabulation
 *
 * Idea: dp[i][a] = fewest coins using the first i denominations to pay exactly a.
 *   dp[0][0] = 0, dp[0][a > 0] = INF (no coins cannot pay a positive amount)
 *   dp[i][a] = min( dp[i-1][a],  1 + dp[i][a - coin] )   — same row for "one more of this coin"
 * INF is amount + 1: no real answer can use more than amount coins (every coin is >= 1),
 * and amount + 1 + 1 cannot overflow an int, so no special guard is needed.
 *
 * Time complexity:  O(n * amount) — every cell once.
 * Space complexity: O(n * amount) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static int coinChange(int[] coins, int amount) {
        int n = coins.length;
        int inf = amount + 1;                                  // safe sentinel: bigger than any real answer
        // dp[i][a] = fewest coins from coins[0..i-1] that sum to exactly a (inf = impossible)
        int[][] dp = new int[n + 1][amount + 1];
        Arrays.fill(dp[0], inf);
        dp[0][0] = 0;
        for (int i = 1; i <= n; i++) {
            int coin = coins[i - 1];
            for (int a = 0; a <= amount; a++) {
                dp[i][a] = dp[i - 1][a];                                      // never use this coin
                if (coin <= a) dp[i][a] = Math.min(dp[i][a], 1 + dp[i][a - coin]);  // one more of it
            }
        }
        return (dp[n][amount] >= inf) ? -1 : dp[n][amount];
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
