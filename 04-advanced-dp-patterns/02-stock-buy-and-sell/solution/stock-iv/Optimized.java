/**
 * LeetCode 188 Best Time to Buy and Sell Stock IV — Optimized (two arrays of size k+1)
 *
 * Idea: the forward state machine of LC 123 generalised to k transactions:
 *   buy[t]  = best cash after the t-th buy,  sell[t] = best cash after the t-th sell.
 * Day by day: sell[t] = max(sell[t], buy[t] + price), buy[t] = max(buy[t], sell[t-1] - price).
 * Shortcut: if k >= n/2 the limit can never bind (each transaction needs 2 days),
 * so we answer with the unlimited LC 122 greedy.
 *
 * Time complexity:  O(n * k) — k updates per day (O(n) in the shortcut case).
 * Space complexity: O(k) — two arrays.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (k >= n / 2) { // limit cannot bind: take every upward step (LC 122)
            int profit = 0;
            for (int day = 1; day < n; day++) {
                profit += Math.max(0, prices[day] - prices[day - 1]);
            }
            return profit;
        }
        int[] buy = new int[k + 1];  // buy[t]: best cash after the t-th buy (t >= 1)
        int[] sell = new int[k + 1]; // sell[t]: best cash after the t-th sell; sell[0] = 0
        Arrays.fill(buy, Integer.MIN_VALUE / 2);
        for (int price : prices) {
            for (int t = k; t >= 1; t--) { // high t first so sell[t-1] is still yesterday's
                sell[t] = Math.max(sell[t], buy[t] + price);
                buy[t] = Math.max(buy[t], sell[t - 1] - price);
            }
        }
        return sell[k];
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
