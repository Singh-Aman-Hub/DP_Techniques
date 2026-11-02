/**
 * LeetCode 121 Best Time to Buy and Sell Stock — Memoization on (day, holding)
 *
 * Idea: the general stock state is (day, holding, transactionsLeft). With exactly one
 * transaction allowed, transactionsLeft is fixed at 1 and selling ends the game, so the
 * state shrinks to (day, holding). Each day we either do nothing, or perform the one
 * action allowed by our holding status: buy (if empty-handed) or sell (if holding).
 *
 * Time complexity:  O(n) — 2n states, O(1) work each.
 * Space complexity: O(n) — memo table of size n x 2 and recursion depth n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[day][holding]: best profit obtainable from 'day' onward; Integer.MIN_VALUE = unknown
    private static int[][] memo;
    private static int[] prices;

    static int maxProfit(int[] p) {
        prices = p;
        memo = new int[p.length][2];
        for (int[] row : memo) Arrays.fill(row, Integer.MIN_VALUE);
        return best(0, 0);
    }

    private static int best(int day, int holding) {
        if (day == prices.length) return 0; // no days left: nothing more to earn
        if (memo[day][holding] != Integer.MIN_VALUE) return memo[day][holding];
        int rest = best(day + 1, holding); // choice: do nothing today
        int act;
        if (holding == 0) {
            act = -prices[day] + best(day + 1, 1); // buy: pay today's price
        } else {
            act = prices[day]; // sell: receive the price; our only transaction is now finished
        }
        return memo[day][holding] = Math.max(rest, act);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}), 8);
        check("prices=[7,1,5,3,6,4]", maxProfit(new int[]{7, 1, 5, 3, 6, 4}), 5);
        check("prices=[3,3,5,0,0,3,1,4]", maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}), 4);
        check("falling prices", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
        check("edge: one day", maxProfit(new int[]{5}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
