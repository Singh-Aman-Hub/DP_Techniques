/**
 * LeetCode 122 Best Time to Buy and Sell Stock II — Tabulation
 *
 * Idea: dp[day][holding] = best profit from 'day' onward. Day n is the base row (all 0),
 * and each day depends only on day + 1, so we fill days from n - 1 down to 0.
 *
 * Time complexity:  O(n) — 2n cells, O(1) each.
 * Space complexity: O(n) — the (n+1) x 2 table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxProfit(int[] prices) {
        int n = prices.length;
        // dp[day][holding]: best profit from 'day' to the end, given the holding status
        int[][] dp = new int[n + 1][2]; // dp[n][*] = 0: no days left
        for (int day = n - 1; day >= 0; day--) {
            dp[day][0] = Math.max(dp[day + 1][0], -prices[day] + dp[day + 1][1]); // rest or buy
            dp[day][1] = Math.max(dp[day + 1][1], prices[day] + dp[day + 1][0]);  // rest or sell
        }
        return dp[0][0];
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
