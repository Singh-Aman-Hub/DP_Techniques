/**
 * 935. Knight Dialer — Tabulation (bottom-up)
 *
 * Idea: dp[len][d] = numbers of length len ending on digit d = sum of dp[len-1][p] over every p
 * that is a knight jump away from d. Start with dp[1][d] = 1.
 *
 * Time complexity:  O(n) — n lengths x 10 digits x at most 3 neighbours.
 * Space complexity: O(n) — the (n+1) x 10 table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;
    private static final int[][] NEIGHBOURS = {
        {4, 6}, {6, 8}, {7, 9}, {4, 8}, {0, 3, 9}, {}, {0, 1, 7}, {2, 6}, {1, 3}, {2, 4}
    };

    static int knightDialer(int n) {
        // dp[len][d] = count of valid numbers with len digits that end on digit d
        long[][] dp = new long[n + 1][10];
        for (int d = 0; d <= 9; d++) dp[1][d] = 1;
        for (int len = 2; len <= n; len++) {
            for (int d = 0; d <= 9; d++) {
                long ways = 0;
                for (int prev : NEIGHBOURS[d]) ways += dp[len - 1][prev];   // knight moves are symmetric
                dp[len][d] = ways % MOD;
            }
        }
        long total = 0;
        for (int d = 0; d <= 9; d++) total += dp[n][d];
        return (int) (total % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", knightDialer(1), 10);
        check("n = 2", knightDialer(2), 20);
        check("n = 3", knightDialer(3), 46);
        check("n = 3131", knightDialer(3131), 136006598);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
