/**
 * 1137. N-th Tribonacci Number — Tabulation (bottom-up)
 *
 * Idea: Start from the three known base values and fill dp[3], dp[4], ..., dp[n] left to
 * right; every cell only needs the three cells just before it, which are already filled.
 *
 * Time complexity:  O(n) — one pass over indices 3..n with O(1) work each.
 * Space complexity: O(n) — the dp array of size n+1.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long tribonacci(int n) {
        if (n == 0) return 0;
        if (n <= 2) return 1;                 // avoid writing dp[2] into a too-small array

        // dp[i] = the i-th Tribonacci number
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 1;
        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 0 (edge)", tribonacci(0), 0);
        check("n = 2 (edge)", tribonacci(2), 1);
        check("n = 4", tribonacci(4), 4);
        check("n = 6", tribonacci(6), 13);
        check("n = 25", tribonacci(25), 1389537);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
