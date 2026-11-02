/**
 * Unique Paths II (LeetCode 63) — Memoization (top-down)
 *
 * Idea: the BruteForce recursion with a 2D cache. Because 0 is now a real answer
 *       (blocked or cut-off cells), the cache uses -1 to mean "not computed yet";
 *       otherwise cells with answer 0 would be recomputed again and again.
 *
 * Time complexity:  O(m * n) — each cell is solved once with O(1) work.
 * Space complexity: O(m * n) — the memo, plus O(m + n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[r][c] = obstacle-free routes from (0,0) to (r,c); -1 = not computed yet
    private static int[][] memo;

    static int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        memo = new int[m][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return countPaths(grid, m - 1, n - 1);
    }

    private static int countPaths(int[][] grid, int r, int c) {
        if (r < 0 || c < 0) return 0;
        if (grid[r][c] == 1) return 0;
        if (r == 0 && c == 0) return 1;
        if (memo[r][c] != -1) return memo[r][c];
        memo[r][c] = countPaths(grid, r - 1, c) + countPaths(grid, r, c - 1);
        return memo[r][c];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3, obstacle in the centre (running example)",
              uniquePathsWithObstacles(new int[][]{{0, 0, 0}, {0, 1, 0}, {0, 0, 0}}), 2);
        check("[[0,1],[0,0]]", uniquePathsWithObstacles(new int[][]{{0, 1}, {0, 0}}), 1);
        check("base-row trap [[0,1,0],[0,0,0]]",
              uniquePathsWithObstacles(new int[][]{{0, 1, 0}, {0, 0, 0}}), 1);
        check("base-column trap [[0,0],[1,0],[0,0]]",
              uniquePathsWithObstacles(new int[][]{{0, 0}, {1, 0}, {0, 0}}), 1);
        check("4x4 with two obstacles",
              uniquePathsWithObstacles(new int[][]{{0, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 0, 1}, {0, 0, 0, 0}}), 4);
        check("blocked start [[1]]", uniquePathsWithObstacles(new int[][]{{1}}), 0);
        check("blocked end [[0,0],[0,1]]", uniquePathsWithObstacles(new int[][]{{0, 0}, {0, 1}}), 0);
        check("single free cell [[0]]", uniquePathsWithObstacles(new int[][]{{0}}), 1);
        check("single column cut off [[0],[1],[0]]", uniquePathsWithObstacles(new int[][]{{0}, {1}, {0}}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
