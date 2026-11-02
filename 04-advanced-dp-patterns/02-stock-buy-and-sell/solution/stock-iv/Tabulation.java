/**
 * LeetCode 188 Best Time to Buy and Sell Stock IV — Tabulation
 *
 * Idea: identical to LC 123 with the constant 2 replaced by the input k.
 * dp[day][holding][left] = best profit from 'day' onward; a sell consumes one transaction.
 *
 * Time complexity:  O(n * k) — n x 2 x (k+1) cells, O(1) each.
 * Space complexity: O(n * k) — the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxProfit(int k, int[] prices) {
        int n = prices.length;
        // dp[day][holding][left]: best profit from 'day' on with 'left' sells still allowed
        int[][][] dp = new int[n + 1][2][k + 1];
        for (int day = n - 1; day >= 0; day--) {
            for (int left = 1; left <= k; left++) {
                dp[day][0][left] = Math.max(dp[day + 1][0][left], -prices[day] + dp[day + 1][1][left]);
                dp[day][1][left] = Math.max(dp[day + 1][1][left], prices[day] + dp[day + 1][0][left - 1]);
            }
        }
        return dp[0][0][k];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("k=2 prices=[1,3,2,8,4,9]", maxProfit(2, new int[]{1, 3, 2, 8, 4, 9}), 12);
        check("k=3 prices=[1,3,2,8,4,9]", maxProfit(3, new int[]{1, 3, 2, 8, 4, 9}), 13);
        check("k=1 prices=[1,3,2,8,4,9]", maxProfit(1, new int[]{1, 3, 2, 8, 4, 9}), 8);
        check("k=2 prices=[2,4,1]", maxProfit(2, new int[]{2, 4, 1}), 2);
        check("k=2 prices=[3,2,6,5,0,3]", maxProfit(2, new int[]{3, 2, 6, 5, 0, 3}), 7);
        check("edge: k=0", maxProfit(0, new int[]{1, 5}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
