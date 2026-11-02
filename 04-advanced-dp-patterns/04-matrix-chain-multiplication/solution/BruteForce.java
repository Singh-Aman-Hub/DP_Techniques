/**
 * Matrix Chain Multiplication — Brute force recursion
 *
 * Idea: matrix number m has shape dims[m-1] x dims[m] (matrices are numbered 1..n).
 * f(i, j) = fewest scalar multiplications to compute the product A_i ... A_j.
 * The LAST multiplication splits the chain into (A_i..A_k)(A_{k+1}..A_j) for some k;
 * that final step costs dims[i-1] * dims[k] * dims[j]. Try every k.
 *
 * Time complexity:  exponential — the number of parenthesizations is a Catalan number,
 *                   and overlapping ranges are recomputed.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long minMultiplications(int[] dims) {
        int n = dims.length - 1; // number of matrices
        return best(dims, 1, n);
    }

    private static long best(int[] dims, int i, int j) {
        if (i == j) return 0; // a single matrix needs no multiplication
        long cheapest = Long.MAX_VALUE;
        for (int k = i; k < j; k++) {
            long cost = best(dims, i, k) + best(dims, k + 1, j) + (long) dims[i - 1] * dims[k] * dims[j];
            cheapest = Math.min(cheapest, cost);
        }
        return cheapest;
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
