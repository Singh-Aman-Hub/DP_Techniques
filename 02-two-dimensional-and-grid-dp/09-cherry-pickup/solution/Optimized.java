/**
 * Cherry Pickup (LeetCode 741) — Space-optimized DP (one n x n layer per step)
 *
 * Idea: In the forward tabulation, step s reads only step s - 1. So we keep two
 *       n x n arrays over (r1, r2): `previous` (step s - 1) and `current` (step s),
 *       and swap them after every step.
 *
 * Time complexity:  O(n^3) — still (2n - 1) * n * n states with 4 predecessors each.
 * Space complexity: O(n^2) — two n x n layers instead of 2n - 1 of them.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    private static final int NEG_INF = Integer.MIN_VALUE; // unreachable / blocked state

    static int cherryPickup(int[][] grid) {
        int n = grid.length;

        // previous[r1][r2] = best total after the previous step with walkers in rows r1, r2.
        int[][] previous = new int[n][n];
        for (int[] line : previous) {
            Arrays.fill(line, NEG_INF);
        }
        previous[0][0] = grid[0][0];

        for (int step = 1; step <= 2 * n - 2; step++) {
            // current[r1][r2] = same meaning for this step; start as "unreachable".
            int[][] current = new int[n][n];
            for (int[] line : current) {
                Arrays.fill(line, NEG_INF);
            }
            int lowRow = Math.max(0, step - (n - 1));
            int highRow = Math.min(n - 1, step);
            for (int r1 = lowRow; r1 <= highRow; r1++) {
                for (int r2 = lowRow; r2 <= highRow; r2++) {
                    int c1 = step - r1;
                    int c2 = step - r2;
                    if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
                        continue;
                    }
                    int bestPrevious = NEG_INF;
                    for (int previous1 = r1 - 1; previous1 <= r1; previous1++) {
                        for (int previous2 = r2 - 1; previous2 <= r2; previous2++) {
                            if (previous1 >= 0 && previous2 >= 0) {
                                bestPrevious = Math.max(bestPrevious, previous[previous1][previous2]);
                            }
                        }
                    }
                    if (bestPrevious == NEG_INF) {
                        continue;
                    }
                    int here = grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0);
                    current[r1][r2] = bestPrevious + here;
                }
            }
            previous = current; // the finished step becomes "previous" for the next one
        }
        return Math.max(0, previous[n - 1][n - 1]);
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
