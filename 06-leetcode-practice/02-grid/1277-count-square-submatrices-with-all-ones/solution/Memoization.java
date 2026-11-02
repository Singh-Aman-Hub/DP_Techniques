/**
 * 1277. Count Square Submatrices with All Ones — Memoization (top-down)
 *
 * Idea: The largest all-1 square ending at (i, j) has side s, and then exactly s squares end
 * there (sides 1..s). s = 1 + min(up, left, diagonal) for a 1 cell. Sum s over every cell.
 *
 * Time complexity:  O(m * n) — each cell computed once in O(1).
 * Space complexity: O(m * n) — memo table plus recursion depth up to m + n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[][] matrix;
    private static Integer[][] memo;   // memo[i][j] = number of all-1 squares ending at (i, j)

    static int countSquares(int[][] grid) {
        matrix = grid;
        memo = new Integer[grid.length][grid[0].length];
        int total = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                total += squaresEndingAt(i, j);
            }
        }
        return total;
    }

    private static int squaresEndingAt(int i, int j) {
        if (i < 0 || j < 0 || matrix[i][j] == 0) return 0;
        if (memo[i][j] != null) return memo[i][j];
        int up = squaresEndingAt(i - 1, j);
        int left = squaresEndingAt(i, j - 1);
        int diagonal = squaresEndingAt(i - 1, j - 1);
        memo[i][j] = 1 + Math.min(up, Math.min(left, diagonal));
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", countSquares(new int[][]{{0, 1, 1, 1}, {1, 1, 1, 1}, {0, 1, 1, 1}}), 15);
        check("example 2", countSquares(new int[][]{{1, 0, 1}, {1, 1, 0}, {1, 1, 0}}), 7);
        check("2x2 ones", countSquares(new int[][]{{1, 1}, {1, 1}}), 5);
        check("single zero (edge)", countSquares(new int[][]{{0}}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
