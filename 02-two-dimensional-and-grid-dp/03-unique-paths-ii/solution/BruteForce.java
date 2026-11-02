/**
 * Unique Paths II (LeetCode 63) — Brute force recursion
 *
 * Idea: paths(r, c) = number of obstacle-free right/down routes from (0,0) to (r,c).
 *       A blocked cell has 0 routes; otherwise add the routes to the cell above and the
 *       cell to the left. Off-board cells have 0 routes. Because the recursion never
 *       assumes anything about row 0 or column 0, it cannot fall into the base-row trap.
 *
 * Time complexity:  O(2^(m+n)) — two calls per free cell, recursion depth m+n-2.
 * Space complexity: O(m + n) — recursion stack.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int uniquePathsWithObstacles(int[][] grid) {
        return countPaths(grid, grid.length - 1, grid[0].length - 1);
    }

    private static int countPaths(int[][] grid, int r, int c) {
        if (r < 0 || c < 0) return 0;      // off the board: no route comes from here
        if (grid[r][c] == 1) return 0;     // blocked (checked BEFORE the start, so a blocked start gives 0)
        if (r == 0 && c == 0) return 1;    // the free start: the empty route
        return countPaths(grid, r - 1, c) + countPaths(grid, r, c - 1);
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
