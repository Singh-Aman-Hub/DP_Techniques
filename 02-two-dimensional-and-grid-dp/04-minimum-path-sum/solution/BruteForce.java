/**
 * Minimum Path Sum (LeetCode 64) — Brute force recursion
 *
 * Idea: best(r, c) = cheapest right/down route cost from (0,0) to (r,c), both ends
 *       included. The last move came from above or from the left, so
 *       best(r, c) = grid[r][c] + min(best(r-1, c), best(r, c-1)).
 *       Off-board predecessors are impossible and return +infinity, so min ignores them.
 *
 * Time complexity:  O(2^(m+n)) — two calls per cell, depth m+n-2 (every route is tried).
 * Space complexity: O(m + n) — recursion stack.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    // +infinity for an impossible state. Never add to it: the cell cost is added AFTER min.
    private static final int INF = Integer.MAX_VALUE;

    static int minPathSum(int[][] grid) {
        return best(grid, grid.length - 1, grid[0].length - 1);
    }

    private static int best(int[][] grid, int r, int c) {
        if (r < 0 || c < 0) return INF;              // off the board: impossible predecessor
        if (r == 0 && c == 0) return grid[0][0];     // the route is just the start cell
        return grid[r][c] + Math.min(best(grid, r - 1, c), best(grid, r, c - 1));
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 running example", minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}), 7);
        check("2x3 [[1,2,3],[4,5,6]]", minPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}}), 12);
        check("2x2 [[1,2],[1,1]]", minPathSum(new int[][]{{1, 2}, {1, 1}}), 3);
        check("single row [[1,2,3]]", minPathSum(new int[][]{{1, 2, 3}}), 6);
        check("single column [[2],[3],[4]]", minPathSum(new int[][]{{2}, {3}, {4}}), 9);
        check("single cell [[5]]", minPathSum(new int[][]{{5}}), 5);
        check("all zeros 2x2", minPathSum(new int[][]{{0, 0}, {0, 0}}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
