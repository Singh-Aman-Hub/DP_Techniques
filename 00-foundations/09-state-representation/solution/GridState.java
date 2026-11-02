/**
 * State representation — 2D state for counting grid paths
 *
 * Idea: A robot starts at the top-left cell of a rows x cols grid and may only
 *       move right or down. Its position needs TWO numbers (row, column), so the
 *       state is paths(r, c) = "the number of ways to reach the bottom-right
 *       cell starting from cell (r, c)". Two parameters -> a 2D cache.
 *
 * Time complexity:  O(rows * cols) — one state per cell, O(1) work per state.
 * Space complexity: O(rows * cols) — the 2D cache, plus a recursion stack of at
 *                   most rows + cols calls (each call moves one cell closer).
 *
 * Run: java GridState.java
 */
import java.util.Arrays;

public class GridState {

    // memo[r][c] = number of right/down paths from (r, c) to (rows-1, cols-1); -1 = not computed
    private static long[][] memo;
    private static int rows, cols;

    static long countPaths(int numRows, int numCols) {
        rows = numRows;
        cols = numCols;
        memo = new long[rows][cols];
        for (long[] row : memo) Arrays.fill(row, -1);
        return paths(0, 0);
    }

    private static long paths(int r, int c) {
        if (r >= rows || c >= cols) return 0;            // walked off the grid: invalid
        if (r == rows - 1 && c == cols - 1) return 1;    // standing on the target cell
        if (memo[r][c] != -1) return memo[r][c];
        memo[r][c] = paths(r, c + 1) + paths(r + 1, c);  // move right or move down
        return memo[r][c];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3 x 3 grid", countPaths(3, 3), 6);
        check("3 x 7 grid", countPaths(3, 7), 28);
        check("2 x 3 grid", countPaths(2, 3), 3);
        check("1 x 1 grid (edge: already at target)", countPaths(1, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
