/**
 * LeetCode 1937 — Maximum Number of Points with Cost — slow O(m·n²) DP and prefix/suffix-max O(m·n) DP
 *
 * Idea: dp[r][c] = best score of picking one cell in each of rows 0..r, ending at column c.
 *       Slow: dp[r][c] = points[r][c] + max over c' of (dp[r-1][c'] - |c - c'|).
 *       Split the max by which side c' lies on:
 *         c' <= c : dp[r-1][c'] + c' - c  -> a PREFIX maximum, swept left to right
 *         c' >= c : dp[r-1][c'] - c' + c  -> a SUFFIX maximum, swept right to left
 *       The sweep "left[c] = max(left[c-1] - 1, prev[c])" charges 1 point per step moved.
 *
 * Time complexity:  slow: O(m·n²) — m·n cells, each scanning n columns.
 *                   fast: O(m·n) — per row, two O(n) sweeps plus O(1) per cell.
 * Space complexity: O(n) — previous row plus the left/right sweep arrays.
 *
 * Run: java MaxPointsWithCost.java
 */
import java.util.Random;

public class MaxPointsWithCost {

    static long slow(int[][] points) {
        int cols = points[0].length;
        long[] prev = new long[cols];
        for (int c = 0; c < cols; c++) prev[c] = points[0][c];
        for (int r = 1; r < points.length; r++) {
            long[] cur = new long[cols];
            for (int c = 0; c < cols; c++) {
                long bestAbove = Long.MIN_VALUE;
                for (int c2 = 0; c2 < cols; c2++) {
                    bestAbove = Math.max(bestAbove, prev[c2] - Math.abs(c - c2));
                }
                cur[c] = points[r][c] + bestAbove;
            }
            prev = cur;
        }
        long answer = Long.MIN_VALUE;
        for (long value : prev) answer = Math.max(answer, value);
        return answer;
    }

    static long fast(int[][] points) {
        int cols = points[0].length;
        long[] prev = new long[cols];
        for (int c = 0; c < cols; c++) prev[c] = points[0][c];
        // left[c]  = max over c' <= c of prev[c'] - (c - c')
        // right[c] = max over c' >= c of prev[c'] - (c' - c)
        long[] left = new long[cols], right = new long[cols];
        for (int r = 1; r < points.length; r++) {
            left[0] = prev[0];
            for (int c = 1; c < cols; c++) left[c] = Math.max(left[c - 1] - 1, prev[c]);
            right[cols - 1] = prev[cols - 1];
            for (int c = cols - 2; c >= 0; c--) right[c] = Math.max(right[c + 1] - 1, prev[c]);
            long[] cur = new long[cols];
            for (int c = 0; c < cols; c++) cur[c] = points[r][c] + Math.max(left[c], right[c]);
            prev = cur;
        }
        long answer = Long.MIN_VALUE;
        for (long value : prev) answer = Math.max(answer, value);
        return answer;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example1 = {{1, 2, 3}, {1, 5, 1}, {3, 1, 1}};   // 3 + 5 + 3 - 1 - 1 = 9
        check("slow example1", slow(example1), 9);
        check("fast example1", fast(example1), 9);
        int[][] example2 = {{1, 5}, {2, 3}, {4, 2}};             // 5 + 3 + 4 - 0 - 1 = 11
        check("fast example2", fast(example2), 11);
        check("fast single cell (edge)", fast(new int[][]{{4}}), 4);

        Random random = new Random(1937);
        int mismatches = 0;
        for (int trial = 0; trial < 500; trial++) {
            int rows = 1 + random.nextInt(5), cols = 1 + random.nextInt(6);
            int[][] points = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) points[r][c] = random.nextInt(10);
            if (slow(points) != fast(points)) mismatches++;
        }
        check("500 random grids: mismatches slow vs fast", mismatches, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
