/**
 * 879. Profitable Schemes — Tabulation (bottom-up)
 *
 * Idea: dp[i][m][p] = subsets of the first i crimes using EXACTLY m members with profit AT LEAST p
 * (p clamped to 0..minProfit). Skip or commit each crime; the answer sums dp[L][m][minProfit]
 * over every m <= n.
 *
 * Time complexity:  O(L * n * P) — every cell of the 3D table in O(1).
 * Space complexity: O(L * n * P) — the 3D table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int profitableSchemes(int n, int minProfit, int[] group, int[] profit) {
        int crimes = group.length;
        long[][][] dp = new long[crimes + 1][n + 1][minProfit + 1];
        dp[0][0][0] = 1;                                    // the empty scheme
        for (int i = 1; i <= crimes; i++) {
            int g = group[i - 1], pr = profit[i - 1];
            for (int m = 0; m <= n; m++) {
                for (int p = 0; p <= minProfit; p++) {
                    long ways = dp[i - 1][m][p];            // skip
                    if (m >= g) ways += dp[i - 1][m - g][Math.max(0, p - pr)];   // commit
                    dp[i][m][p] = ways % MOD;
                }
            }
        }
        long answer = 0;
        for (int m = 0; m <= n; m++) answer += dp[crimes][m][minProfit];
        return (int) (answer % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", profitableSchemes(5, 3, new int[]{2, 2}, new int[]{2, 3}), 2);
        check("example 2", profitableSchemes(10, 5, new int[]{2, 3, 5}, new int[]{6, 7, 8}), 7);
        check("only empty scheme (edge)", profitableSchemes(1, 0, new int[]{2}, new int[]{1}), 1);
        check("three single crimes", profitableSchemes(1, 1, new int[]{1, 1, 1}, new int[]{1, 1, 1}), 3);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
