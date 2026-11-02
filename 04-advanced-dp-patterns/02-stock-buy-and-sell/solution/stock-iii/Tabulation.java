/**
 * LeetCode 123 Best Time to Buy and Sell Stock III — Tabulation
 *
 * Idea: dp[day][holding][left] = best profit from 'day' onward. Base: day n (no days left)
 * and left = 0 (no transactions left) are 0. Each day reads only day + 1, so fill days backwards.
 *
 * Time complexity:  O(n * 2 * 3) = O(n).
 * Space complexity: O(n) — the (n+1) x 2 x 3 table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxProfit(int[] prices) {
        int n = prices.length, k = 2;
        // dp[day][holding][left]: best profit from 'day' on with 'left' sells still allowed
        int[][][] dp = new int[n + 1][2][k + 1]; // Java zero-fills: all base cases are 0
        for (int day = n - 1; day >= 0; day--) {
            for (int left = 1; left <= k; left++) { // left = 0 stays 0
                dp[day][0][left] = Math.max(dp[day + 1][0][left], -prices[day] + dp[day + 1][1][left]);
                dp[day][1][left] = Math.max(dp[day + 1][1][left], prices[day] + dp[day + 1][0][left - 1]);
            }
        }
        return dp[0][0][k];
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
