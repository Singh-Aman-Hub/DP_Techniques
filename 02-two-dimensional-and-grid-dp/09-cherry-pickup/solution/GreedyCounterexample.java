/**
 * Cherry Pickup (LeetCode 741) — Why "best path twice" is wrong
 *
 * Idea: The tempting greedy plan is: (1) find the single down/right path with the
 *       most cherries, (2) erase those cherries, (3) find the best path again on the
 *       erased grid. This program runs that greedy plan and the correct two-walker DP
 *       on the same grids and checks that the greedy plan falls short on the trap grid.
 *
 *       Trap grid (4x4):        Greedy first path takes all of row 2 (4 cherries).
 *         0 0 0 0               The two cherries left, (1,2) and (3,1), cannot be on
 *         0 0 1 0               one down/right path, so the second trip gets only 1.
 *         1 1 1 1               Greedy total = 5. The best pair of trips splits row 2
 *         0 1 0 0               between them and gets all 6.
 *
 * Time complexity:  O(n^2) for each single-path DP, O(n^3) for the two-walker DP.
 * Space complexity: O(n^2) for the single-path table and the copied grid.
 *
 * Run: java GreedyCounterexample.java
 */
import java.util.Arrays;

public class GreedyCounterexample {

    private static final int NEG_INF = Integer.MIN_VALUE;

    // ---------- the greedy plan ----------
    static int greedyTwoPasses(int[][] original) {
        int n = original.length;
        int[][] grid = new int[n][];
        for (int r = 0; r < n; r++) {
            grid[r] = original[r].clone(); // we will erase cherries, so work on a copy
        }
        int first = bestSinglePathAndErase(grid);
        if (first == NEG_INF) {
            return 0; // no path at all
        }
        int second = bestSinglePathAndErase(grid);
        return first + second;
    }

    // Finds the best (0,0) -> (n-1,n-1) path moving only right/down, sets its cells to 0,
    // and returns how many cherries it held (NEG_INF if no path exists).
    private static int bestSinglePathAndErase(int[][] grid) {
        int n = grid.length;
        // best[r][c] = most cherries on a path from (0,0) to (r,c); NEG_INF if unreachable.
        int[][] best = new int[n][n];
        for (int[] line : best) {
            Arrays.fill(line, NEG_INF);
        }
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == -1) {
                    continue;
                }
                if (r == 0 && c == 0) {
                    best[r][c] = grid[r][c];
                    continue;
                }
                int fromAbove = (r > 0) ? best[r - 1][c] : NEG_INF;
                int fromLeft = (c > 0) ? best[r][c - 1] : NEG_INF;
                int bestBefore = Math.max(fromAbove, fromLeft);
                if (bestBefore != NEG_INF) {
                    best[r][c] = bestBefore + grid[r][c];
                }
            }
        }
        int total = best[n - 1][n - 1];
        if (total == NEG_INF) {
            return NEG_INF;
        }
        // Walk back from the corner to erase the chosen path's cherries.
        int r = n - 1;
        int c = n - 1;
        while (true) {
            grid[r][c] = 0;
            if (r == 0 && c == 0) {
                break;
            }
            int fromAbove = (r > 0) ? best[r - 1][c] : NEG_INF;
            int fromLeft = (c > 0) ? best[r][c - 1] : NEG_INF;
            if (fromAbove >= fromLeft) {
                r--;
            } else {
                c--;
            }
        }
        return total;
    }

    // ---------- the correct two-walker DP (same as Optimized.java) ----------
    static int twoWalkersOptimal(int[][] grid) {
        int n = grid.length;
        int[][] previous = new int[n][n];
        for (int[] line : previous) {
            Arrays.fill(line, NEG_INF);
        }
        previous[0][0] = grid[0][0];
        for (int step = 1; step <= 2 * n - 2; step++) {
            int[][] current = new int[n][n];
            for (int[] line : current) {
                Arrays.fill(line, NEG_INF);
            }
            for (int r1 = Math.max(0, step - (n - 1)); r1 <= Math.min(n - 1, step); r1++) {
                for (int r2 = Math.max(0, step - (n - 1)); r2 <= Math.min(n - 1, step); r2++) {
                    int c1 = step - r1;
                    int c2 = step - r2;
                    if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
                        continue;
                    }
                    int bestPrevious = NEG_INF;
                    for (int p1 = r1 - 1; p1 <= r1; p1++) {
                        for (int p2 = r2 - 1; p2 <= r2; p2++) {
                            if (p1 >= 0 && p2 >= 0) {
                                bestPrevious = Math.max(bestPrevious, previous[p1][p2]);
                            }
                        }
                    }
                    if (bestPrevious != NEG_INF) {
                        current[r1][r2] = bestPrevious + grid[r1][c1] + (r1 != r2 ? grid[r2][c2] : 0);
                    }
                }
            }
            previous = current;
        }
        return Math.max(0, previous[n - 1][n - 1]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] trap = {{0, 0, 0, 0}, {0, 0, 1, 0}, {1, 1, 1, 1}, {0, 1, 0, 0}};
        int[][] example1 = {{0, 1, -1}, {1, 0, -1}, {1, 1, 1}};

        int greedyTrap = greedyTwoPasses(trap);
        int optimalTrap = twoWalkersOptimal(trap);
        check("trap grid, greedy two-pass total", greedyTrap, 5);
        check("trap grid, two-walker DP total", optimalTrap, 6);
        check("trap grid, greedy is strictly worse (1 = yes)", greedyTrap < optimalTrap ? 1 : 0, 1);

        // Greedy is not always wrong; on LeetCode's first example it happens to match.
        check("example 1, greedy two-pass total", greedyTwoPasses(example1), 5);
        check("example 1, two-walker DP total", twoWalkersOptimal(example1), 5);
        check("edge: single cell, greedy", greedyTwoPasses(new int[][]{{1}}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
