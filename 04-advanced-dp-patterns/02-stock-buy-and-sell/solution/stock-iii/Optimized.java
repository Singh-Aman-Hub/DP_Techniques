/**
 * LeetCode 123 Best Time to Buy and Sell Stock III — Optimized (four variables)
 *
 * Idea: run the state machine forward. With k = 2 there are four "holding/after" states:
 *   buy1  = best cash after the 1st buy,   sell1 = best cash after the 1st sell,
 *   buy2  = best cash after the 2nd buy,   sell2 = best cash after the 2nd sell.
 * Each can only be reached from the state before it, so the four values are updated per day.
 * Updating in the order sell2, buy2, sell1, buy1 reads only yesterday's values.
 *
 * Time complexity:  O(n) — one pass, 4 updates per day.
 * Space complexity: O(1).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProfit(int[] prices) {
        int impossible = Integer.MIN_VALUE / 2;
        int buy1 = impossible, sell1 = 0, buy2 = impossible, sell2 = 0;
        for (int price : prices) {
            // Later states first, so each line still sees the previous day's value of its source.
            sell2 = Math.max(sell2, buy2 + price);
            buy2 = Math.max(buy2, sell1 - price);
            sell1 = Math.max(sell1, buy1 + price);
            buy1 = Math.max(buy1, -price);
        }
        return sell2; // sell2 >= sell1 because a "second transaction" may be empty
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
