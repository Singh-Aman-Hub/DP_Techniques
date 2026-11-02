/**
 * 221. Maximal Square — Space-optimized (one row)
 *
 * Idea: Row i only reads row i-1, so keep one array. Before overwriting dp[j+1] (the "up"
 * value), remember it: it becomes the "diagonal" for the next column.
 *
 * Time complexity:  O(m * n) — every cell once.
 * Space complexity: O(n) — one row of length n+1.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maximalSquare(char[][] matrix) {
        int cols = matrix[0].length;
        int[] dp = new int[cols + 1];   // dp[j+1] = largest side ending at (current row, j)
        int bestSide = 0;
        for (char[] row : matrix) {
            int prevDiagonal = 0;       // old dp[j] from the previous row, i.e. the top-left value
            for (int j = 0; j < cols; j++) {
                int up = dp[j + 1];     // still holds the previous row's value
                if (row[j] == '1') {
                    dp[j + 1] = 1 + Math.min(up, Math.min(dp[j], prevDiagonal));
                    bestSide = Math.max(bestSide, dp[j + 1]);
                } else {
                    dp[j + 1] = 0;
                }
                prevDiagonal = up;
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
