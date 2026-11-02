/**
 * Climbing Stairs (LeetCode 70) — Brute force recursion
 *
 * Idea: the last move onto step n was either a 1-step (from n-1) or a 2-step
 * (from n-2). Every way to reach n is a way to reach n-1 or n-2, extended by
 * one move, so ways(n) = ways(n-1) + ways(n-2). Plain recursion recomputes
 * the same smaller staircases many times.
 *
 * Time complexity:  O(2^n) — two recursive calls per level, like Fibonacci.
 * Space complexity: O(n) — recursion depth is at most n.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static long climbStairs(int n) {
        // ways(0) = 1: standing on the ground, "do nothing" is exactly one way.
        // ways(1) = 1: the only way is a single 1-step.
        if (n <= 1) return 1;
        return climbStairs(n - 1) + climbStairs(n - 2);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", climbStairs(5), 8);
        check("n = 2", climbStairs(2), 2);
        check("n = 3", climbStairs(3), 3);
        check("n = 1 (edge)", climbStairs(1), 1);
        check("n = 10", climbStairs(10), 89);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
