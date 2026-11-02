/**
 * 688. Knight Probability in Chessboard — Tabulation (bottom-up)
 *
 * Idea: dp[k][r][c] = survival probability from (r, c) with k moves left. Layer 0 is all 1.0;
 * layer k averages layer k-1 over the 8 knight moves that stay on the board.
 *
 * Time complexity:  O(k * n^2) — k layers, n^2 squares, 8 moves each.
 * Space complexity: O(k * n^2) — the full table.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int[][] MOVES = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

    static double knightProbability(int n, int k, int row, int column) {
        // dp[step][r][c] = probability of staying on the board for `step` more moves from (r, c)
        double[][][] dp = new double[k + 1][n][n];
        for (double[] line : dp[0]) Arrays.fill(line, 1.0);
        for (int step = 1; step <= k; step++) {
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    double probability = 0.0;
                    for (int[] m : MOVES) {
                        int nr = r + m[0], nc = c + m[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < n) {   // off-board moves add 0
                            probability += dp[step - 1][nr][nc] / 8.0;
                        }
                    }
                    dp[step][r][c] = probability;
                }
            }
        }
        return dp[k][row][column];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=3, k=2, (0,0)", knightProbability(3, 2, 0, 0), 0.0625);
        check("n=1, k=0 (edge)", knightProbability(1, 0, 0, 0), 1.0);
        check("n=1, k=1", knightProbability(1, 1, 0, 0), 0.0);
        check("n=8, k=1, corner", knightProbability(8, 1, 0, 0), 0.25);
    }

    private static void check(String label, double actual, double expected) {
        boolean ok = Math.abs(actual - expected) < 1e-9;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
