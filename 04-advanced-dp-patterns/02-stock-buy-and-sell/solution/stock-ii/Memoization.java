/**
 * LeetCode 122 Best Time to Buy and Sell Stock II — Memoization on (day, holding)
 *
 * Idea: unlimited transactions means transactionsLeft never runs out, so we drop it from
 * the state. The only change from LC 121: after selling we return to (day + 1, not holding)
 * and may buy again, instead of stopping.
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
        if (day == prices.length) return 0;
        if (memo[day][holding] != Integer.MIN_VALUE) return memo[day][holding];
        int rest = best(day + 1, holding);
        int act = (holding == 0)
                ? -prices[day] + best(day + 1, 1)   // buy
                : prices[day] + best(day + 1, 0);   // sell, then we are free to buy again
        return memo[day][holding] = Math.max(rest, act);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}), 13);
        check("prices=[7,1,5,3,6,4]", maxProfit(new int[]{7, 1, 5, 3, 6, 4}), 7);
        check("prices=[1,2,3,4,5]", maxProfit(new int[]{1, 2, 3, 4, 5}), 4);
        check("falling prices", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
        check("edge: one day", maxProfit(new int[]{5}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
