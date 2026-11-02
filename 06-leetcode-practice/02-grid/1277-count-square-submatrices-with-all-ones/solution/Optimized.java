/**
 * 1277. Count Square Submatrices with All Ones — Space-optimized (one row)
 *
 * Idea: Same recurrence, but row i only needs row i-1: keep one array and save the old
 * "up" value of each column so the next column can use it as its diagonal.
 *
 * Time complexity:  O(m * n) — every cell once.
 * Space complexity: O(n) — one row of length n+1 (the input is left untouched).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int countSquares(int[][] matrix) {
        int cols = matrix[0].length;
        int[] dp = new int[cols + 1];   // dp[j+1] = squares ending at (current row, j)
        int total = 0;
        for (int[] row : matrix) {
            int prevDiagonal = 0;
            for (int j = 0; j < cols; j++) {
                int up = dp[j + 1];
                dp[j + 1] = (row[j] == 1) ? 1 + Math.min(up, Math.min(dp[j], prevDiagonal)) : 0;
                total += dp[j + 1];
                prevDiagonal = up;
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
