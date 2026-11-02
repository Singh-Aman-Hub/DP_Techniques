/**
 * Coin Change — why GREEDY ("always take the largest coin that fits") is wrong
 *
 * Idea: greedy works for some coin systems (like 1, 2, 5, 10, ...) but not in general.
 * For coins {1, 3, 4} and amount 6, greedy takes 4 + 1 + 1 = 3 coins, but 3 + 3 = 2 coins.
 * For coins {3, 5} and amount 9, greedy takes 5, then cannot pay 4 with a 3 and gives up —
 * although 3 + 3 + 3 works. This file runs greedy next to the DP to show both failures.
 *
 * Time complexity:  greedy O(n log n) for sorting + O(n) for the walk; DP O(n * amount).
 * Space complexity: greedy O(n) for the sorted copy; DP O(amount).
 *
 * Run: java Greedy.java
 */
import java.util.Arrays;

public class Greedy {

    /** Largest coin first, as many times as it fits. Returns -1 if it gets stuck. */
    static int greedyCoins(int[] coins, int amount) {
        int[] sorted = coins.clone();
        Arrays.sort(sorted);
        int count = 0;
        for (int k = sorted.length - 1; k >= 0; k--) {
            count += amount / sorted[k];       // use this coin as often as possible
            amount %= sorted[k];               // what is left to pay
        }
        return (amount == 0) ? count : -1;
    }

    /** The correct DP (same as Optimized.java). */
    static int dpCoins(int[] coins, int amount) {
        int inf = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, inf);
        dp[0] = 0;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) dp[a] = Math.min(dp[a], 1 + dp[a - coin]);
        }
        return (dp[amount] >= inf) ? -1 : dp[amount];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("greedy on [1,3,4], 6 picks 4+1+1", greedyCoins(new int[]{1, 3, 4}, 6), 3);
        check("DP on [1,3,4], 6 finds 3+3", dpCoins(new int[]{1, 3, 4}, 6), 2);
        check("greedy on [3,5], 9 gets stuck after taking 5", greedyCoins(new int[]{3, 5}, 9), -1);
        check("DP on [3,5], 9 finds 3+3+3", dpCoins(new int[]{3, 5}, 9), 3);
        check("greedy on [1,2,5], 11 happens to be right", greedyCoins(new int[]{1, 2, 5}, 11), 3);
        check("DP on [1,2,5], 11 agrees", dpCoins(new int[]{1, 2, 5}, 11), 3);
        check("amount 0 (edge): both say 0", greedyCoins(new int[]{2}, 0) + dpCoins(new int[]{2}, 0), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
