/**
 * 688. Knight Probability in Chessboard — Space-optimized (two layers)
 *
 * Idea: Each layer needs only the previous one, so keep two n x n grids and swap them.
 *
 * Time complexity:  O(k * n^2) — same as tabulation.
 * Space complexity: O(n^2) — two layers.
 *
 * Run: java Optimized.java
 */
import java.util.Arrays;

public class Optimized {

    private static final int[][] MOVES = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};

    static double knightProbability(int n, int k, int row, int column) {
        double[][] previous = new double[n][n];   // survival probability with (step - 1) moves left
        for (double[] line : previous) Arrays.fill(line, 1.0);
        for (int step = 1; step <= k; step++) {
            double[][] current = new double[n][n];
            for (int r = 0; r < n; r++) {
                for (int c = 0; c < n; c++) {
                    for (int[] m : MOVES) {
                        int nr = r + m[0], nc = c + m[1];
                        if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                            current[r][c] += previous[nr][nc] / 8.0;
                        }
                    }
                }
            }
            previous = current;
        }
        return previous[row][column];
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
