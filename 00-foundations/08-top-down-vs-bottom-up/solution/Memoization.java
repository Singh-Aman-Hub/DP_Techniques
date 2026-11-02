/**
 * Fibonacci — Memoization (top-down DP)
 *
 * Idea: Keep the recursive structure of the brute force, but store every answer
 *       in memo[] the first time it is computed. Control flow is still
 *       "start from the big question, ask for smaller ones", which is why it is
 *       called top-down. Only the states the recursion actually reaches are filled.
 *
 * Time complexity:  O(n) — states 2..n are each computed once with O(1) work;
 *                   all other calls are O(1) base cases or cache hits.
 * Space complexity: O(n) — memo has n + 1 cells, and the call stack is n deep.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] stores fib(i); -1 means "not computed yet" (Fibonacci values are never negative)
    private static long[] memo;

    static long fib(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return solve(n);
    }

    private static long solve(int n) {
        if (n <= 1) return n;                   // base cases
        if (memo[n] != -1) return memo[n];      // check before computing
        memo[n] = solve(n - 1) + solve(n - 2);  // compute once, store
        return memo[n];
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
