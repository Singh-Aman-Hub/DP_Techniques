/**
 * Matrix Chain Multiplication — Tabulation (fill by chain length)
 *
 * Idea: dp[i][j] needs dp[i][k] and dp[k+1][j], both shorter chains. Fill chains of
 * length 2, then 3, ..., then n. Length-1 chains (the diagonal) cost 0.
 *
 * Time complexity:  O(n^3) — ~n^2/2 cells x up to n-1 split points.
 * Space complexity: O(n^2) — the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long minMultiplications(int[] dims) {
        int n = dims.length - 1;
        // dp[i][j]: min scalar multiplications to compute A_i x ... x A_j (1-indexed matrices)
        long[][] dp = new long[n + 1][n + 1];
        for (int length = 2; length <= n; length++) {
            for (int i = 1; i + length - 1 <= n; i++) {
                int j = i + length - 1;
                dp[i][j] = Long.MAX_VALUE;
                for (int k = i; k < j; k++) {
                    long cost = dp[i][k] + dp[k + 1][j] + (long) dims[i - 1] * dims[k] * dims[j];
                    dp[i][j] = Math.min(dp[i][j], cost);
                }
            }
        }
        return dp[1][n];
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
