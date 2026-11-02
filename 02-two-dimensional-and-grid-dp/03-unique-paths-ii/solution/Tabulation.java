/**
 * Unique Paths II (LeetCode 63) — Tabulation (bottom-up)
 *
 * Idea: fill the table row by row, left to right, computing EVERY cell (including the
 *       top row and left column) with the same rule: blocked -> 0, start -> 1,
 *       otherwise above + left with off-board neighbours counting 0. Treating the
 *       border like every other cell is what avoids the base-row / base-column trap.
 *
 * Time complexity:  O(m * n) — one pass, O(1) per cell.
 * Space complexity: O(m * n) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        // dp[r][c] = number of obstacle-free routes from (0,0) to (r,c)
        int[][] dp = new int[m][n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) { dp[r][c] = 0; continue; }    // blocked
                if (r == 0 && c == 0) { dp[r][c] = 1; continue; }   // free start
                int fromAbove = (r > 0) ? dp[r - 1][c] : 0;
                int fromLeft  = (c > 0) ? dp[r][c - 1] : 0;
                dp[r][c] = fromAbove + fromLeft;
            }
        }
        return dp[m - 1][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3, obstacle in the centre (running example)",
              uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}), 2);
        check("[[0,1],[0,0]]", uniquePathsWithObstacles(new int[][]{{0, 1}, {0, 0}}), 1);
        check("base-row trap [[0,1,0],[0,0,0]]",
              uniquePathsWithObstacles(new int[][]{{0, 1, 0}, {0, 0, 0}}), 1);
        check("base-column trap [[0,0],[1,0],[0,0]]",
              uniquePathsWithObstacles(new int[][]{{0, 0}, {1, 0}, {0, 0}}), 1);
        check("4x4 with two obstacles",
              uniquePathsWithObstacles(new int[][]{{0, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 0, 1}, {0, 0, 0, 0}}), 4);
        check("blocked start [[1]]", uniquePathsWithObstacles(new int[][]{{1}}), 0);
        check("blocked end [[0,0],[0,1]]", uniquePathsWithObstacles(new int[][]{{0, 0}, {0, 1}}), 0);
        check("single free cell [[0]]", uniquePathsWithObstacles(new int[][]{{0}}), 1);
        check("single column cut off [[0],[1],[0]]", uniquePathsWithObstacles(new int[][]{{0}, {1}, {0}}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
