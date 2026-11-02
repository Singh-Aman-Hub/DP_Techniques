/**
 * 576. Out of Boundary Paths — Space-optimized (two layers)
 *
 * Idea: Layer k reads only layer k-1, so keep two m x n grids and swap them after each layer.
 * One grid is not enough: a cell's neighbours on both sides need the OLD layer.
 *
 * Time complexity:  O(K * m * n) — same arithmetic as tabulation.
 * Space complexity: O(m * n) — two layers.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    static int findPaths(int m, int n, int maxMove, int startRow, int startColumn) {
        long[][] previous = new long[m][n];   // previous[r][c] = exits from (r, c) within k-1 moves
        for (int k = 1; k <= maxMove; k++) {
            long[][] current = new long[m][n];
            for (int r = 0; r < m; r++) {
                for (int c = 0; c < n; c++) {
                    long ways = 0;
                    for (int[] d : DIRECTIONS) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr < 0 || nr >= m || nc < 0 || nc >= n) ways += 1;
                        else ways += previous[nr][nc];
                    }
                    current[r][c] = ways % MOD;
                }
            }
            previous = current;
        }
        return (int) previous[startRow][startColumn];
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
