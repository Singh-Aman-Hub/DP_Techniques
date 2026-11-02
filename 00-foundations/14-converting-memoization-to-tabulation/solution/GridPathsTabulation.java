/**
 * Grid paths (Unique Paths style) — Tabulation, produced from the memoized version
 *
 * Idea: translate the memoized function line by line.
 *   1. The cache long[rows][cols] becomes the table of the same shape.
 *   2. Base cases (r == 0 || c == 0 -> 1) become the first row and first column of 1s.
 *   3. Calls paths(r-1, c) and paths(r, c-1) become reads of the cell above and the cell to the left.
 *   4. Both reads point up or left, so fill rows top to bottom, columns left to right.
 *   5. Return the cell matching the top-level call paths(rows-1, cols-1).
 * The memoized version stays in this file so the two can be checked against each other.
 *
 * Time complexity:  O(rows * cols) — every cell is filled once with one addition.
 * Space complexity: O(rows * cols) — the table; no recursion stack.
 *
 * Run: java GridPathsTabulation.java
 */
import java.util.Arrays;

public class GridPathsTabulation {

    // ---------- tabulation (the "after") ----------

    static long uniquePaths(int rows, int cols) {
        // paths[r][c] = number of right/down routes from (0,0) to (r,c)
        long[][] paths = new long[rows][cols];

        // base cases -> initialisation: the first row and the first column are straight lines
        for (int c = 0; c < cols; c++) paths[0][c] = 1;
        for (int r = 0; r < rows; r++) paths[r][0] = 1;

        // every other cell reads the cell above and the cell to the left -> both already filled
        for (int r = 1; r < rows; r++) {
            for (int c = 1; c < cols; c++) {
                paths[r][c] = paths[r - 1][c] + paths[r][c - 1]; // was: paths(r-1,c) + paths(r,c-1)
            }
        }
        return paths[rows - 1][cols - 1]; // top-level call was paths(rows-1, cols-1)
    }

    // ---------- memoization (the "before"), for cross-checking ----------

    // memo[r][c] = number of routes from (0,0) to (r,c); -1 = not computed yet
    private static long[][] memo;

    static long uniquePathsMemo(int rows, int cols) {
        memo = new long[rows][cols];
        for (long[] row : memo) Arrays.fill(row, -1);
        return pathsMemo(rows - 1, cols - 1);
    }

    private static long pathsMemo(int r, int c) {
        if (r == 0 || c == 0) return 1;
        if (memo[r][c] != -1) return memo[r][c];
        memo[r][c] = pathsMemo(r - 1, c) + pathsMemo(r, c - 1);
        return memo[r][c];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3 x 3", uniquePaths(3, 3), 6);
        check("3 x 7", uniquePaths(3, 7), 28);
        check("2 x 3", uniquePaths(2, 3), 3);
        check("10 x 10", uniquePaths(10, 10), 48_620);
        check("1 x 1 (edge)", uniquePaths(1, 1), 1);
        check("1 x 5 (edge: single row)", uniquePaths(1, 5), 1);
        check("5 x 1 (edge: single column)", uniquePaths(5, 1), 1);

        for (int rows = 1; rows <= 12; rows++) {
            for (int cols = 1; cols <= 12; cols++) {
                check("tabulation == memoization for " + rows + " x " + cols,
                      uniquePaths(rows, cols), uniquePathsMemo(rows, cols));
            }
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
