/**
 * Vacation (AtCoder DP C) — Brute force over every activity schedule
 *
 * Idea: each day Taro does activity A, B or C (index 0, 1, 2) and gains happiness[day][activity],
 *       but he may not do the same activity on two consecutive days. Try every
 *       allowed choice for each day, remembering only yesterday's activity (the "state").
 *
 * Time complexity:  O(2^n) — 3 choices on day 0, then 2 allowed choices on each later day.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java VacationBruteForce.java
 */
public class VacationBruteForce {

    static long maxHappiness(int[][] happiness) {
        return best(happiness, 0, -1);                    // -1: no activity yesterday (before day 0)
    }

    /** Most happiness from `day` to the end, given yesterday's activity. */
    private static long best(int[][] happiness, int day, int yesterday) {
        if (day == happiness.length) return 0;
        long result = 0;
        for (int activity = 0; activity < 3; activity++) {
            if (activity == yesterday) continue;           // the forbidden transition
            result = Math.max(result, happiness[day][activity] + best(happiness, day + 1, activity));
        }
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
