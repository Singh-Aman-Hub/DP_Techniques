/**
 * Climbing Stairs (LeetCode 70) — Tabulation (bottom-up DP)
 *
 * Idea: fill ways[0..n] from the ground upward. ways[i] only needs the two
 * cells below it, which are already final when the loop reaches i.
 *
 * Time complexity:  O(n) — one pass over i = 2..n.
 * Space complexity: O(n) — the table has n+1 cells.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static long climbStairs(int n) {
        if (n <= 1) return 1;
        // ways[i] = number of distinct ways to stand on step i, starting from the ground
        long[] ways = new long[n + 1];
        ways[0] = 1;   // the empty climb
        ways[1] = 1;   // a single 1-step
        for (int i = 2; i <= n; i++) {
            ways[i] = ways[i - 1] + ways[i - 2];
        }
        return ways[n];
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
