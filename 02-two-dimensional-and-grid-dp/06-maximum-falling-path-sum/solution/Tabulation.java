/**
 * Maximum Falling Path Sum — Tabulation (bottom-up DP, filled top row first)
 *
 * Idea: dp[row][col] = largest sum of a falling path that ends at (row, col).
 * Row 0 is the matrix's row 0. Every later cell adds its value to the best of
 * its (up to) three parents in the previous row. The answer is the largest
 * value in the last row, because the path may end in any column.
 *
 * Time complexity:  O(n^2) — n * n cells, each reading at most 3 parents.
 * Space complexity: O(n^2) — the full dp table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxFallingPathSum(int[][] matrix) {
        int n = matrix.length;

        // dp[row][col] = largest falling-path sum over paths that end at (row, col)
        int[][] dp = new int[n][n];

        // Base case: a path ending in row 0 is just that cell (the start is free).
        for (int col = 0; col < n; col++) {
            dp[0][col] = matrix[0][col];
        }

        for (int row = 1; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int bestParent = dp[row - 1][col];                       // straight above always exists
                if (col > 0) bestParent = Math.max(bestParent, dp[row - 1][col - 1]);      // above-left
                if (col < n - 1) bestParent = Math.max(bestParent, dp[row - 1][col + 1]);  // above-right
                dp[row][col] = matrix[row][col] + bestParent;
            }
        }

        int best = dp[n - 1][0];
        for (int col = 1; col < n; col++) {
            best = Math.max(best, dp[n - 1][col]);
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", maxFallingPathSum(new int[][]{{2, 1, 3}, {6, 5, 4}, {7, 8, 9}}), 17);
        check("1..9 grid", maxFallingPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 18);
        check("1x1 grid (edge)", maxFallingPathSum(new int[][]{{5}}), 5);
        check("negatives", maxFallingPathSum(new int[][]{{-19, 57}, {-40, -5}}), 52);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
