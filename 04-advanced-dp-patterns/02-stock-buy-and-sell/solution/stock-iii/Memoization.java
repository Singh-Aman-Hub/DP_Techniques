/**
 * LeetCode 123 Best Time to Buy and Sell Stock III — Memoization on (day, holding, transactionsLeft)
 *
 * Idea: at most two transactions, so the full state (day, holding, transactionsLeft) is
 * needed. A transaction is counted when we SELL: selling moves transactionsLeft from t to t - 1.
 * When transactionsLeft reaches 0 nothing more can be earned.
 *
 * Time complexity:  O(n * 2 * 3) = O(n) — n days x 2 holding values x 3 counts (0, 1, 2).
 * Space complexity: O(n) — memo table and recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MAX_TRANSACTIONS = 2;
    // memo[day][holding][left]: best profit from 'day' onward; Integer.MIN_VALUE = unknown
    private static int[][][] memo;
    private static int[] prices;

    static int maxProfit(int[] p) {
        prices = p;
        memo = new int[p.length][2][MAX_TRANSACTIONS + 1];
        for (int[][] plane : memo) for (int[] row : plane) Arrays.fill(row, Integer.MIN_VALUE);
        return best(0, 0, MAX_TRANSACTIONS);
    }

    private static int best(int day, int holding, int left) {
        if (day == prices.length || left == 0) return 0;
        if (memo[day][holding][left] != Integer.MIN_VALUE) return memo[day][holding][left];
        int rest = best(day + 1, holding, left);
        int act = (holding == 0)
                ? -prices[day] + best(day + 1, 1, left)        // buy: count unchanged
                : prices[day] + best(day + 1, 0, left - 1);    // sell: one transaction used up
        return memo[day][holding][left] = Math.max(rest, act);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}), 12);
        check("prices=[3,3,5,0,0,3,1,4]", maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}), 6);
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
