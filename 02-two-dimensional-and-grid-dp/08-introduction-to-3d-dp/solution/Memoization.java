/**
 * Cherry Pickup II (LeetCode 1463) — Memoization (top-down 3D DP)
 *
 * Idea: Same recursion as the brute force, f(row, col1, col2) = best cherries from
 *       here to the bottom. There are only rows * cols * cols different states, so
 *       we store each answer in a 3D array the first time we compute it and reuse it.
 *
 * Time complexity:  O(rows * cols^2 * 9) — each of the rows*cols*cols states is
 *                   computed once and looks at 9 move combinations.
 * Space complexity: O(rows * cols^2) — the 3D memo table, plus O(rows) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[row][col1][col2] = most cherries collectable from row to the bottom when
    // robot 1 is at column col1 and robot 2 at column col2 of that row; -1 = not computed.
    private static int[][][] memo;

    static int cherryPickup(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        memo = new int[rows][cols][cols];
        for (int[][] layer : memo) {
            for (int[] line : layer) {
                Arrays.fill(line, -1); // cherries are never negative, so -1 is a safe "empty" marker
            }
        }
        return collect(grid, 0, 0, cols - 1);
    }

    private static int collect(int[][] grid, int row, int col1, int col2) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Off the grid: impossible plan, make max() ignore it.
        if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            return Integer.MIN_VALUE / 2;
        }
        if (memo[row][col1][col2] != -1) {
            return memo[row][col1][col2]; // cache hit: this state was solved before
        }

        int here = grid[row][col1];
        if (col1 != col2) {
            here += grid[row][col2]; // two different cells -> both counted
        }

        int answer;
        if (row == rows - 1) {
            answer = here; // bottom row: no future
        } else {
            int bestFuture = Integer.MIN_VALUE / 2;
            for (int move1 = -1; move1 <= 1; move1++) {
                for (int move2 = -1; move2 <= 1; move2++) {
                    bestFuture = Math.max(bestFuture, collect(grid, row + 1, col1 + move1, col2 + move2));
                }
            }
            answer = here + bestFuture;
        }
        memo[row][col1][col2] = answer;
        return answer;
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
