/**
 * 1289. Minimum Falling Path Sum II — Brute force
 *
 * Idea: Try every valid path: from each cell, recurse into every column of the next row except
 * the current one. Exponential, used only on tiny grids to cross-check the DP answers.
 *
 * Time complexity:  O(n * (n-1)^(n-1)) — every valid path is explored.
 * Space complexity: O(n) — recursion depth equals the number of rows.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    private static int[][] grid;

    static int minFallingPathSum(int[][] input) {
        grid = input;
        int best = Integer.MAX_VALUE;
        for (int c = 0; c < grid.length; c++) best = Math.min(best, fallFrom(0, c));
        return best;
    }

    // minimum sum of a path that starts at (r, c) and continues to the last row
    private static int fallFrom(int r, int c) {
        if (r == grid.length - 1) return grid[r][c];
        int best = Integer.MAX_VALUE;
        for (int next = 0; next < grid.length; next++) {
            if (next != c) best = Math.min(best, fallFrom(r + 1, next));
        }
        return grid[r][c] + best;
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
