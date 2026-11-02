/**
 * Cherry Pickup II (LC 1463) — 3D DP rolled down to two 2D layers
 *
 * Idea: dp[r][c1][c2] = most cherries the two robots can still collect from row r
 * to the bottom, if robot 1 stands at column c1 and robot 2 at column c2 in row r.
 * Layer r reads ONLY layer r+1, so we keep two layers and pick them with r % 2:
 * layer[r % 2] is being written, layer[(r + 1) % 2] holds row r+1.
 *
 * Time complexity:  O(R·C²·9) = O(R·C²) for both versions — R·C² states, 9 move pairs each.
 * Space complexity: O(R·C²) for the full 3D table, O(C²) for the rolling version (2·C² cells).
 *
 * Run: java CherryPickupII.java
 */
import java.util.Random;

public class CherryPickupII {

    /** Cherries collected in row r when the robots stand at c1 and c2 (a shared cell counts once). */
    private static int cherriesAt(int[][] grid, int r, int c1, int c2) {
        return (c1 == c2) ? grid[r][c1] : grid[r][c1] + grid[r][c2];
    }

    /** Full 3D table, bottom row first. */
    static int cherryPickupFull(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        // dp[r][c1][c2] = best total from row r downwards with robots at (r,c1) and (r,c2)
        int[][][] dp = new int[rows][cols][cols];
        for (int r = rows - 1; r >= 0; r--) {
            for (int c1 = 0; c1 < cols; c1++) {
                for (int c2 = 0; c2 < cols; c2++) {
                    int bestBelow = 0; // the last row has nothing below it
                    if (r + 1 < rows) {
                        bestBelow = Integer.MIN_VALUE;
                        for (int d1 = -1; d1 <= 1; d1++) {
                            for (int d2 = -1; d2 <= 1; d2++) {
                                int n1 = c1 + d1, n2 = c2 + d2;
                                if (n1 < 0 || n1 >= cols || n2 < 0 || n2 >= cols) continue;
                                bestBelow = Math.max(bestBelow, dp[r + 1][n1][n2]);
                            }
                        }
                    }
                    dp[r][c1][c2] = cherriesAt(grid, r, c1, c2) + bestBelow;
                }
            }
        }
        return dp[0][0][cols - 1];
    }

    /** Rolling version: only two C x C layers, selected by r % 2. */
    static int cherryPickupRolling(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        // layer[r % 2][c1][c2] = dp[r][c1][c2]; the other layer holds dp[r+1]
        int[][][] layer = new int[2][cols][cols];
        for (int r = rows - 1; r >= 0; r--) {
            int[][] current = layer[r % 2];
            int[][] below = layer[(r + 1) % 2];
            for (int c1 = 0; c1 < cols; c1++) {
                for (int c2 = 0; c2 < cols; c2++) {
                    int bestBelow = 0;
                    if (r + 1 < rows) {
                        bestBelow = Integer.MIN_VALUE;
                        for (int d1 = -1; d1 <= 1; d1++) {
                            for (int d2 = -1; d2 <= 1; d2++) {
                                int n1 = c1 + d1, n2 = c2 + d2;
                                if (n1 < 0 || n1 >= cols || n2 < 0 || n2 >= cols) continue;
                                bestBelow = Math.max(bestBelow, below[n1][n2]);
                            }
                        }
                    }
                    // Every cell of "current" is overwritten (pull style), so no reset is needed here.
                    current[c1][c2] = cherriesAt(grid, r, c1, c2) + bestBelow;
                }
            }
        }
        return layer[0][0][cols - 1]; // row 0 lives in layer 0 % 2 = 0
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] example = { {3, 1, 1}, {2, 5, 1}, {1, 5, 5}, {2, 1, 1} };
        check("full    4x3 example", cherryPickupFull(example), 24);
        check("rolling 4x3 example", cherryPickupRolling(example), 24);

        int[][] wide = {
            {1, 0, 0, 0, 0, 0, 1},
            {2, 0, 0, 0, 0, 3, 0},
            {2, 0, 9, 0, 0, 0, 0},
            {0, 3, 0, 5, 4, 0, 0},
            {1, 0, 2, 3, 0, 0, 6}
        };
        check("full    5x7 example", cherryPickupFull(wide), 28);
        check("rolling 5x7 example", cherryPickupRolling(wide), 28);

        int[][] oneRow = { {4, 7} };
        check("rolling single row edge", cherryPickupRolling(oneRow), 11);

        int[][] oneColumn = { {5}, {6} };
        check("rolling single column edge (shared cell counted once)", cherryPickupRolling(oneColumn), 11);

        Random random = new Random(1463);
        boolean allAgree = true;
        for (int trial = 0; trial < 500 && allAgree; trial++) {
            int rows = 1 + random.nextInt(6), cols = 1 + random.nextInt(6);
            int[][] grid = new int[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) grid[r][c] = random.nextInt(10);
            allAgree = cherryPickupFull(grid) == cherryPickupRolling(grid);
        }
        check("500 random grids agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
