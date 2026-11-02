/**
 * Transition dependencies and computation order — correct vs. wrong loop order
 *
 * Idea: a tabulation loop is only correct if every cell it READS has already
 * been WRITTEN. This file fills the same tables twice: once in an order that
 * follows the dependency arrows, and once in an order that breaks them. The
 * broken order silently reads cells that still hold their initial 0 and
 * returns a wrong answer without any error. Three problems are shown:
 *   1. Climbing Stairs, ways[i] = ways[i-1] + ways[i-2]       -> fill left to right
 *   2. Min Cost Climbing Stairs, suffix form
 *      best[i] = cost[i] + min(best[i+1], best[i+2])          -> fill right to left
 *   3. Grid paths, paths[r][c] = paths[r-1][c] + paths[r][c-1] -> fill row by row
 *
 * Time complexity:  O(n) for the 1D tables, O(rows * cols) for the grid — every
 *                   cell is visited once and does O(1) work, whatever the order.
 * Space complexity: O(n) or O(rows * cols) — one table per run.
 *
 * Run: java LoopOrderDemo.java
 */
public class LoopOrderDemo {

    // ---------- 1. Climbing Stairs: ways[i] depends on ways[i-1], ways[i-2] ----------

    /** Correct: fill from small i to large i, so both "earlier" cells are ready. */
    static long stairsLeftToRight(int n) {
        // ways[i] = number of distinct ways to reach step i using 1- or 2-steps
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;
        ways[1] = 1;
        for (int i = 2; i <= n; i++) {
            ways[i] = ways[i - 1] + ways[i - 2];
        }
        return ways[n];
    }

    /** WRONG: fills from large i to small i, so ways[i-1] and ways[i-2] are still 0. */
    static long stairsRightToLeftWrong(int n) {
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;
        ways[1] = 1;
        for (int i = n; i >= 2; i--) {
            ways[i] = ways[i - 1] + ways[i - 2]; // reads cells that are not filled yet
        }
        return ways[n];
    }

    // ---------- 2. Min Cost Climbing Stairs (suffix form): best[i] depends on best[i+1], best[i+2] ----------

    /** Correct: best[i] reads LARGER indices, so fill from right to left. */
    static int minCostRightToLeft(int[] cost) {
        int n = cost.length;
        // best[i] = cheapest total cost to get from step i to the top (step n),
        // paying cost[i] when we stand on step i. best[n] = best[n+1] = 0 (already at/after the top).
        int[] best = new int[n + 2];
        for (int i = n - 1; i >= 0; i--) {
            best[i] = cost[i] + Math.min(best[i + 1], best[i + 2]);
        }
        return Math.min(best[0], best[1]); // we may start on step 0 or step 1
    }

    /** WRONG: fills left to right, so best[i+1] and best[i+2] are still 0 when read. */
    static int minCostLeftToRightWrong(int[] cost) {
        int n = cost.length;
        int[] best = new int[n + 2];
        for (int i = 0; i < n; i++) {
            best[i] = cost[i] + Math.min(best[i + 1], best[i + 2]); // reads unfilled cells
        }
        return Math.min(best[0], best[1]);
    }

    // ---------- 3. Grid paths: paths[r][c] depends on the cell above and the cell to the left ----------

    /** Correct: row by row, left to right inside each row. */
    static long gridRowByRow(int rows, int cols) {
        // paths[r][c] = number of right/down paths from (0,0) to (r,c)
        long[][] paths = new long[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || c == 0) {
                    paths[r][c] = 1; // first row / first column: only one straight path
                } else {
                    paths[r][c] = paths[r - 1][c] + paths[r][c - 1];
                }
            }
        }
        return paths[rows - 1][cols - 1];
    }

    /** Also correct: column by column. Both arrows (from above, from the left) still point at finished cells. */
    static long gridColumnByColumn(int rows, int cols) {
        long[][] paths = new long[rows][cols];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                if (r == 0 || c == 0) {
                    paths[r][c] = 1;
                } else {
                    paths[r][c] = paths[r - 1][c] + paths[r][c - 1];
                }
            }
        }
        return paths[rows - 1][cols - 1];
    }

    /**
     * WRONG: bottom-right to top-left. Only the start cell is seeded, so every
     * read of "above" or "left" sees a 0 that has not been computed yet.
     */
    static long gridReversedWrong(int rows, int cols) {
        long[][] paths = new long[rows][cols];
        paths[0][0] = 1;
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = cols - 1; c >= 0; c--) {
                if (r == 0 && c == 0) continue;
                long fromAbove = (r > 0) ? paths[r - 1][c] : 0;
                long fromLeft = (c > 0) ? paths[r][c - 1] : 0;
                paths[r][c] = fromAbove + fromLeft;
            }
        }
        return paths[rows - 1][cols - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        // Climbing Stairs
        check("stairs n=5, left to right", stairsLeftToRight(5), 8);
        check("stairs n=10, left to right", stairsLeftToRight(10), 89);
        check("stairs n=1 (edge), left to right", stairsLeftToRight(1), 1);
        check("stairs n=5, WRONG right to left gives 0", stairsRightToLeftWrong(5), 0);

        // Min Cost Climbing Stairs, suffix form
        int[] small = {10, 15, 20};
        int[] longer = {1, 100, 1, 1, 1, 100, 1, 1, 100, 1};
        check("min cost [10,15,20], right to left", minCostRightToLeft(small), 15);
        check("min cost long example, right to left", minCostRightToLeft(longer), 6);
        check("min cost [10,15,20], WRONG left to right gives 10", minCostLeftToRightWrong(small), 10);

        // Grid paths
        check("grid 3x3, row by row", gridRowByRow(3, 3), 6);
        check("grid 3x7, row by row", gridRowByRow(3, 7), 28);
        check("grid 1x1 (edge), row by row", gridRowByRow(1, 1), 1);
        check("grid 3x7, column by column", gridColumnByColumn(3, 7), 28);
        check("grid 3x3, WRONG reversed gives 0", gridReversedWrong(3, 3), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
