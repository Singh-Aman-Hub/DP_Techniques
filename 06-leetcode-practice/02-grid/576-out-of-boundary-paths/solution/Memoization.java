/**
 * 576. Out of Boundary Paths — Memoization (top-down)
 *
 * Idea: paths(k, r, c) = ways to push the ball out from (r, c) with at most k moves. Each of
 * the 4 moves either exits (count 1) or lands on a cell with k-1 moves left. The ball can
 * revisit cells, so k must be part of the state. Cache every (k, r, c).
 *
 * Time complexity:  O(K * m * n) — each state once, 4 additions each.
 * Space complexity: O(K * m * n) — the memo cube, plus recursion depth K.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int MOD = 1_000_000_007;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static int rows, cols;
    private static long[][][] memo;   // memo[k][r][c] = exits from (r, c) within k moves; -1 = unknown

    static int findPaths(int m, int n, int maxMove, int startRow, int startColumn) {
        rows = m;
        cols = n;
        memo = new long[maxMove + 1][m][n];
        for (long[][] layer : memo) for (long[] row : layer) Arrays.fill(row, -1);
        return (int) paths(maxMove, startRow, startColumn);
    }

    private static long paths(int movesLeft, int r, int c) {
        if (r < 0 || r >= rows || c < 0 || c >= cols) return 1;   // the last move left the grid
        if (movesLeft == 0) return 0;                              // stuck inside
        if (memo[movesLeft][r][c] != -1) return memo[movesLeft][r][c];
        long ways = 0;
        for (int[] d : DIRECTIONS) {
            ways = (ways + paths(movesLeft - 1, r + d[0], c + d[1])) % MOD;
        }
        memo[movesLeft][r][c] = ways;
        return ways;
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
