/**
 * Vacation (AtCoder DP C) — Memoization
 *
 * Idea: best(day, yesterday) depends only on the day and on yesterday's activity,
 *       so there are n * 4 different calls (yesterday in {none, A, B, C}). Cache them.
 *
 * Time complexity:  O(n * 4 * 3) = O(n) — each state tries the 3 activities once.
 * Space complexity: O(n) — the memo table plus recursion depth.
 *
 * Run: java VacationMemoization.java
 */
import java.util.Arrays;

public class VacationMemoization {

    // memo[day][yesterday + 1] = most happiness from day onwards; -1 = unknown (index 0 = "none")
    private static long[][] memo;

    static long maxHappiness(int[][] happiness) {
        memo = new long[happiness.length][4];
        for (long[] row : memo) Arrays.fill(row, -1);
        return best(happiness, 0, -1);
    }

    private static long best(int[][] happiness, int day, int yesterday) {
        if (day == happiness.length) return 0;
        if (memo[day][yesterday + 1] != -1) return memo[day][yesterday + 1];
        long result = 0;
        for (int activity = 0; activity < 3; activity++) {
            if (activity == yesterday) continue;
            result = Math.max(result, happiness[day][activity] + best(happiness, day + 1, activity));
        }
        memo[day][yesterday + 1] = result;
        return result;
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
