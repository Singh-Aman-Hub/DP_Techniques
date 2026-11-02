/**
 * 983. Minimum Cost For Tickets — Tabulation (bottom-up, over calendar days)
 *
 * Idea: dp[d] = cheapest cost so every travel day <= d is covered. A non-travel day copies
 * dp[d-1]; a travel day takes the cheapest of "last pass was 1, 7 or 30 days ending at d".
 *
 * Time complexity:  O(D) — D = last travel day (<= 365), O(1) work per day.
 * Space complexity: O(D) — the dp and isTravel arrays.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int mincostTickets(int[] days, int[] costs) {
        int lastDay = days[days.length - 1];
        boolean[] isTravel = new boolean[lastDay + 1];
        for (int d : days) isTravel[d] = true;

        // dp[d] = minimum cost to cover all travel days up to and including day d
        int[] dp = new int[lastDay + 1];
        for (int d = 1; d <= lastDay; d++) {
            if (!isTravel[d]) {
                dp[d] = dp[d - 1];                       // nothing new to cover today
                continue;
            }
            int oneDay = dp[d - 1] + costs[0];
            int sevenDay = dp[Math.max(0, d - 7)] + costs[1];
            int thirtyDay = dp[Math.max(0, d - 30)] + costs[2];
            dp[d] = Math.min(oneDay, Math.min(sevenDay, thirtyDay));
        }
        return dp[lastDay];
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
