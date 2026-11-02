/**
 * Vacation (AtCoder DP C) — Optimized: three variables instead of a table
 *
 * Idea: day `day` reads only day `day - 1`, so keep one value per state:
 *       a, b, c = best totals so far ending in activity A, B, C.
 *       Compute all three new values from the OLD ones before overwriting.
 *
 * Time complexity:  O(n) — constant work per day.
 * Space complexity: O(1) — three running values.
 *
 * Run: java VacationOptimized.java
 */
public class VacationOptimized {

    static long maxHappiness(int[][] happiness) {
        long a = happiness[0][0], b = happiness[0][1], c = happiness[0][2];
        for (int day = 1; day < happiness.length; day++) {
            long newA = happiness[day][0] + Math.max(b, c);   // A can follow B or C
            long newB = happiness[day][1] + Math.max(a, c);   // B can follow A or C
            long newC = happiness[day][2] + Math.max(a, b);   // C can follow A or B
            a = newA;                                         // overwrite only after all three are computed
            b = newB;
            c = newC;
        }
        return Math.max(a, Math.max(b, c));
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
