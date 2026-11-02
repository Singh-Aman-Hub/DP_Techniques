/**
 * Cherry Pickup (LeetCode 741) — Memoization (top-down 3D DP)
 *
 * Idea: Same two-walkers recursion as the brute force: f(step, r1, r2) = most cherries
 *       collectable from this moment until both walkers reach (n-1, n-1). Columns are
 *       derived (c = step - r), so there are only (2n-1) * n * n states. Each one is
 *       cached the first time it is solved.
 *
 * Time complexity:  O(n^3) — (2n - 1) * n * n states, each doing O(1) work (4 choices).
 * Space complexity: O(n^3) — the memo table, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static final int NEG_INF = Integer.MIN_VALUE; // impossible state

    // memo[step][r1][r2] = most cherries from this state to the end (NEG_INF if impossible).
    private static int[][][] memo;
    // computed[step][r1][r2] = true once memo holds a real answer. A separate flag is
    // needed because every int value, even NEG_INF, can be a genuine answer.
    private static boolean[][][] computed;

    static int cherryPickup(int[][] grid) {
        int n = grid.length;
        memo = new int[2 * n - 1][n][n];
        computed = new boolean[2 * n - 1][n][n];
        return Math.max(0, collect(grid, 0, 0, 0));
    }

    private static int collect(int[][] grid, int step, int r1, int r2) {
        int n = grid.length;
        int c1 = step - r1;
        int c2 = step - r2;

        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n) {
            return NEG_INF; // off the grid
        }
        if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return NEG_INF; // thorn
        }
        if (computed[step][r1][r2]) {
            return memo[step][r1][r2]; // cache hit
        }

        int here = grid[r1][c1];
        if (r1 != r2) {
            here += grid[r2][c2]; // different cells -> both count
        }

        int answer;
        if (step == 2 * n - 2) {
            answer = here; // both at the bottom-right corner
        } else {
            int bestFuture = NEG_INF;
            bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1 + 1, r2 + 1));
            bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1 + 1, r2));
            bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1, r2 + 1));
            bestFuture = Math.max(bestFuture, collect(grid, step + 1, r1, r2));
            answer = (bestFuture == NEG_INF) ? NEG_INF : here + bestFuture;
        }

        computed[step][r1][r2] = true;
        memo[step][r1][r2] = answer;
        return answer;
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
