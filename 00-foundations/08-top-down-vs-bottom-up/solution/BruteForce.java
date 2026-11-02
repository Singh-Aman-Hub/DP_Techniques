/**
 * Fibonacci — Brute force (plain recursion)
 *
 * Idea: Translate the definition directly: fib(n) = fib(n-1) + fib(n-2) with
 *       fib(0) = 0 and fib(1) = 1. Nothing is remembered, so the same
 *       subproblems are solved over and over.
 *
 * Time complexity:  O(2^n) — the recursion tree has 2*fib(n+1) - 1 nodes, which
 *                   grows by a factor of about 1.618 per +1 in n (bounded by 2^n).
 * Space complexity: O(n) — the deepest chain of waiting calls is n frames.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long fib(int n) {
        if (n <= 1) return n;                 // base cases: fib(0) = 0, fib(1) = 1
        return fib(n - 1) + fib(n - 2);       // the recurrence, recomputing freely
    }

    // ---------- self-check ----------
    // Same test set as the other three files, minus the large n (too slow here).
    public static void main(String[] args) {
        check("fib(0)  [edge]", fib(0), 0);
        check("fib(1)  [edge]", fib(1), 1);
        check("fib(2)", fib(2), 1);
        check("fib(6)", fib(6), 8);
        check("fib(10)", fib(10), 55);
        check("fib(20)", fib(20), 6765);
        check("fib(30)", fib(30), 832040);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
