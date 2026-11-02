/**
 * Fibonacci — Tabulation (bottom-up DP)
 *
 * Idea: Fill dp[0..n] from the base cases upward with a plain loop. Every cell
 *       is computed after the two cells it depends on, so no recursion and no
 *       "is it cached?" check are needed. Control flow is "smallest first",
 *       which is why it is called bottom-up. Every state 0..n is computed.
 *
 * Time complexity:  O(n) — one loop iteration per cell 2..n, O(1) work each.
 * Space complexity: O(n) — the table has n + 1 cells; no call stack.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long fib(int n) {
        if (n <= 1) return n;                  // avoids writing dp[1] into a 1-cell table
        // dp[i] = the i-th Fibonacci number
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {         // increasing i: dp[i-1], dp[i-2] already filled
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("fib(0)  [edge]", fib(0), 0);
        check("fib(1)  [edge]", fib(1), 1);
        check("fib(2)", fib(2), 1);
        check("fib(6)", fib(6), 8);
        check("fib(10)", fib(10), 55);
        check("fib(20)", fib(20), 6765);
        check("fib(30)", fib(30), 832040);
        check("fib(50)", fib(50), 12586269025L);
        check("fib(90)", fib(90), 2880067194370816120L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
