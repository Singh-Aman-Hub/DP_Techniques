/**
 * Cherry Pickup (LeetCode 741) — Brute force recursion
 *
 * Idea: Replace "walk down to the corner and back up" by two walkers who both go
 *       from (0, 0) to (n-1, n-1) at the same time, one cell per step. After `step`
 *       steps a walker in row r is in column step - r, so the state is (step, r1, r2).
 *       Each walker moves right or down: 2 x 2 = 4 combined choices. A cell both
 *       walkers stand on is counted once. Thorns (-1) and off-grid cells make a
 *       plan impossible, which we mark with NEG_INF.
 *
 * Time complexity:  O(4^(2n)) — 2n - 2 steps, 4 branches per step, nothing cached.
 * Space complexity: O(n) — recursion depth is 2n - 1 steps.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    // "Minus infinity": this state cannot be part of any valid pair of walks.
    // We never add to it (that could overflow); we test for it explicitly.
    private static final int NEG_INF = Integer.MIN_VALUE;

    static int cherryPickup(int[][] grid) {
        int best = collect(grid, 0, 0, 0);
        // If no valid round trip exists, LeetCode wants 0.
        return Math.max(0, best);
    }

    // Most cherries the two walkers can still pick up, given that after `step` steps
    // walker 1 is in row r1 and walker 2 is in row r2 (cells at that moment included).
    private static int collect(int[][] grid, int step, int r1, int r2) {
        int n = grid.length;
        int c1 = step - r1; // derived: a walker has made r1 down-moves and c1 right-moves
        int c2 = step - r2;

        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n) {
            return NEG_INF; // walked off the bottom or the right edge
        }
        if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return NEG_INF; // stepped on a thorn
        }

        int here = grid[r1][c1];
        if (r1 != r2) {
            here += grid[r2][c2]; // same row on the same step means same cell: count once
        }

        if (step == 2 * n - 2) {
            return here; // both walkers are at (n-1, n-1)
        }

        // 4 combinations: each walker goes down (row + 1) or right (row stays).
        int bestFuture = NEG_INF;
        bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1 + 1, r2 + 1)); // down, down
        bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1 + 1, r2));     // down, right
        bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1, r2 + 1));     // right, down
        bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1, r2));         // right, right

        if (bestFuture == NEG_INF) {
            return NEG_INF; // every continuation is blocked
        }
        return here + bestFuture;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example1 = {{0, 1, -1}, {1, 0, -1}, {1, 1, 1}};
        int[][] blocked = {{1, 1, -1}, {1, -1, 1}, {-1, 1, 1}};
        int[][] single = {{1}};
        int[][] greedyTrap = {{0, 0, 0, 0}, {0, 0, 1, 0}, {1, 1, 1, 1}, {0, 1, 0, 0}};

        check("example 1 (3x3)", cherryPickup(example1), 5);
        check("no path to the corner", cherryPickup(blocked), 0);
        check("edge: single cell", cherryPickup(single), 1);
        check("greedy trap (4x4)", cherryPickup(greedyTrap), 6);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
