/**
 * Maximum Falling Path Sum — Brute force recursion
 *
 * Idea: a falling path starts at any cell of the top row and moves one row down
 * at a time, to the cell directly below, below-left or below-right. The best path
 * that ENDS at (row, col) is the cell's value plus the best path ending at one of
 * its (up to) three parents in the row above. We try every parent recursively and
 * take the best ending cell in the last row.
 *
 * Time complexity:  O(n * 3^n) — each of the n ending cells starts a recursion that
 *                   branches up to 3 ways for n - 1 levels.
 * Space complexity: O(n)       — the call stack is at most n frames deep.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    // Largest sum of a falling path that starts anywhere in row 0 and ends at (row, col).
    static int bestEndingAt(int[][] matrix, int row, int col) {
        // Base case: a path ending in the top row consists of that cell only.
        if (row == 0) {
            return matrix[0][col];
        }
        int n = matrix.length;
        int bestParent = Integer.MIN_VALUE;
        // The parents are (row-1, col-1), (row-1, col), (row-1, col+1); skip those outside the grid.
        for (int parentCol = col - 1; parentCol <= col + 1; parentCol++) {
            if (parentCol >= 0 && parentCol < n) {
                bestParent = Math.max(bestParent, bestEndingAt(matrix, row - 1, parentCol));
            }
        }
        return matrix[row][col] + bestParent;
    }

    static int maxFallingPathSum(int[][] matrix) {
        int n = matrix.length;
        int best = Integer.MIN_VALUE;
        // The path may end anywhere in the last row, so try every ending column.
        for (int col = 0; col < n; col++) {
            best = Math.max(best, bestEndingAt(matrix, n - 1, col));
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
