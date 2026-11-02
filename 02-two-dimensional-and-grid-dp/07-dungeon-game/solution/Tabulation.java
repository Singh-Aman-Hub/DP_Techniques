/**
 * Dungeon Game (LeetCode 174) — Tabulation (bottom-up DP, filled from the princess backward)
 *
 * Idea: need[row][col] = minimum health on entering (row, col). It depends on
 * the cell to the right and the cell below, so we fill from the bottom-right
 * corner toward the top-left. We pad the table with one extra row and column
 * filled with "infinity" (moves off the grid), and put 1 in the two padding
 * cells next to the princess, meaning "after her room, 1 health is enough".
 * Then every cell, including the princess's, uses the same formula.
 *
 * Time complexity:  O(m * n) — one O(1) update per cell.
 * Space complexity: O(m * n) — the (m + 1) x (n + 1) table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static int calculateMinimumHP(int[][] dungeon) {
        int rows = dungeon.length, cols = dungeon[0].length;

        // need[row][col] = minimum health the knight must have when entering (row, col)
        int[][] need = new int[rows + 1][cols + 1];
        for (int[] line : need) {
            Arrays.fill(line, Integer.MAX_VALUE); // off-grid: never the better choice
        }
        // The two virtual cells "after" the princess: leaving her room with 1 health is enough.
        need[rows][cols - 1] = 1;
        need[rows - 1][cols] = 1;

        for (int row = rows - 1; row >= 0; row--) {
            for (int col = cols - 1; col >= 0; col--) {
                int needAfter = Math.min(need[row + 1][col], need[row][col + 1]);
                need[row][col] = Math.max(1, needAfter - dungeon[row][col]);
            }
        }
        return need[0][0];
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
