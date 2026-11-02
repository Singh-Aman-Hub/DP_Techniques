/**
 * Fibonacci Number (LeetCode 509) — Memoization (top-down DP)
 *
 * Idea: the same recursion as BruteForce, but every answer F(k) is stored the
 * first time it is computed. Any later call for the same k returns the stored
 * value immediately, so each F(k) is computed only once.
 *
 * Time complexity:  O(n) — there are n+1 distinct states (0..n), each one does
 *                   O(1) work the first time and O(1) lookup afterwards.
 * Space complexity: O(n) — the memo array has n+1 cells and the recursion stack
 *                   can be n calls deep.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[k] stores F(k); -1 means "not computed yet" (real values are never negative)
    private static long[] memo;

    static long fib(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return solve(n);
    }

    private static long solve(int k) {
        if (k <= 1) return k;                 // base cases F(0)=0, F(1)=1
        if (memo[k] != -1) return memo[k];    // cache hit: reuse earlier work
        memo[k] = solve(k - 1) + solve(k - 2);
        return memo[k];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", fib(5), 5);
        check("n = 10", fib(10), 55);
        check("n = 0 (edge)", fib(0), 0);
        check("n = 1 (edge)", fib(1), 1);
        check("n = 30", fib(30), 832040);
        check("n = 50 (too slow for brute force)", fib(50), 12586269025L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
