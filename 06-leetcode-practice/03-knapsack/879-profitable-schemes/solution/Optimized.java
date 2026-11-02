/**
 * 879. Profitable Schemes — Space-optimized (one 2D layer)
 *
 * Idea: Drop the crime index; keep dp[m][p] (exactly m members, profit at least p). Iterate m
 * downward so the commit term dp[m-g][...] still holds the value from before this crime,
 * which means each crime is used at most once.
 *
 * Time complexity:  O(L * n * P) — same cells as tabulation.
 * Space complexity: O(n * P) — one (n+1) x (P+1) table.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int MOD = 1_000_000_007;

    static int profitableSchemes(int n, int minProfit, int[] group, int[] profit) {
        // dp[m][p] = schemes so far using exactly m members with profit at least p
        long[][] dp = new long[n + 1][minProfit + 1];
        dp[0][0] = 1;
        for (int c = 0; c < group.length; c++) {
            int g = group[c], pr = profit[c];
            for (int m = n; m >= g; m--) {                 // downward: 0/1 knapsack rule
                for (int p = 0; p <= minProfit; p++) {
                    dp[m][p] = (dp[m][p] + dp[m - g][Math.max(0, p - pr)]) % MOD;
                }
            }
        }
        long answer = 0;
        for (int m = 0; m <= n; m++) answer += dp[m][minProfit];
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
