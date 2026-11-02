/**
 * 1335. Minimum Difficulty of a Job Schedule — Tabulation (rolling over days)
 *
 * Idea: dp[day][i] = minimum difficulty to finish jobs 0..i (i inclusive) in exactly `day` days.
 *       dp[1][i] = max(jobs[0..i]).
 *       dp[day][i] = min over s (first job of the last day, day-1 <= s <= i) of
 *                    dp[day-1][s-1] + max(jobs[s..i]).
 *       Scanning s from i downwards keeps max(jobs[s..i]) in one variable.
 *       Each day only reads the previous day, so two rows are enough.
 *
 * Time complexity:  O(n^2 * d) — for each day and each i, scan every start s.
 * Space complexity: O(n) — two rows of length n.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int minDifficulty(int[] jobs, int d) {
        int n = jobs.length;
        if (n < d) return -1;
        // previous[i] = best for jobs 0..i using (day - 1) days
        int[] previous = new int[n];
        previous[0] = jobs[0];
        for (int i = 1; i < n; i++) previous[i] = Math.max(previous[i - 1], jobs[i]);

        for (int day = 2; day <= d; day++) {
            int[] current = new int[n];
            java.util.Arrays.fill(current, INF);
            for (int i = day - 1; i < n; i++) {        // need at least `day` jobs
                int hardestLastDay = 0;
                for (int s = i; s >= day - 1; s--) {   // last day = jobs s..i
                    hardestLastDay = Math.max(hardestLastDay, jobs[s]);
                    current[i] = Math.min(current[i], previous[s - 1] + hardestLastDay);
                }
            }
            previous = current;
        }
        return previous[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[6,5,4,3,2,1], d=2", minDifficulty(new int[]{6, 5, 4, 3, 2, 1}, 2), 7);
        check("[9,9,9], d=4 (edge: impossible)", minDifficulty(new int[]{9, 9, 9}, 4), -1);
        check("[1,1,1], d=3", minDifficulty(new int[]{1, 1, 1}, 3), 3);
        check("[7,1,7,1,7,1], d=3", minDifficulty(new int[]{7, 1, 7, 1, 7, 1}, 3), 15);
        check("[11,111,22,222,33,333,44,444], d=6", minDifficulty(new int[]{11, 111, 22, 222, 33, 333, 44, 444}, 6), 843);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
