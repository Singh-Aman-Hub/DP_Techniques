/**
 * LeetCode 309 Best Time to Buy and Sell Stock with Cooldown — Optimized (three variables)
 *
 * Idea: forward state machine with an extra "cooling" state:
 *   hold    = best cash while owning a share,
 *   cooling = best cash on the day right after a sale (cannot buy today),
 *   free    = best cash while empty-handed and allowed to buy.
 * Sell: hold -> cooling.  Cooling always moves to free the next day.  Buy only from free.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxProfit(int[] prices) {
        int hold = Integer.MIN_VALUE / 2, cooling = Integer.MIN_VALUE / 2, free = 0;
        for (int price : prices) {
            int newHold = Math.max(hold, free - price);  // keep holding, or buy (only from free)
            int newCooling = hold + price;               // sold today -> forced rest tomorrow
            int newFree = Math.max(free, cooling);       // stay free, or finish yesterday's cooldown
            hold = newHold;
            cooling = newCooling;
            free = newFree;
        }
        return Math.max(free, cooling);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("prices=[1,3,2,8,4,9]", maxProfit(new int[]{1, 3, 2, 8, 4, 9}), 8);
        check("prices=[1,2,3,0,2]", maxProfit(new int[]{1, 2, 3, 0, 2}), 3);
        check("prices=[3,3,5,0,0,3,1,4]", maxProfit(new int[]{3, 3, 5, 0, 0, 3, 1, 4}), 6);
        check("falling prices", maxProfit(new int[]{7, 6, 4, 3, 1}), 0);
        check("edge: one day", maxProfit(new int[]{1}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
