/**
 * Unique Paths (LeetCode 62) — Tabulation (bottom-up)
 *
 * Idea: fill dp[r][c] = dp[r-1][c] + dp[r][c-1] row by row, left to right, so the cell
 *       above and the cell to the left are always ready. Row 0 and column 0 are all 1.
 *
 * Time complexity:  O(m * n) — two nested loops, O(1) work per cell.
 * Space complexity: O(m * n) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int uniquePaths(int m, int n) {
        // dp[r][c] = number of right/down routes from (0,0) to (r,c)
        int[][] dp = new int[m][n];
        for (int c = 0; c < n; c++) dp[0][c] = 1;   // top row: only reachable by moving right
        for (int r = 0; r < m; r++) dp[r][0] = 1;   // left column: only reachable by moving down
        for (int r = 1; r < m; r++) {
            for (int c = 1; c < n; c++) {
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1];   // from above + from the left
            }
        }
        return dp[m - 1][n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("m=3, n=3 (running example)", uniquePaths(3, 3), 6);
        check("m=3, n=7", uniquePaths(3, 7), 28);
        check("m=7, n=3 (rows and columns swapped)", uniquePaths(7, 3), 28);
        check("m=3, n=2", uniquePaths(3, 2), 3);
        check("m=10, n=10", uniquePaths(10, 10), 48620);
        check("m=1, n=5 (single row)", uniquePaths(1, 5), 1);
        check("m=1, n=1 (start is the end)", uniquePaths(1, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
