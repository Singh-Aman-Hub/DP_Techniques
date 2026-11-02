/**
 * Triangle (LeetCode 120) — Memoization (top-down DP)
 *
 * Idea: the brute force solves the same (row, col) many times, because a cell
 * can be reached from the cell above-left AND the cell directly above. We cache
 * the answer of each (row, col) the first time we compute it.
 *
 * Time complexity:  O(n^2) — there are 1 + 2 + ... + n = n(n+1)/2 cells, each solved once with O(1) work.
 * Space complexity: O(n^2) — the memo table has one slot per cell, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[row][col] = cheapest path total from (row, col) down to the last row.
    // Integer (not int) so that null can mean "not computed yet"; any int value,
    // even a negative one, is a legal answer, so we cannot use -1 as a marker.
    private static Integer[][] memo;

    static int minPathFrom(int[][] triangle, int row, int col) {
        if (row == triangle.length - 1) {
            return triangle[row][col];
        }
        if (memo[row][col] != null) {
            return memo[row][col]; // cache hit: this cell was already solved
        }
        int goStraightDown = minPathFrom(triangle, row + 1, col);
        int goDownRight = minPathFrom(triangle, row + 1, col + 1);
        memo[row][col] = triangle[row][col] + Math.min(goStraightDown, goDownRight);
        return memo[row][col];
    }

    static int minimumTotal(int[][] triangle) {
        int rows = triangle.length;
        memo = new Integer[rows][];
        for (int r = 0; r < rows; r++) {
            memo[r] = new Integer[r + 1]; // jagged, like the triangle itself
        }
        return minPathFrom(triangle, 0, 0);
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
