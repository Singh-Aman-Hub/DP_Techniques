/**
 * 1335. Minimum Difficulty of a Job Schedule — Memoization (partition DP)
 *
 * Idea: Jobs must be done in order, at least one per day, so a schedule is a cut of the array
 *       into exactly d contiguous blocks; a block's cost is its maximum.
 *       f(start, daysLeft) = cheapest way to finish jobs start..n-1 in exactly daysLeft days.
 *       Choice: today's block is start..end; pay max(jobs[start..end]) and recurse on end+1.
 *       Today's block must leave at least one job for each remaining day.
 *
 * Time complexity:  O(n^2 * d) — n*d states, each scans up to n block ends (max kept incrementally).
 * Space complexity: O(n * d) memo plus O(d) recursion depth.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int INF = Integer.MAX_VALUE / 2;
    private static int[] jobs;
    // memo[start][daysLeft] = minimum difficulty for jobs start.. in daysLeft days; -1 = unknown
    private static int[][] memo;

    static int minDifficulty(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (n < d) return -1;                 // some day would have no job
        jobs = jobDifficulty;
        memo = new int[n][d + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(0, d);
    }

    private static int solve(int start, int daysLeft) {
        int n = jobs.length;
        if (daysLeft == 1) {                  // last day takes everything that is left
            int hardest = 0;
            for (int i = start; i < n; i++) hardest = Math.max(hardest, jobs[i]);
            return hardest;
        }
        if (memo[start][daysLeft] != -1) return memo[start][daysLeft];
        int best = INF, hardestToday = 0;
        // leave daysLeft-1 jobs for the remaining days: end <= n - daysLeft
        for (int end = start; end <= n - daysLeft; end++) {
            hardestToday = Math.max(hardestToday, jobs[end]);
            best = Math.min(best, hardestToday + solve(end + 1, daysLeft - 1));
        }
        memo[start][daysLeft] = best;
        return best;
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
