/**
 * Triangle (LeetCode 120) — Tabulation (bottom-up DP, filled from the LAST row upward)
 *
 * Idea: dp[row][col] = cheapest path from (row, col) to the bottom. The last row
 * is known directly (the cell itself). Every other cell has exactly two children,
 * (row + 1, col) and (row + 1, col + 1), and both always exist, so there are no
 * edge cases. The answer ends up in dp[0][0] — no final scan of a row is needed.
 *
 * Time complexity:  O(n^2) — one O(1) update per cell, n(n+1)/2 cells.
 * Space complexity: O(n^2) — the jagged dp table has one entry per cell.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minimumTotal(int[][] triangle) {
        int rows = triangle.length;

        // dp[row][col] = cheapest total of a path that starts at (row, col) and ends in the last row
        int[][] dp = new int[rows][];
        for (int r = 0; r < rows; r++) {
            dp[r] = new int[r + 1];
        }

        // Base case: a path that starts in the last row is just that one cell.
        for (int col = 0; col < rows; col++) {
            dp[rows - 1][col] = triangle[rows - 1][col];
        }

        // Fill upward: row r needs row r + 1, which is already complete.
        for (int row = rows - 2; row >= 0; row--) {
            for (int col = 0; col <= row; col++) {
                dp[row][col] = triangle[row][col] + Math.min(dp[row + 1][col], dp[row + 1][col + 1]);
            }
        }
        return dp[0][0];
    }

    /**
     * For comparison: the top-down-direction table (fill from the apex downward).
     * dp[row][col] = cheapest path from the apex TO (row, col). It works, but the
     * two edges of each row need special cases, and the answer is the minimum
     * over the whole last row.
     */
    static int minimumTotalFromApex(int[][] triangle) {
        int rows = triangle.length;
        int[][] dp = new int[rows][];
        for (int r = 0; r < rows; r++) {
            dp[r] = new int[r + 1];
        }
        dp[0][0] = triangle[0][0];
        for (int row = 1; row < rows; row++) {
            // left edge: only reachable from the left edge of the row above
            dp[row][0] = dp[row - 1][0] + triangle[row][0];
            // right edge: only reachable from the right edge of the row above
            dp[row][row] = dp[row - 1][row - 1] + triangle[row][row];
            for (int col = 1; col < row; col++) {
                dp[row][col] = triangle[row][col] + Math.min(dp[row - 1][col - 1], dp[row - 1][col]);
            }
        }
        int best = dp[rows - 1][0];
        for (int col = 1; col < rows; col++) {
            best = Math.min(best, dp[rows - 1][col]);
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][][] inputs = {
            {{2}, {3, 4}, {6, 5, 7}, {4, 1, 8, 3}},
            {{-10}},
            {{1}, {2, 3}},
            {{-1}, {2, 3}, {1, -1, -3}}
        };
        String[] labels = {"4-row example", "single row (edge)", "two rows", "negative values"};
        int[] expected = {11, -10, 3, -1};
        for (int t = 0; t < inputs.length; t++) {
            check(labels[t] + " (from bottom)", minimumTotal(inputs[t]), expected[t]);
            check(labels[t] + " (from apex)", minimumTotalFromApex(inputs[t]), expected[t]);
        }
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
