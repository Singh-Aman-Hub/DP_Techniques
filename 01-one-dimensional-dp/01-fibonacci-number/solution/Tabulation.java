/**
 * Fibonacci Number (LeetCode 509) — Tabulation (bottom-up DP)
 *
 * Idea: fill a table dp[0..n] from the smallest index upward. When we reach
 * dp[i], the two cells it needs (dp[i-1] and dp[i-2]) are already filled, so
 * no recursion is needed at all.
 *
 * Time complexity:  O(n) — one loop over i = 2..n, O(1) work per cell.
 * Space complexity: O(n) — the table has n+1 cells.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long fib(int n) {
        if (n <= 1) return n;                 // the table below needs at least 2 cells
        // dp[i] = the i-th Fibonacci number F(i)
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];    // both right-hand cells are already final
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", fib(5), 5);
        check("n = 10", fib(10), 55);
        check("n = 0 (edge)", fib(0), 0);
        check("n = 1 (edge)", fib(1), 1);
        check("n = 30", fib(30), 832040);
        check("n = 50", fib(50), 12586269025L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
