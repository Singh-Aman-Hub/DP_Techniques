/**
 * LeetCode 714 Best Time to Buy and Sell Stock with Transaction Fee — Memoization on (day, holding)
 *
 * Idea: same as LC 122, with ONE change in the transition: every completed transaction
 * costs 'fee', which we subtract when we sell.
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
    private static int fee;

    static int maxProfit(int[] p, int transactionFee) {
        prices = p;
        fee = transactionFee;
        memo = new int[p.length][2];
        for (int[] row : memo) Arrays.fill(row, Integer.MIN_VALUE);
        return best(0, 0);
    }

    private static int best(int day, int holding) {
        if (day == prices.length) return 0;
        if (memo[day][holding] != Integer.MIN_VALUE) return memo[day][holding];
        int rest = best(day + 1, holding);
        int act = (holding == 0)
                ? -prices[day] + best(day + 1, 1)
                : prices[day] - fee + best(day + 1, 0); // pay the fee once per transaction
        return memo[day][holding] = Math.max(rest, act);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("fee=2 prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}, 2), 8);
        check("fee=3 prices=[1,3,7,5,10,3]", maxProfit(new int[]{1, 3, 7, 5, 10, 3}, 3), 6);
        check("fee=0 behaves like LC 122", maxProfit(new int[]{1, 3, 2, 8, 4, 9}, 0), 13);
        check("edge: one day", maxProfit(new int[]{4}, 1), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
