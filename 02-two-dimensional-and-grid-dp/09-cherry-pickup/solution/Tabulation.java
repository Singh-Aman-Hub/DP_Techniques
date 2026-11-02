/**
 * Cherry Pickup (LeetCode 741) — Tabulation (forward 3D DP)
 *
 * Idea: Walk the two walkers forward in time. dp[step][r1][r2] = most cherries the
 *       pair can have collected when, after `step` steps, walker 1 is in row r1 and
 *       walker 2 is in row r2 (columns derived as step - row). Step s reads only
 *       step s - 1, so we fill step by step. The answer is dp[2n-2][n-1][n-1].
 *
 * Time complexity:  O(n^3) — (2n - 1) * n * n states, 4 predecessors each.
 * Space complexity: O(n^3) — the full 3D table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int NEG_INF = Integer.MIN_VALUE; // unreachable / blocked state

    static int cherryPickup(int[][] grid) {
        int n = grid.length;
        int steps = 2 * n - 1;

        // dp[step][r1][r2] = best total collected so far by both walkers, or NEG_INF
        // if the walkers cannot be at those cells at that step.
        int[][][] dp = new int[steps][n][n];
        for (int[][] layer : dp) {
            for (int[] line : layer) {
                Arrays.fill(line, NEG_INF);
            }
        }
        dp[0][0][0] = grid[0][0]; // both start on (0, 0); the problem guarantees it is not a thorn

        for (int step = 1; step < steps; step++) {
            // Row r is valid at this step only if its column step - r is inside 0..n-1.
            int lowRow = Math.max(0, step - (n - 1));
            int highRow = Math.min(n - 1, step);
            for (int r1 = lowRow; r1 <= highRow; r1++) {
                for (int r2 = lowRow; r2 <= highRow; r2++) {
                    int c1 = step - r1;
                    int c2 = step - r2;
                    if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
                        continue; // thorn: stays NEG_INF
                    }
                    // Each walker arrived either from above (row - 1) or from the left (same row).
                    int bestPrevious = NEG_INF;
                    for (int previous1 = r1 - 1; previous1 <= r1; previous1++) {
                        for (int previous2 = r2 - 1; previous2 <= r2; previous2++) {
                            if (previous1 >= 0 && previous2 >= 0) {
                                bestPrevious = Math.max(bestPrevious, dp[step - 1][previous1][previous2]);
                            }
                        }
                    }
                    if (bestPrevious == NEG_INF) {
                        continue; // no valid way to get here
                    }
                    int here = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0); // shared cell once
                    dp[step][r1][r2] = bestPrevious + here;
                }
            }
        }
        return Math.max(0, dp[steps - 1][n - 1][n - 1]);
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
