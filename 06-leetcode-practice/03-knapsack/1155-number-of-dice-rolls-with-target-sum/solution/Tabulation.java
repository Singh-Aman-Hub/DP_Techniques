/**
 * 1155. Number of Dice Rolls With Target Sum — Tabulation (bottom-up)
 *
 * Idea: dp[i][t] = ways for i dice to sum to t = sum of dp[i-1][t-f] for faces f = 1..k.
 * Start from dp[0][0] = 1 and fill one die at a time.
 *
 * Time complexity:  O(n * target * k) — every cell sums up to k cells of the previous row.
 * Space complexity: O(n * target) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int numRollsToTarget(int n, int k, int target) {
        // dp[i][t] = number of ways i dice can show faces summing to exactly t
        long[][] dp = new long[n + 1][target + 1];
        dp[0][0] = 1;                                       // zero dice, sum zero: one (empty) way
        for (int i = 1; i <= n; i++) {
            for (int t = 1; t <= target; t++) {
                long total = 0;
                for (int face = 1; face <= k && face <= t; face++) {
                    total += dp[i - 1][t - face];
                }
                dp[i][t] = total % MOD;
            }
        }
        return (int) dp[n][target];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n=1, k=6, target=3", numRollsToTarget(1, 6, 3), 1);
        check("n=2, k=6, target=7", numRollsToTarget(2, 6, 7), 6);
        check("n=1, k=2, target=3 (edge: impossible)", numRollsToTarget(1, 2, 3), 0);
        check("n=30, k=30, target=500", numRollsToTarget(30, 30, 500), 222616187);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
