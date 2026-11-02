/**
 * Fibonacci Number (LeetCode 509) — Brute force recursion
 *
 * Idea: translate the definition F(n) = F(n-1) + F(n-2) directly into a
 * recursive function. It is correct but recomputes the same F(k) many times.
 *
 * Time complexity:  O(2^n) — every call (except base cases) makes two more
 *                   calls, so the recursion tree roughly doubles per level.
 * Space complexity: O(n) — the deepest chain of open calls is fib(n), fib(n-1), ..., fib(0).
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int fib(int n) {
        // Base cases: the two values the definition gives us directly.
        if (n <= 1) return n;
        // Recurrence: the answer is built from the two smaller answers.
        return fib(n - 1) + fib(n - 2);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", fib(5), 5);
        check("n = 10", fib(10), 55);
        check("n = 0 (edge)", fib(0), 0);
        check("n = 1 (edge)", fib(1), 1);
        check("n = 2", fib(2), 1);
        check("n = 30 (LeetCode max)", fib(30), 832040);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
