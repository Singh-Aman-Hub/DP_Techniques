/**
 * 1277. Count Square Submatrices with All Ones — Tabulation (bottom-up)
 *
 * Idea: Build the Maximal Square table row by row; each value is also the number of squares
 * ending at that cell, so add it to a running total.
 *
 * Time complexity:  O(m * n) — every cell once.
 * Space complexity: O(m * n) — the zero-padded (m+1) x (n+1) table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int countSquares(int[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        // dp[i+1][j+1] = number of all-1 squares with bottom-right corner (i, j)
        int[][] dp = new int[rows + 1][cols + 1];
        int total = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == 1) {
                    dp[i + 1][j + 1] = 1 + Math.min(dp[i][j + 1], Math.min(dp[i + 1][j], dp[i][j]));
                    total += dp[i + 1][j + 1];
                }
            }
        }
        return total;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", countSquares(new int[][]{{0, 1, 1, 1}, {1, 1, 1, 1}, {0, 1, 1, 1}}), 15);
        check("example 2", countSquares(new int[][]{{1, 0, 1}, {1, 1, 0}, {1, 1, 0}}), 7);
        check("2x2 ones", countSquares(new int[][]{{1, 1}, {1, 1}}), 5);
        check("single zero (edge)", countSquares(new int[][]{{0}}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
