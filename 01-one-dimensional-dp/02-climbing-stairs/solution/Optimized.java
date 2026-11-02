/**
 * Climbing Stairs (LeetCode 70) — Space-optimized DP
 *
 * Idea: ways[i] reads only ways[i-1] and ways[i-2], so two rolling variables
 * replace the table. This is exactly the Fibonacci loop, shifted by one:
 * ways(n) = F(n+1).
 *
 * Time complexity:  O(n) — one loop.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static long climbStairs(int n) {
        long twoBelow = 1;   // ways(i-2), starts as ways(0)
        long oneBelow = 1;   // ways(i-1), starts as ways(1)
        for (int i = 2; i <= n; i++) {
            long current = oneBelow + twoBelow;
            twoBelow = oneBelow;
            oneBelow = current;
        }
        return oneBelow;     // for n = 0 or 1 the loop never runs and this is 1
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", climbStairs(5), 8);
        check("n = 2", climbStairs(2), 2);
        check("n = 3", climbStairs(3), 3);
        check("n = 1 (edge)", climbStairs(1), 1);
        check("n = 45", climbStairs(45), 1836311903L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
