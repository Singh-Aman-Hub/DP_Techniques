/**
 * Maximum Falling Path Sum — Space-optimized DP (two rows)
 *
 * Idea: row r of the table reads only row r - 1. We keep the previous row in
 * "prev", build the current row in "cur", then swap. A single in-place array
 * does NOT work here: cur[col] needs prev[col - 1], which we would already have
 * overwritten when moving left to right.
 *
 * Time complexity:  O(n^2) — still n * n cells with O(1) work each.
 * Space complexity: O(n)   — two arrays of length n.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxFallingPathSum(int[][] matrix) {
        int n = matrix.length;

        // prev[col] = best falling-path sum ending at (row - 1, col)
        int[] prev = new int[n];
        for (int col = 0; col < n; col++) {
            prev[col] = matrix[0][col];
        }

        for (int row = 1; row < n; row++) {
            // cur[col] = best falling-path sum ending at (row, col)
            int[] cur = new int[n];
            for (int col = 0; col < n; col++) {
                int bestParent = prev[col];
                if (col > 0) bestParent = Math.max(bestParent, prev[col - 1]);
                if (col < n - 1) bestParent = Math.max(bestParent, prev[col + 1]);
                cur[col] = matrix[row][col] + bestParent;
            }
            prev = cur; // the current row becomes "previous" for the next iteration
        }

        int best = prev[0];
        for (int col = 1; col < n; col++) {
            best = Math.max(best, prev[col]);
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", maxFallingPathSum(new int[][]{{2, 1, 3}, {6, 5, 4}, {7, 8, 9}}), 17);
        check("1..9 grid", maxFallingPathSum(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}), 18);
        check("1x1 grid (edge)", maxFallingPathSum(new int[][]{{5}}), 5);
        check("negatives", maxFallingPathSum(new int[][]{{-19, 57}, {-40, -5}}), 52);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
