/**
 * 1289. Minimum Falling Path Sum II — Tabulation (bottom-up)
 *
 * Idea: Fill dp row by row: dp[r][c] = grid[r][c] + min of dp[r-1][c'] over c' != c.
 * The answer is the minimum of the last row.
 *
 * Time complexity:  O(n^3) — n^2 cells, each scanning the n cells of the previous row.
 * Space complexity: O(n^2) — the dp table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minFallingPathSum(int[][] grid) {
        int n = grid.length;
        // dp[r][c] = minimum sum of a valid falling path from row 0 that ends at (r, c)
        int[][] dp = new int[n][n];
        for (int c = 0; c < n; c++) dp[0][c] = grid[0][c];
        for (int r = 1; r < n; r++) {
            for (int c = 0; c < n; c++) {
                int bestPrevious = Integer.MAX_VALUE;
                for (int prev = 0; prev < n; prev++) {
                    if (prev != c) bestPrevious = Math.min(bestPrevious, dp[r - 1][prev]);
                }
                dp[r][c] = grid[r][c] + bestPrevious;
            }
        }
        int best = Integer.MAX_VALUE;
        for (int c = 0; c < n; c++) best = Math.min(best, dp[n - 1][c]);
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", minFallingPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 13);
        check("1x1 (edge)", minFallingPathSum(new int[][]{{7}}), 7);
        check("2x2", minFallingPathSum(new int[][]{{1, 2}, {3, 4}}), 5);
        check("repeated minimum", minFallingPathSum(new int[][]{{2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 1, 2, 2, 2}, {2, 2, 2, 2, 2}}), 7);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
