/**
 * Triangle (LeetCode 120) — Brute force recursion
 *
 * Idea: from cell (row, col) we may step to (row + 1, col) or (row + 1, col + 1).
 * The cheapest path starting at (row, col) is the cell's own value plus the
 * cheaper of the two cheapest paths below it. We simply try both, recursively.
 *
 * Time complexity:  O(2^n) — every call makes two calls, and the recursion is n levels deep.
 * Space complexity: O(n)   — the call stack holds at most one frame per row.
 *
 * The triangle is stored as a jagged int[][] (row r has r + 1 entries).
 * On LeetCode the input is a List<List<Integer>>; the logic is identical.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    // Cheapest total of a path that starts at (row, col) and ends anywhere in the last row.
    static int minPathFrom(int[][] triangle, int row, int col) {
        // Base case: the last row has nothing below it, so the path is just this cell.
        if (row == triangle.length - 1) {
            return triangle[row][col];
        }
        int goStraightDown = minPathFrom(triangle, row + 1, col);
        int goDownRight = minPathFrom(triangle, row + 1, col + 1);
        return triangle[row][col] + Math.min(goStraightDown, goDownRight);
    }

    static int minimumTotal(int[][] triangle) {
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
