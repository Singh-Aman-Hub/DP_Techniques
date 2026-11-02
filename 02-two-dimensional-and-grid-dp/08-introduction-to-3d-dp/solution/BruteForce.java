/**
 * Cherry Pickup II (LeetCode 1463) — Brute force recursion
 *
 * Idea: Both robots move down one row per step, so one number `row` describes
 *       "when" we are, and two numbers col1, col2 describe "where" each robot is.
 *       From (row, col1, col2) each robot picks one of 3 moves (down-left, down,
 *       down-right): 3 x 3 = 9 combined choices. We try all of them and keep the best.
 *       If both robots stand on the same cell, its cherries are counted once.
 *
 * Time complexity:  O(9^rows) — every level of the recursion branches 9 ways and
 *                   nothing is remembered, so the same (row, col1, col2) is solved many times.
 * Space complexity: O(rows) — only the recursion stack, one frame per row.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int cherryPickup(int[][] grid) {
        int cols = grid[0].length;
        // Robot 1 starts in the top-left corner, robot 2 in the top-right corner.
        return collect(grid, 0, 0, cols - 1);
    }

    // Returns the most cherries both robots can still collect, given that
    // robot 1 is at (row, col1) and robot 2 is at (row, col2) right now.
    private static int collect(int[][] grid, int row, int col1, int col2) {
        int rows = grid.length;
        int cols = grid[0].length;

        // A robot that walked off the left or right edge is an impossible plan.
        // Returning a very negative value makes max() ignore this branch.
        if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            return Integer.MIN_VALUE / 2;
        }

        // Cherries on the two current cells; a shared cell is picked only once.
        int here = grid[row][col1];
        if (col1 != col2) {
            here += grid[row][col2];
        }

        // Base case: the bottom row, nothing more to collect after this.
        if (row == rows - 1) {
            return here;
        }

        // Try all 9 combinations of moves (each robot: -1, 0, +1 column).
        int bestFuture = Integer.MIN_VALUE / 2;
        for (int move1 = -1; move1 <= 1; move1++) {
            for (int move2 = -1; move2 <= 1; move2++) {
                int future = collect(grid, row + 1, col1 + move1, col2 + move2);
                bestFuture = Math.max(bestFuture, future);
            }
        }
        return here + bestFuture;
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
