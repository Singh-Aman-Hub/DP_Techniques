/**
 * Vacation (AtCoder DP C) — Tabulation: dp[day][state]
 *
 * Idea: the states are the three activities. dp[day][s] = most happiness over days 0..day
 *       if the activity on `day` is s. You can enter state s from any OTHER state:
 *         dp[day][s] = happiness[day][s] + max(dp[day-1][t] for t != s).
 *
 * Time complexity:  O(n * 3 * 3) = O(n) — every day, every state looks at 3 previous states.
 * Space complexity: O(n * 3) — the full table.
 *
 * Run: java VacationTabulation.java
 */
public class VacationTabulation {

    static long maxHappiness(int[][] happiness) {
        int days = happiness.length;
        // dp[day][s] = best total for days 0..day when day `day` ends in state s
        long[][] dp = new long[days][3];
        for (int s = 0; s < 3; s++) dp[0][s] = happiness[0][s];   // day 0: no restriction yet

        for (int day = 1; day < days; day++) {
            for (int s = 0; s < 3; s++) {
                long bestPrevious = 0;
                for (int t = 0; t < 3; t++) {
                    if (t != s) bestPrevious = Math.max(bestPrevious, dp[day - 1][t]);   // edge t -> s exists
                }
                dp[day][s] = happiness[day][s] + bestPrevious;
            }
        }
        return Math.max(dp[days - 1][0], Math.max(dp[days - 1][1], dp[days - 1][2]));
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("sample 1 (3 days)", maxHappiness(new int[][]{{10, 40, 70}, {20, 50, 80}, {30, 60, 90}}), 210);
        check("sample 2 (1 day)",  maxHappiness(new int[][]{{100, 10, 1}}), 100);
        check("sample 3 (7 days)", maxHappiness(new int[][]{{6, 7, 8}, {8, 8, 3}, {2, 5, 2}, {7, 8, 6},
                                                             {4, 6, 8}, {2, 3, 4}, {7, 5, 1}}), 46);
        check("2 days, best activity both days is A", maxHappiness(new int[][]{{9, 1, 1}, {9, 1, 1}}), 10);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
