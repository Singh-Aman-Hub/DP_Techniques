/**
 * LeetCode 1289 — Minimum Falling Path Sum II — slow O(n³) DP and best/second-best O(n²) DP
 *
 * Idea: dp[r][c] = smallest sum of a path that picks one cell per row from row 0 to row r,
 *       ends at column c, and never uses the same column in two consecutive rows.
 *       Slow transition: dp[r][c] = grid[r][c] + min over c' != c of dp[r-1][c'] (an O(n) scan).
 *       "Minimum of the row except one position" only ever needs two numbers: the smallest
 *       value of the previous row (and its column) and the second smallest. If c is the
 *       column of the smallest, use the second smallest; otherwise use the smallest.
 *
 * Time complexity:  slow: O(n³) — n² cells, each scanning n previous cells.
 *                   fast: O(n²) — per row one O(n) pass to find best/second best, then O(1) per cell.
 * Space complexity: O(n) — one previous row (fast version keeps only three numbers about it).
 *
 * Run: java MinFallingPathSumII.java
 */
import java.util.Random;

public class MinFallingPathSumII {

    static int slow(int[][] grid) {
        int n = grid.length;
        // prev[c] = best path sum ending at column c of the previous row
        int[] prev = grid[0].clone();
        for (int r = 1; r < n; r++) {
            int[] cur = new int[n];
            for (int c = 0; c < n; c++) {
                int bestAbove = Integer.MAX_VALUE;
                for (int c2 = 0; c2 < n; c2++) {
                    if (c2 != c) bestAbove = Math.min(bestAbove, prev[c2]);  // must change column
                }
                cur[c] = grid[r][c] + bestAbove;
            }
            prev = cur;
        }
        int answer = Integer.MAX_VALUE;
        for (int value : prev) answer = Math.min(answer, value);
        return answer;
    }

    static int fast(int[][] grid) {
        int n = grid.length;
        if (n == 1) return grid[0][0];          // one cell: no "different column" constraint to apply
        // For the previous row we remember only: smallest value, its column, second smallest value.
        int best = Integer.MAX_VALUE, bestCol = -1, second = Integer.MAX_VALUE;
        for (int c = 0; c < n; c++) {
            int value = grid[0][c];
            if (value < best) { second = best; best = value; bestCol = c; }
            else if (value < second) { second = value; }
        }
        for (int r = 1; r < n; r++) {
            int newBest = Integer.MAX_VALUE, newBestCol = -1, newSecond = Integer.MAX_VALUE;
            for (int c = 0; c < n; c++) {
                // the minimum over "every column except c" is best, unless c IS best's column
                int above = (c == bestCol) ? second : best;
                int value = grid[r][c] + above;
                if (value < newBest) { newSecond = newBest; newBest = value; newBestCol = c; }
                else if (value < newSecond) { newSecond = value; }
            }
            best = newBest; bestCol = newBestCol; second = newSecond;
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};       // 1 -> 5 -> 7 = 13
        check("slow 3x3 example", slow(example), 13);
        check("fast 3x3 example", fast(example), 13);
        check("fast 1x1 (edge)", fast(new int[][]{{7}}), 7);
        // the two smallest values of row 0 tie (both 1): column choice must still work -> 1 + 1 = 2
        int[][] tie = {{1, 1}, {1, 100}};
        check("slow tie", slow(tie), 2);
        check("fast tie", fast(tie), 2);

        Random random = new Random(1289);
        int mismatches = 0;
        for (int trial = 0; trial < 500; trial++) {
            int n = 2 + random.nextInt(5);
            int[][] grid = new int[n][n];
            for (int r = 0; r < n; r++)
                for (int c = 0; c < n; c++) grid[r][c] = random.nextInt(21) - 10;  // includes negatives
            if (slow(grid) != fast(grid)) mismatches++;
        }
        check("500 random grids: mismatches slow vs fast", mismatches, 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
