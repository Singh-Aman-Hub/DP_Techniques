/**
 * Minimum Path Sum (LeetCode 64) — Space-optimized (one rolling row)
 *
 * Idea: row r of the table reads only row r-1 (from above) and row r itself (from the
 *       left). Keep one array: before row[c] is overwritten it holds the value from
 *       above; row[c-1] was already updated, so it holds the value from the left.
 *       Hence row[c] = grid[r][c] + min(row[c], row[c-1]), looping left to right.
 *
 * Time complexity:  O(m * n) — same cells as Tabulation, O(1) each.
 * Space complexity: O(n) — one row (the route can no longer be reconstructed).
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        // row[c] = minimum cost to reach (r, c) for the row r currently being processed
        int[] row = new int[n];
        row[0] = grid[0][0];
        for (int c = 1; c < n; c++) row[c] = row[c - 1] + grid[0][c];   // row 0: running sum
        for (int r = 1; r < m; r++) {
            row[0] += grid[r][0];                                       // column 0: only from above
            for (int c = 1; c < n; c++) {
                row[c] = grid[r][c] + Math.min(row[c], row[c - 1]);     // above (old) vs left (new)
            }
        }
        return row[n - 1];
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
