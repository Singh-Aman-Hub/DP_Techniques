/**
 * Unique Paths II (LeetCode 63) — Space-optimized (one rolling row)
 *
 * Idea: keep one row of length n. Seed it with "only the start is reachable", then run
 *       the same left-to-right update for every row, including row 0:
 *       blocked -> 0, otherwise old row[c] (from above) + new row[c-1] (from the left).
 *       No separate base-row loop is needed, so there is no base-row trap.
 *
 * Time complexity:  O(m * n) — every cell is visited once.
 * Space complexity: O(n) — a single row.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int uniquePathsWithObstacles(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        // row[c] = routes to (r, c) for the row r currently being processed
        int[] row = new int[n];
        row[0] = (grid[0][0] == 0) ? 1 : 0;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 1) {
                    row[c] = 0;              // wipe the count inherited from the row above
                } else if (c > 0) {
                    row[c] += row[c - 1];    // from above + from the left
                }
                // c == 0 and free: only one way in (from above), row[0] already holds it
            }
        }
        return row[n - 1];
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
