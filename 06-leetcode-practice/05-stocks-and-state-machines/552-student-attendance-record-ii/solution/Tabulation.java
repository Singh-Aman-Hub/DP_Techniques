/**
 * 552. Student Attendance Record II — Tabulation (bottom-up)
 *
 * Idea: dp[i][a][l] = valid records of length i with a absences that end in l consecutive L's.
 * From every state, push its count to the states reached by appending P, A (if a == 0) or
 * L (if l < 2).
 *
 * Time complexity:  O(n) — n lengths x 6 states x 3 moves.
 * Space complexity: O(n) — the (n+1) x 2 x 3 table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    private static final int MOD = 1_000_000_007;

    static int checkRecord(int n) {
        // dp[i][a][l] = records of length i with a absences and l trailing lates
        long[][][] dp = new long[n + 1][2][3];
        dp[0][0][0] = 1;                                   // the empty record
        for (int i = 0; i < n; i++) {
            for (int a = 0; a <= 1; a++) {
                for (int l = 0; l <= 2; l++) {
                    long count = dp[i][a][l];
                    if (count == 0) continue;
                    dp[i + 1][a][0] = (dp[i + 1][a][0] + count) % MOD;                   // P
                    if (a == 0) dp[i + 1][1][0] = (dp[i + 1][1][0] + count) % MOD;       // A
                    if (l < 2) dp[i + 1][a][l + 1] = (dp[i + 1][a][l + 1] + count) % MOD; // L
                }
            }
        }
        long total = 0;
        for (int a = 0; a <= 1; a++) for (int l = 0; l <= 2; l++) total += dp[n][a][l];
        return (int) (total % MOD);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 1 (edge)", checkRecord(1), 3);
        check("n = 2", checkRecord(2), 8);
        check("n = 3", checkRecord(3), 19);
        check("n = 10101", checkRecord(10101), 183236316);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
