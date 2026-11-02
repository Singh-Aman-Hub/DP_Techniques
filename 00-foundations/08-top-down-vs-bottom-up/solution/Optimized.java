/**
 * Fibonacci — Space-optimized bottom-up DP
 *
 * Idea: In the tabulation loop, dp[i] reads only dp[i-1] and dp[i-2]. Older
 *       cells are never read again, so two variables are enough: `previous`
 *       (fib(i-2)) and `current` (fib(i-1)). Each step slides the window by one.
 *
 * Time complexity:  O(n) — n-1 loop iterations with O(1) work each.
 * Space complexity: O(1) — two long variables, regardless of n.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long fib(int n) {
        if (n <= 1) return n;
        long previous = 0;   // fib(i-2), starts as fib(0)
        long current = 1;    // fib(i-1), starts as fib(1)
        for (int i = 2; i <= n; i++) {
            long next = previous + current;   // fib(i)
            previous = current;               // slide the window forward by one
            current = next;
        }
        return current;                       // after the loop, current = fib(n)
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
