/**
 * 576. Out of Boundary Paths — Tabulation (bottom-up)
 *
 * Idea: dp[k][r][c] = ways to exit from (r, c) within k moves. Layer k is built from layer
 * k-1: each of the 4 moves contributes 1 if it leaves the grid, else dp[k-1][neighbour].
 *
 * Time complexity:  O(K * m * n) — K layers, m*n cells, 4 moves each.
 * Space complexity: O(K * m * n) — the full 3D table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    static int findPaths(int m, int n, int maxMove, int startRow, int startColumn) {
        // dp[k][r][c] = number of move sequences of length <= k that push the ball out from (r, c)
        long[][][] dp = new long[maxMove + 1][m][n];          // layer 0 is all zeros
        for (int k = 1; k <= maxMove; k++) {
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    long ways = 0;
                    for (int[] d : DIRECTIONS) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= m || nc < 0 || nc >= n) ways += 1;     // exits now
                        else ways += dp[k - 1][nr][nc];
                    }
                    dp[k][r][c] = ways % MOD;
                }
            }
        }
        return (int) dp[maxMove][startRow][startColumn];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("2x2, 2 moves, (0,0)", findPaths(2, 2, 2, 0, 0), 6);
        check("1x3, 3 moves, (0,1)", findPaths(1, 3, 3, 0, 1), 12);
        check("1x1, 1 move", findPaths(1, 1, 1, 0, 0), 4);
        check("0 moves (edge)", findPaths(3, 3, 0, 1, 1), 0);
        check("8x50, 23 moves, (5,26)", findPaths(8, 50, 23, 5, 26), 914783380);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
