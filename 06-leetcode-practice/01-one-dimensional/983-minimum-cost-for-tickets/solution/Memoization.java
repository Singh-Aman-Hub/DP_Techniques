/**
 * 983. Minimum Cost For Tickets — Memoization (top-down)
 *
 * Idea: cover(i) = cheapest way to cover travel days days[i..]. On day days[i] we must buy one
 * of three passes; it covers every travel day before days[i] + duration, and we recurse on the
 * first travel day it does not cover. Cache by travel-day index.
 *
 * Time complexity:  O(n * 30) — n states; for each pass we scan at most 30 travel days ahead.
 * Space complexity: O(n) — memo array plus recursion depth up to n.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int[] DURATION = {1, 7, 30};
    private static int[] days;
    private static int[] costs;
    private static int[] memo;    // memo[i] = cheapest cost to cover days[i..]; -1 = unknown

    static int mincostTickets(int[] travelDays, int[] passCosts) {
        days = travelDays;
        costs = passCosts;
        memo = new int[days.length];
        Arrays.fill(memo, -1);
        return cover(0);
    }

    private static int cover(int i) {
        if (i == days.length) return 0;                  // every travel day is covered
        if (memo[i] != -1) return memo[i];
        int best = Integer.MAX_VALUE;
        for (int t = 0; t < 3; t++) {
            int firstUncovered = i;
            while (firstUncovered < days.length && days[firstUncovered] < days[i] + DURATION[t]) {
                firstUncovered++;
            }
            best = Math.min(best, costs[t] + cover(firstUncovered));
        }
        memo[i] = best;
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] prices = {2, 7, 15};
        check("example 1", mincostTickets(new int[]{1, 4, 6, 7, 8, 20}, prices), 11);
        check("example 2", mincostTickets(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 30, 31}, prices), 17);
        check("single day (edge)", mincostTickets(new int[]{1}, prices), 2);
        check("one full week", mincostTickets(new int[]{1, 2, 3, 4, 5, 6, 7}, prices), 7);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
