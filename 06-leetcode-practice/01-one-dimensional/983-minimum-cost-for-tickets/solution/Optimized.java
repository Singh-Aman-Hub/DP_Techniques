/**
 * 983. Minimum Cost For Tickets — Space-optimized (30-slot circular buffer)
 *
 * Idea: The calendar recurrence never looks back more than 30 days, so store dp[d] in slot
 * d % 30. dp[d-30] lives in the same slot as dp[d]; we read it before overwriting it.
 *
 * Time complexity:  O(D) — one pass over the calendar up to the last travel day.
 * Space complexity: O(1) — a fixed 30-slot buffer (plus a pointer into the input).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int mincostTickets(int[] days, int[] costs) {
        int[] window = new int[30];      // window[d % 30] = dp[d] for the last 30 days
        int nextTrip = 0;                // index of the next travel day in days[]
        int lastDay = days[days.length - 1];
        for (int d = 1; d <= lastDay; d++) {
            int prev = window[(d - 1) % 30];
            if (d != days[nextTrip]) {
                window[d % 30] = prev;                   // not a travel day
                continue;
            }
            nextTrip++;
            int oneDay = prev + costs[0];
            int sevenDay = (d >= 7 ? window[(d - 7) % 30] : 0) + costs[1];
            int thirtyDay = (d >= 30 ? window[d % 30] : 0) + costs[2];   // slot still holds dp[d-30]
            window[d % 30] = Math.min(oneDay, Math.min(sevenDay, thirtyDay));
        }
        return window[lastDay % 30];
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
