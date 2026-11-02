/**
 * LeetCode 309 Best Time to Buy and Sell Stock with Cooldown — Memoization on (day, holding)
 *
 * Idea: same as LC 122 (unlimited transactions, state = (day, holding)), with ONE change
 * in the transition: after selling on 'day' we cannot buy on day + 1, so selling jumps
 * straight to day + 2 in the not-holding state.
 *
 * Time complexity:  O(n) — 2n states, O(1) work each.
 * Space complexity: O(n) — memo table and recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[day][holding]: best profit from 'day' onward; Integer.MIN_VALUE = unknown
    private static int[][] memo;
    private static int[] prices;

    static int maxProfit(int[] p) {
        prices = p;
        memo = new int[p.length][2];
        for (int[] row : memo) Arrays.fill(row, Integer.MIN_VALUE);
        return best(0, 0);
    }

    private static int best(int day, int holding) {
        if (day >= prices.length) return 0; // day may jump past the end after a sell
        if (memo[day][holding] != Integer.MIN_VALUE) return memo[day][holding];
        int rest = best(day + 1, holding);
        int act = (holding == 0)
                ? -prices[day] + best(day + 1, 1)
                : prices[day] + best(day + 2, 0); // the cooldown: skip the next day
        return memo[day][holding] = Math.max(rest, act);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}), 8);
        check("prices=[1,2,3,0,2]", maxProfit(new int[]{1, 2, 3, 0, 2}), 3);
        check("prices=[3,3,5,0,0,3,1,4]", maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}), 6);
        check("falling prices", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
        check("edge: one day", maxProfit(new int[]{1}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
