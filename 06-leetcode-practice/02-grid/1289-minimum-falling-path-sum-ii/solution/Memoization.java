/**
 * 1289. Minimum Falling Path Sum II — Memoization (top-down)
 *
 * Idea: endAt(r, c) = smallest sum of a valid path from row 0 ending at (r, c). It is
 * grid[r][c] plus the best endAt(r-1, c') over every column c' != c. Cache each cell.
 *
 * Time complexity:  O(n^3) — n^2 states, each scanning n previous columns.
 * Space complexity: O(n^2) — memo table plus recursion depth n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[][] grid;
    private static Integer[][] memo;   // memo[r][c] = min path sum ending at (r, c)

    static int minFallingPathSum(int[][] input) {
        grid = input;
        int n = grid.length;
        memo = new Integer[n][n];
        int best = Integer.MAX_VALUE;
        for (int c = 0; c < n; c++) best = Math.min(best, endAt(n - 1, c));
        return best;
    }

    private static int endAt(int r, int c) {
        if (r == 0) return grid[0][c];                 // the path has only picked this cell
        if (memo[r][c] != null) return memo[r][c];
        int bestPrevious = Integer.MAX_VALUE;
        for (int prev = 0; prev < grid.length; prev++) {
            if (prev != c) bestPrevious = Math.min(bestPrevious, endAt(r - 1, prev));
        }
        memo[r][c] = grid[r][c] + bestPrevious;
        return memo[r][c];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", minFallingPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 13);
        check("1x1 (edge)", minFallingPathSum(new int[][]{{7}}), 7);
        check("2x2", minFallingPathSum(new int[][]{{1, 2}, {3, 4}}), 5);
        check("repeated minimum", minFallingPathSum(new int[][]{{2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 2, 1, 2, 2}, {2, 1, 2, 2, 2}, {2, 2, 2, 2, 2}}), 7);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
