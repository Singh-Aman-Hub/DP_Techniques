/**
 * 221. Maximal Square — Tabulation (bottom-up)
 *
 * Idea: Fill dp row by row; a '1' cell supports a square one larger than the smallest square
 * ending at its top, left and top-left neighbours. A zero-padded first row/column handles the
 * grid border without special cases.
 *
 * Time complexity:  O(m * n) — every cell once, O(1) each.
 * Space complexity: O(m * n) — the (m+1) x (n+1) table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maximalSquare(char[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        // dp[i+1][j+1] = largest square side with bottom-right corner (i, j); row/col 0 are padding
        int[][] dp = new int[rows + 1][cols + 1];
        int bestSide = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    dp[i + 1][j + 1] = 1 + Math.min(dp[i][j + 1], Math.min(dp[i + 1][j], dp[i][j]));
                    bestSide = Math.max(bestSide, dp[i + 1][j + 1]);
                }
            }
        }
        return bestSide * bestSide;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example grid", maximalSquare(grid("10100", "10111", "11111", "10010")), 4);
        check("diagonal ones", maximalSquare(grid("01", "10")), 1);
        check("single zero (edge)", maximalSquare(grid("0")), 0);
        check("all ones 3x3", maximalSquare(grid("111", "111", "111")), 9);
    }

    private static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
