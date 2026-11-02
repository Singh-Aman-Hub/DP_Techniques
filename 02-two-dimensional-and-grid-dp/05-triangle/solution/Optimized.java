/**
 * Triangle (LeetCode 120) — Space-optimized bottom-up DP (one 1D array of length n)
 *
 * Idea: row r of the bottom-up table only reads row r + 1. So we keep a single
 * array "below" that starts as a copy of the last row and is overwritten in
 * place, left to right. Writing below[col] is safe because the cells still to
 * be computed in this row (col + 1, col + 2, ...) only read below[col + 1] and
 * beyond, which have not been overwritten yet.
 *
 * Time complexity:  O(n^2) — still one O(1) update per cell.
 * Space complexity: O(n)   — a single array as wide as the last row.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minimumTotal(int[][] triangle) {
        int rows = triangle.length;

        // below[col] = cheapest path from (current row, col) to the bottom.
        // Before the loop "current row" is the last row.
        int[] below = new int[rows];
        for (int col = 0; col < rows; col++) {
            below[col] = triangle[rows - 1][col];
        }

        for (int row = rows - 2; row >= 0; row--) {
            for (int col = 0; col <= row; col++) {
                // below[col] and below[col + 1] still hold row + 1's values here
                below[col] = triangle[row][col] + Math.min(below[col], below[col + 1]);
            }
        }
        return below[0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("4-row example", minimumTotal(new int[][]{{2}, {3, 4}, {6, 5, 7}, {4, 1, 8, 3}}), 11);
        check("single row (edge)", minimumTotal(new int[][]{{-10}}), -10);
        check("two rows", minimumTotal(new int[][]{{1}, {2, 3}}), 3);
        check("negative values", minimumTotal(new int[][]{{-1}, {2, 3}, {1, -1, -3}}), -1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
