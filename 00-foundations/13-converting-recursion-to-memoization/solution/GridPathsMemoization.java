/**
 * Grid paths (Unique Paths style) — Memoization, produced by the 4-step recipe
 *
 * Idea: apply the recipe to the brute force mechanically.
 *   1. Changing parameters: r (0..rows-1) and c (0..cols-1)  -> 2D cache
 *   2. Cache sized by the ranges: long[rows][cols], filled with -1
 *   3. Check the cache right after the base cases
 *   4. Store the result before returning it
 * The recurrence and base cases are copied unchanged from the brute force.
 *
 * Time complexity:  O(rows * cols) — one state per cell, O(1) work each.
 * Space complexity: O(rows * cols) for the cache + O(rows + cols) stack depth.
 *
 * Run: java GridPathsMemoization.java
 */
import java.util.Arrays;

public class GridPathsMemoization {

    // memo[r][c] = number of right/down routes from (0,0) to (r,c); -1 = not computed yet
    private static long[][] memo;

    static long uniquePaths(int rows, int cols) {
        memo = new long[rows][cols];                 // step 2: one cell per (r, c) pair
        for (long[] row : memo) Arrays.fill(row, -1);
        return paths(rows - 1, cols - 1);
    }

    private static long paths(int r, int c) {
        if (r == 0 || c == 0) return 1;              // base cases: unchanged
        if (memo[r][c] != -1) return memo[r][c];     // step 3: check
        long result = paths(r - 1, c) + paths(r, c - 1);
        memo[r][c] = result;                         // step 4: store
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3 x 3", uniquePaths(3, 3), 6);
        check("3 x 7", uniquePaths(3, 7), 28);
        check("2 x 3", uniquePaths(2, 3), 3);
        check("1 x 1 (edge)", uniquePaths(1, 1), 1);
        check("10 x 10 (C(18, 9))", uniquePaths(10, 10), 48_620);
        check("1 x 100 (edge: single row)", uniquePaths(1, 100), 1);

        // Cross-check against the brute force on all small grids.
        for (int rows = 1; rows <= 8; rows++) {
            for (int cols = 1; cols <= 8; cols++) {
                check("memo == brute force for " + rows + " x " + cols,
                      uniquePaths(rows, cols), bruteForce(rows - 1, cols - 1));
            }
        }
    }

    /** The original recursion, kept only to cross-check the memoized answers. */
    private static long bruteForce(int r, int c) {
        if (r == 0 || c == 0) return 1;
        return bruteForce(r - 1, c) + bruteForce(r, c - 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
