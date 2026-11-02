/**
 * LeetCode 122 Best Time to Buy and Sell Stock II — Optimized (two variables)
 *
 * Idea: forward version of the same state machine. free / hold are the best cash
 * balances at the end of the current day while not holding / holding a share.
 * Unlike LC 121, a buy may start from the cash earned by earlier sales: hold = max(hold, free - price).
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProfit(int[] prices) {
        int free = 0;
        int hold = Integer.MIN_VALUE / 2; // impossible before the first day
        for (int price : prices) {
            int newFree = Math.max(free, hold + price);
            int newHold = Math.max(hold, free - price); // buy using earlier profits
            free = newFree;
            hold = newHold;
        }
        return free;
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
