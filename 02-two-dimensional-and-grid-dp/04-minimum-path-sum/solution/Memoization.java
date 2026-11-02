/**
 * Minimum Path Sum (LeetCode 64) — Memoization (top-down)
 *
 * Idea: the BruteForce recursion with a 2D cache indexed by (row, col). A path sum can
 *       be 0 (all-zero grid), so -1 marks "not computed yet".
 *
 * Time complexity:  O(m * n) — each of the m*n cells is solved once with O(1) work.
 * Space complexity: O(m * n) — the memo, plus O(m + n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int INF = Integer.MAX_VALUE;

    // memo[r][c] = minimum cost of a route from (0,0) to (r,c); -1 = not computed yet
    private static int[][] memo;

    static int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        memo = new int[m][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return best(grid, m - 1, n - 1);
    }

    private static int best(int[][] grid, int r, int c) {
        if (r < 0 || c < 0) return INF;
        if (r == 0 && c == 0) return grid[0][0];
        if (memo[r][c] != -1) return memo[r][c];
        memo[r][c] = grid[r][c] + Math.min(best(grid, r - 1, c), best(grid, r, c - 1));
        return memo[r][c];
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
