/**
 * 96. Unique Binary Search Trees — Optimized (Catalan closed form)
 *
 * Idea: the table from Tabulation.java is the Catalan sequence. Consecutive Catalan
 * numbers satisfy C(k+1) = C(k) * 2(2k+1) / (k+2), so we can walk from C(0) = 1 to
 * C(n) with one multiply and one exact division per step, keeping only one number.
 *
 * Time complexity:  O(n) — one constant-time update per step.
 * Space complexity: O(1) — a single running value.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long numTrees(int n) {
        long catalan = 1;                         // C(0) = 1
        for (int k = 0; k < n; k++) {
            // multiply first, then divide: the product is always divisible by (k + 2)
            catalan = catalan * 2 * (2L * k + 1) / (k + 2);
        }
        return catalan;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 3", numTrees(3), 5);
        check("n = 1", numTrees(1), 1);
        check("n = 0 (edge: empty tree)", numTrees(0), 1);
        check("n = 4", numTrees(4), 14);
        check("n = 19 (largest allowed, needs long)", numTrees(19), 1767263190L);
        // cross-check against the O(n^2) table for every n up to 19
        for (int n = 0; n <= 19; n++) check("agrees with table, n = " + n, numTrees(n), table(n));
    }

    private static long table(int n) {
        long[] ways = new long[n + 1];
        ways[0] = 1;
        for (int k = 1; k <= n; k++)
            for (int root = 1; root <= k; root++) ways[k] += ways[root - 1] * ways[k - root];
        return ways[n];
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
