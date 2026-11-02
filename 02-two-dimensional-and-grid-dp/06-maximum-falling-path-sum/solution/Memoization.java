/**
 * Maximum Falling Path Sum — Memoization (top-down DP)
 *
 * Idea: the brute force recomputes bestEndingAt(row, col) once for every child
 * that asks for it (up to three children per cell). We cache each (row, col)
 * after its first computation, so every cell is solved exactly once.
 *
 * Time complexity:  O(n^2) — n * n states, each doing O(1) work (at most 3 lookups).
 * Space complexity: O(n^2) — the memo table, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[row][col] = largest falling-path sum that ends at (row, col); null = not computed yet.
    // Sums can be negative or zero, so no int value is safe as a "missing" marker.
    private static Integer[][] memo;

    static int bestEndingAt(int[][] matrix, int row, int col) {
        if (row == 0) {
            return matrix[0][col];
        }
        if (memo[row][col] != null) {
            return memo[row][col]; // cache hit
        }
        int n = matrix.length;
        int bestParent = Integer.MIN_VALUE;
        for (int parentCol = col - 1; parentCol <= col + 1; parentCol++) {
            if (parentCol >= 0 && parentCol < n) {
                bestParent = Math.max(bestParent, bestEndingAt(matrix, row - 1, parentCol));
            }
        }
        memo[row][col] = matrix[row][col] + bestParent;
        return memo[row][col];
    }

    static int maxFallingPathSum(int[][] matrix) {
        int n = matrix.length;
        memo = new Integer[n][n];
        int best = Integer.MIN_VALUE;
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
