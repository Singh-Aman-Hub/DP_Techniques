/**
 * Dungeon Game (LeetCode 174) — Brute force recursion (reverse direction)
 *
 * Idea: need(row, col) = the minimum health the knight must have when he STEPS
 * ONTO (row, col) so that he can survive from here to the princess. Looking
 * forward from a cell, he goes right or down, whichever needs less health.
 * After the cell changes his health by dungeon[row][col], he must still have at
 * least that much, and he must always have at least 1:
 *     need(row, col) = max(1, min(need(right), need(down)) - dungeon[row][col])
 *
 * Time complexity:  O(2^(m+n)) — two recursive calls per cell along paths of length m + n - 1.
 * Space complexity: O(m + n)   — the recursion depth equals the path length.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int need(int[][] dungeon, int row, int col) {
        int rows = dungeon.length, cols = dungeon[0].length;
        // Base case: the princess's room. After this room he needs exactly 1 health left.
        if (row == rows - 1 && col == cols - 1) {
            return Math.max(1, 1 - dungeon[row][col]);
        }
        // A move off the grid is impossible: give it "infinite" need so min() never picks it.
        int needDown = (row + 1 < rows) ? need(dungeon, row + 1, col) : Integer.MAX_VALUE;
        int needRight = (col + 1 < cols) ? need(dungeon, row, col + 1) : Integer.MAX_VALUE;
        int needAfterThisRoom = Math.min(needDown, needRight); // at least one move is always real
        return Math.max(1, needAfterThisRoom - dungeon[row][col]);
    }

    static int calculateMinimumHP(int[][] dungeon) {
        return need(dungeon, 0, 0);
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
