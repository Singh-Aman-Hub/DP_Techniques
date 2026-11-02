/**
 * Matrix Chain Multiplication — Memoization (top-down)
 *
 * Idea: the partition recursion f(i, j) = min over k of f(i, k) + f(k+1, j) + dims[i-1]*dims[k]*dims[j],
 * with every (i, j) range solved once and cached.
 *
 * Time complexity:  O(n^3) — O(n^2) ranges x O(n) split points.
 * Space complexity: O(n^2) — memo table; recursion depth O(n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j]: min multiplications for A_i..A_j; -1 = not computed
    private static long[][] memo;

    static long minMultiplications(int[] dims) {
        int n = dims.length - 1;
        memo = new long[n + 1][n + 1];
        for (long[] row : memo) Arrays.fill(row, -1);
        return best(dims, 1, n);
    }

    private static long best(int[] dims, int i, int j) {
        if (i == j) return 0;
        if (memo[i][j] != -1) return memo[i][j];
        long cheapest = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {
            long cost = best(dims, i, k) + best(dims, k + 1, j) + (long) dims[i - 1] * dims[k] * dims[j];
            cheapest = Math.min(cheapest, cost);
        }
        return memo[i][j] = cheapest;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("dims=[40,20,30,10,30]", minMultiplications(new int[]{40, 20, 30, 10, 30}), 26000);
        check("dims=[10,30,5,60]", minMultiplications(new int[]{10, 30, 5, 60}), 4500);
        check("dims=[10,20,30,40,30]", minMultiplications(new int[]{10, 20, 30, 40, 30}), 30000);
        check("dims=[10,20,30]", minMultiplications(new int[]{10, 20, 30}), 6000);
        check("edge: one matrix", minMultiplications(new int[]{10, 20}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
