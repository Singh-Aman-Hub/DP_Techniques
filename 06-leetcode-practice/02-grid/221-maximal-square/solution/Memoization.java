/**
 * 221. Maximal Square — Memoization (top-down)
 *
 * Idea: side(i, j) = side of the largest all-'1' square with bottom-right corner (i, j).
 * It is 1 + min(side above, side left, side diagonal) when the cell is '1'. Cache each cell,
 * ask every cell, and square the largest side.
 *
 * Time complexity:  O(m * n) — each cell computed once with O(1) work.
 * Space complexity: O(m * n) — the memo table plus recursion depth up to m + n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static char[][] matrix;
    private static Integer[][] memo;   // memo[i][j] = largest square side ending at (i, j)

    static int maximalSquare(char[][] grid) {
        matrix = grid;
        memo = new Integer[grid.length][grid[0].length];
        int bestSide = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                bestSide = Math.max(bestSide, side(i, j));
            }
        }
        return bestSide * bestSide;
    }

    private static int side(int i, int j) {
        if (i < 0 || j < 0) return 0;                  // outside the grid: no square
        if (matrix[i][j] == '0') return 0;
        if (memo[i][j] != null) return memo[i][j];
        int up = side(i - 1, j);
        int left = side(i, j - 1);
        int diagonal = side(i - 1, j - 1);
        memo[i][j] = 1 + Math.min(up, Math.min(left, diagonal));   // smallest neighbour limits us
        return memo[i][j];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example grid", maximalSquare(grid("10100", "10111", "11111", "10010")), 4);
        check("diagonal ones", maximalSquare(grid("01", "10")), 1);
        check("single zero (edge)", maximalSquare(grid("0")), 0);
        check("all ones 3x3", maximalSquare(grid("111", "111", "111")), 9);
    }

    private static char[][] grid(String... rows) {
        char[][] g = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray();
        return g;
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
