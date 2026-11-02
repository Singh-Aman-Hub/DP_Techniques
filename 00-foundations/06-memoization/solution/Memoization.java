/**
 * Memoization — Fibonacci with a cache, showing every cache hit
 *
 * Idea: Keep the plain recursion, add a cache (memo[k] = fib(k), or -1 for
 *       "not computed yet"), and follow the check-before-compute pattern:
 *         1. base case?            -> return it
 *         2. already in the cache? -> return the cached value (a "cache hit")
 *         3. otherwise compute it with the recurrence, STORE it, then return it.
 *       Each subproblem is now solved once; every later request is a lookup,
 *       which prunes the exponential recursion tree down to a thin chain.
 *
 * Time complexity:  O(n) — n-1 subproblems (2..n) are computed once each with
 *                   O(1) work; every other call is a base case or an O(1) cache hit
 *                   (2n - 1 calls in total).
 * Space complexity: O(n) — the memo array has n + 1 cells and the recursion
 *                   goes n frames deep before it starts returning.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[k] stores fib(k) once computed; -1 means "not computed yet".
    // -1 is safe because every Fibonacci number is >= 0.
    private static long[] memo;

    private static int computed = 0, cacheHits = 0, baseCases = 0;
    private static boolean trace = false;

    static long fib(int n) {
        memo = new long[n + 1];           // a fresh cache for each new top-level question
        Arrays.fill(memo, -1);
        computed = cacheHits = baseCases = 0;
        return solve(n, 0);
    }

    private static long solve(int n, int depth) {
        if (n <= 1) {                                        // 1. base case
            baseCases++;
            log(depth, "fib(" + n + ") base case = " + n);
            return n;
        }
        if (memo[n] != -1) {                                 // 2. check the cache first
            cacheHits++;
            log(depth, "fib(" + n + ") CACHE HIT = " + memo[n]);
            return memo[n];
        }
        log(depth, "fib(" + n + ") not cached, computing...");
        long answer = solve(n - 1, depth + 1) + solve(n - 2, depth + 1);
        memo[n] = answer;                                    // 3. store before returning
        computed++;
        log(depth, "fib(" + n + ") stored memo[" + n + "] = " + answer);
        return answer;
    }

    private static void log(int depth, String message) {
        if (trace) System.out.println("  ".repeat(depth) + message);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        System.out.println("Dry run of fib(6):");
        trace = true;
        long six = fib(6);
        trace = false;
        System.out.println("memo after fib(6): " + Arrays.toString(memo));
        System.out.println();

        check("fib(6)", six, 8);
        check("fib(6): subproblems computed (2..6)", computed, 5);
        check("fib(6): cache hits", cacheHits, 3);
        check("fib(6): base-case calls", baseCases, 3);

        check("fib(0)  [edge]", fib(0), 0);
        check("fib(1)  [edge]", fib(1), 1);
        check("fib(10)", fib(10), 55);
        check("fib(50)", fib(50), 12586269025L);
        check("fib(90)  [largest test; still fits in long]", fib(90), 2880067194370816120L);
        check("fib(90): total calls = 2n - 1", computed + cacheHits + baseCases, 179);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
