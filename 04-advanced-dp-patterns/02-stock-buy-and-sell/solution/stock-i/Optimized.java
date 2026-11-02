/**
 * LeetCode 121 Best Time to Buy and Sell Stock — Optimized (two variables)
 *
 * Idea: roll the (day, holding) table forward and keep only today's two values:
 *   hold = best cash balance if we currently own the share (we bought it at some point),
 *   free = best cash balance if we do not own a share.
 * Because only ONE buy is allowed, buying always starts from 0 cash: hold = max(hold, -price).
 * This is the familiar "minimum price so far" trick written as a state machine.
 *
 * Time complexity:  O(n) — one pass over the prices.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProfit(int[] prices) {
        int hold = Integer.MIN_VALUE / 2; // cannot hold anything before day 0
        int free = 0;                     // empty-handed with zero profit
        for (int price : prices) {
            int newFree = Math.max(free, hold + price); // rest, or sell today
            int newHold = Math.max(hold, -price);       // rest, or buy today (first and only buy)
            free = newFree;
            hold = newHold;
        }
        return free; // ending while still holding a share is never better
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
