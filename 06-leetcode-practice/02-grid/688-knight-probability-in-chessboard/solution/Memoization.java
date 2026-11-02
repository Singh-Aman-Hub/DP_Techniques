/**
 * 688. Knight Probability in Chessboard — Memoization (top-down)
 *
 * Idea: stay(k, r, c) = probability a knight on (r, c) is still on the board after k more random
 * moves. Average stay(k-1, ...) over the 8 equally likely moves; off-board squares give 0.
 * The knight can revisit squares, so k is part of the state.
 *
 * Time complexity:  O(k * n^2) — each state once, 8 moves each.
 * Space complexity: O(k * n^2) — memo cube plus recursion depth k.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static final int[][] MOVES = {{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
    private static int size;
    private static double[][][] memo;   // memo[k][r][c] = survival probability; -1 = unknown

    static double knightProbability(int n, int k, int row, int column) {
        size = n;
        memo = new double[k + 1][n][n];
        for (double[][] layer : memo) for (double[] line : layer) Arrays.fill(line, -1);
        return stay(k, row, column);
    }

    private static double stay(int movesLeft, int r, int c) {
        if (r < 0 || r >= size || c < 0 || c >= size) return 0.0;   // fell off the board
        if (movesLeft == 0) return 1.0;                               // finished on the board
        if (memo[movesLeft][r][c] >= 0) return memo[movesLeft][r][c];
        double probability = 0.0;
        for (int[] m : MOVES) {
            probability += stay(movesLeft - 1, r + m[0], c + m[1]) / 8.0;
        }
        memo[movesLeft][r][c] = probability;
        return probability;
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
