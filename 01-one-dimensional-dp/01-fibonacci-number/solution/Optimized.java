/**
 * Fibonacci Number (LeetCode 509) — Space-optimized DP
 *
 * Idea: dp[i] only reads dp[i-1] and dp[i-2]. Everything older is never read
 * again, so two variables that "slide" forward replace the whole table.
 *
 * Time complexity:  O(n) — one loop, O(1) work per step.
 * Space complexity: O(1) — exactly two variables, whatever n is.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long fib(int n) {
        if (n <= 1) return n;
        long prev2 = 0;   // F(i-2), starts as F(0)
        long prev1 = 1;   // F(i-1), starts as F(1)
        for (int i = 2; i <= n; i++) {
            long current = prev1 + prev2;     // F(i)
            prev2 = prev1;                    // slide the window one step right;
            prev1 = current;                  // order matters: shift prev2 first
        }
        return prev1;                         // after the loop prev1 holds F(n)
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
