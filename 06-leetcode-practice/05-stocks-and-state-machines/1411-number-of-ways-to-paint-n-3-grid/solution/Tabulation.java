/**
 * 1411. Number of Ways to Paint N x 3 Grid — Tabulation (bottom-up, 12 row patterns)
 *
 * Idea: dp[r][q] = colourings of rows 0..r whose row r has pattern q. Each entry sums dp[r-1][p]
 * over every pattern p compatible with q (different colour in every column).
 *
 * Time complexity:  O(n * 144) — n rows x 12 patterns x 12 predecessors.
 * Space complexity: O(n * 12) — the table.
 *
 * Run: java Tabulation.java
 */
import java.util.ArrayList;
import java.util.List;

public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int numOfWays(int n) {
        List<int[]> patterns = new ArrayList<>();          // the 12 valid single-row colourings
        for (int a = 0; a < 3; a++)
            for (int b = 0; b < 3; b++)
                for (int c = 0; c < 3; c++)
                    if (a != b && b != c) patterns.add(new int[]{a, b, c});
        int count = patterns.size();

        // dp[r][q] = number of valid colourings of rows 0..r with row r painted as pattern q
        long[][] dp = new long[n][count];
        for (int q = 0; q < count; q++) dp[0][q] = 1;
        for (int r = 1; r < n; r++) {
            for (int q = 0; q < count; q++) {
                long total = 0;
                for (int p = 0; p < count; p++) {
                    if (compatible(patterns.get(p), patterns.get(q))) total += dp[r - 1][p];
                }
                dp[r][q] = total % MOD;
            }
        }
        long answer = 0;
        for (int q = 0; q < count; q++) answer += dp[n - 1][q];
        return (int) (answer % MOD);
    }

    private static boolean compatible(int[] upper, int[] lower) {
        return upper[0] != lower[0] && upper[1] != lower[1] && upper[2] != lower[2];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", numOfWays(1), 12);
        check("n = 2", numOfWays(2), 54);
        check("n = 3", numOfWays(3), 246);
        check("n = 5000", numOfWays(5000), 30228214);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
