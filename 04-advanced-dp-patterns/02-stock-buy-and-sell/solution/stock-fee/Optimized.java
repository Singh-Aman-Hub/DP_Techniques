/**
 * LeetCode 714 Best Time to Buy and Sell Stock with Transaction Fee — Optimized (two variables)
 *
 * Idea: LC 122's forward state machine; the sell edge now earns price - fee.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProfit(int[] prices, int fee) {
        int free = 0, hold = Integer.MIN_VALUE / 2;
        for (int price : prices) {
            int newFree = Math.max(free, hold + price - fee); // sell and pay the fee
            int newHold = Math.max(hold, free - price);
            free = newFree;
            hold = newHold;
        }
        return free;
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
