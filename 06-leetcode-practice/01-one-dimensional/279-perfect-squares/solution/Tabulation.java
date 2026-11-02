/**
 * 279. Perfect Squares — Tabulation (bottom-up)
 *
 * Idea: dp[i] = 1 + min(dp[i - k*k]) over all squares k*k <= i, filled for i = 1..n. This is
 * climbing stairs (or coin change) where the allowed step sizes are the perfect squares.
 *
 * Time complexity:  O(n * sqrt(n)) — n cells, each tries at most sqrt(n) squares.
 * Space complexity: O(n) — the dp array.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int numSquares(int n) {
        // dp[i] = fewest perfect squares that sum to exactly i
        int[] dp = new int[n + 1];
        dp[0] = 0;
        for (int i = 1; i <= n; i++) {
            dp[i] = Integer.MAX_VALUE;
            for (int k = 1; k * k <= i; k++) {
                dp[i] = Math.min(dp[i], dp[i - k * k] + 1);
            }
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 12", numSquares(12), 3);
        check("n = 13", numSquares(13), 2);
        check("n = 7", numSquares(7), 4);
        check("n = 1 (edge)", numSquares(1), 1);
        check("n = 43", numSquares(43), 3);
        check("n = 100", numSquares(100), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
