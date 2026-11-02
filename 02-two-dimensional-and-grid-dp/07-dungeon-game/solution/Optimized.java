/**
 * Dungeon Game (LeetCode 174) — Space-optimized DP (one row of length n + 1)
 *
 * Idea: row r of the table reads only row r + 1 (the cell below) and the cell
 * to its right in row r itself. Sweeping each row right to left over a single
 * array, below[col] still holds row r + 1's value when we read it, and
 * below[col + 1] already holds row r's value — exactly the "right" neighbour.
 *
 * Time complexity:  O(m * n) — one O(1) update per cell.
 * Space complexity: O(n)     — one array of length n + 1.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    static int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length, cols = dungeon[0].length;

        // need[col] = minimum health on entering (current row, col); index cols is the off-grid padding
        int[] need = new int[cols + 1];
        Arrays.fill(need, Integer.MAX_VALUE);
        need[cols - 1] = 1; // virtual cell below the princess (before any row is processed)

        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                // need[col] = cell below (old value), need[col + 1] = cell to the right (new value)
                int needAfter = Math.min(need[col], need[col + 1]);
                need[col] = Math.max(1, needAfter - dungeon[row][col]);
            }
        }
        return need[0];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("3x3 example", calculateMinimumHP(new int[][]{{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}}), 7);
        check("single cell 0 (edge)", calculateMinimumHP(new int[][]{{0}}), 1);
        check("single cell -3", calculateMinimumHP(new int[][]{{-3}}), 4);
        check("single cell +100", calculateMinimumHP(new int[][]{{100}}), 1);
        check("one row", calculateMinimumHP(new int[][]{{-2, -3, 3}}), 6);
        check("grid A (future drain)", calculateMinimumHP(new int[][]{{0, -2, 7}, {0, 0, 0}, {-100, -100, -6}}), 3);
        check("grid B (no drain)", calculateMinimumHP(new int[][]{{0, -2, 7}, {0, 0, 0}, {-100, -100, 0}}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
