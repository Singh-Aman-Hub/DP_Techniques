/**
 * Cherry Pickup II (LeetCode 1463) — Tabulation (bottom-up 3D DP)
 *
 * Idea: dp[row][col1][col2] depends only on layer row + 1, so we fill the 3D table
 *       one row-layer at a time from the bottom row upward. Inside a layer the
 *       order of (col1, col2) does not matter. The answer is dp[0][0][cols - 1].
 *
 * Time complexity:  O(rows * cols^2 * 9) — three nested loops over all states and
 *                   9 move combinations for each.
 * Space complexity: O(rows * cols^2) — the full 3D table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        // dp[row][col1][col2] = most cherries collectable from this row down to the
        // bottom when robot 1 stands in column col1 and robot 2 in column col2.
        int[][][] dp = new int[rows][cols][cols];

        for (int row = rows - 1; row >= 0; row--) {
            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {
                    int here = grid[row][col1];
                    if (col1 != col2) {
                        here += grid[row][col2]; // shared cell counted once
                    }
                    if (row == rows - 1) {
                        dp[row][col1][col2] = here; // base case: bottom layer
                        continue;
                    }
                    int bestFuture = 0; // every in-grid next state is >= 0, and at least one exists
                    for (int move1 = -1; move1 <= 1; move1++) {
                        for (int move2 = -1; move2 <= 1; move2++) {
                            int next1 = col1 + move1;
                            int next2 = col2 + move2;
                            if (next1 < 0 || next1 >= cols || next2 < 0 || next2 >= cols) {
                                continue; // that move leaves the grid
                            }
                            bestFuture = Math.max(bestFuture, dp[row + 1][next1][next2]);
                        }
                    }
                    dp[row][col1][col2] = here + bestFuture;
                }
            }
        }
        return dp[0][0][cols - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example1 = {{3, 1, 1}, {2, 5, 1}, {1, 5, 5}, {2, 1, 1}};
        int[][] example2 = {
            {1, 0, 0, 0, 0, 0, 1},
            {2, 0, 0, 0, 0, 3, 0},
            {2, 0, 9, 0, 0, 0, 0},
            {0, 3, 0, 5, 4, 0, 0},
            {1, 0, 2, 3, 0, 0, 6}
        };
        int[][] tiny = {{1, 1}, {1, 1}};
        int[][] empty = {{0, 0}, {0, 0}};

        check("example 1 (4x3)", cherryPickup(example1), 24);
        check("example 2 (5x7)", cherryPickup(example2), 28);
        check("edge: 2x2 all ones", cherryPickup(tiny), 4);
        check("edge: 2x2 no cherries", cherryPickup(empty), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
