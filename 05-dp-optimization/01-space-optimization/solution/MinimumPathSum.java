/**
 * Minimum Path Sum (LC 64) — 2D DP reduced to one row
 *
 * Idea: dp[r][c] = grid[r][c] + min(dp[r-1][c], dp[r][c-1]). The footprint is
 * "the cell above" and "the cell to the left". If we keep a single array row[c]
 * and sweep c from left to right, then just before we overwrite row[c] it still
 * holds the value from the row above, and row[c-1] already holds the new value
 * of the current row: exactly the two values the recurrence needs.
 *
 * Time complexity:  O(R·C) for both versions — every cell is computed once with O(1) work.
 * Space complexity: O(R·C) for the table, O(C) for the one-row version.
 *
 * Run: java MinimumPathSum.java
 */
import java.util.Random;

public class MinimumPathSum {

    /** Full-table version. */
    static int minPathSumTable(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        // dp[r][c] = cheapest sum of a right/down path from (0,0) to (r,c)
        int[][] dp = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) dp[r][c] = grid[0][0];
                else if (r == 0) dp[r][c] = grid[r][c] + dp[r][c - 1];
                else if (c == 0) dp[r][c] = grid[r][c] + dp[r - 1][c];
                else dp[r][c] = grid[r][c] + Math.min(dp[r - 1][c], dp[r][c - 1]);
            }
        }
        return dp[rows - 1][cols - 1];
    }

    /** One-row version: row[c] is "dp of the row above" until we overwrite it. */
    static int minPathSumOneRow(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[] row = new int[cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) row[c] = grid[0][0];
                else if (r == 0) row[c] = grid[r][c] + row[c - 1];       // only left exists
                else if (c == 0) row[c] = grid[r][c] + row[c];           // only "above" exists (old value)
                else row[c] = grid[r][c] + Math.min(row[c] /* above, not yet overwritten */,
                                                    row[c - 1] /* left, already new */);
            }
        }
        return row[cols - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example = { {1, 3, 1}, {1, 5, 1}, {4, 2, 1} };
        check("table   3x3 example", minPathSumTable(example), 7);
        check("one row 3x3 example", minPathSumOneRow(example), 7);

        int[][] twoByThree = { {1, 2, 3}, {4, 5, 6} };
        check("table   2x3", minPathSumTable(twoByThree), 12);
        check("one row 2x3", minPathSumOneRow(twoByThree), 12);

        int[][] single = { {9} };
        check("table   1x1 edge", minPathSumTable(single), 9);
        check("one row 1x1 edge", minPathSumOneRow(single), 9);

        int[][] column = { {1}, {2}, {3} };
        check("one row 3x1 column", minPathSumOneRow(column), 6);

        Random random = new Random(7);
        boolean allAgree = true;
        for (int trial = 0; trial < 1000 && allAgree; trial++) {
            int rows = 1 + random.nextInt(6), cols = 1 + random.nextInt(6);
            int[][] grid = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) grid[r][c] = random.nextInt(10);
            allAgree = minPathSumTable(grid) == minPathSumOneRow(grid);
        }
        check("1000 random grids agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
