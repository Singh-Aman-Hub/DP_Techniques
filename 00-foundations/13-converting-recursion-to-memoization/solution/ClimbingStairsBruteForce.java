/**
 * Climbing Stairs — Brute force (the "before" picture for the memoization recipe)
 *
 * Idea: to reach step n, the last move was either a 1-step (from n-1) or a
 * 2-step (from n-2). So ways(n) = ways(n-1) + ways(n-2). This plain recursion is
 * correct, but it recomputes the same smaller steps again and again.
 *
 * Time complexity:  O(2^n) — every call makes up to two more calls; the tree has
 *                   2 * ways(n) - 1 nodes, which grows like 1.618^n.
 * Space complexity: O(n) — no table, but the call stack is up to n frames deep.
 *
 * Run: java ClimbingStairsBruteForce.java
 */
public class ClimbingStairsBruteForce {

    /** Number of distinct ways to reach step n taking 1 or 2 steps at a time. */
    static long ways(int n) {
        if (n <= 1) return 1;               // step 0: the empty plan; step 1: a single 1-step
        return ways(n - 1) + ways(n - 2);   // last move was a 1-step or a 2-step
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", ways(5), 8);
        check("n = 10", ways(10), 89);
        check("n = 1 (edge)", ways(1), 1);
        check("n = 0 (edge: already at the top)", ways(0), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
