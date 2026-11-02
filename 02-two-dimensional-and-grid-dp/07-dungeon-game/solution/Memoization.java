/**
 * Dungeon Game (LeetCode 174) — Memoization (top-down DP, reverse direction)
 *
 * Idea: same recurrence as the brute force, but each cell's need(row, col) is
 * cached. A cell is reached from its left neighbour and from the cell above,
 * so without the cache it would be recomputed many times.
 *
 * Time complexity:  O(m * n) — each of the m * n cells is solved once with O(1) work.
 * Space complexity: O(m * n) — the memo table, plus O(m + n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[row][col] = minimum health needed on entering (row, col); 0 = not computed yet.
    // 0 is a safe marker because every real answer is at least 1.
    private static int[][] memo;

    static int need(int[][] dungeon, int row, int col) {
        int rows = dungeon.length, cols = dungeon[0].length;
        if (row == rows - 1 && col == cols - 1) {
            return Math.max(1, 1 - dungeon[row][col]);
        }
        if (memo[row][col] != 0) {
            return memo[row][col]; // cache hit
        }
        int needDown = (row + 1 < rows) ? need(dungeon, row + 1, col) : Integer.MAX_VALUE;
        int needRight = (col + 1 < cols) ? need(dungeon, row, col + 1) : Integer.MAX_VALUE;
        memo[row][col] = Math.max(1, Math.min(needDown, needRight) - dungeon[row][col]);
        return memo[row][col];
    }

    static int calculateMinimumHP(int[][] dungeon) {
        memo = new int[dungeon.length][dungeon[0].length];
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
