/**
 * Climbing Stairs (LeetCode 70) — Memoization (top-down DP)
 *
 * Idea: same recursion as BruteForce, ways(i) = ways(i-1) + ways(i-2), but
 * each ways(i) is stored after its first computation and reused afterwards.
 *
 * Time complexity:  O(n) — n+1 states, O(1) work each.
 * Space complexity: O(n) — memo array plus a recursion stack up to n deep.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = number of distinct ways to reach step i; -1 means "not computed yet"
    private static long[] memo;

    static long climbStairs(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return ways(n);
    }

    private static long ways(int i) {
        if (i <= 1) return 1;                 // ground (0) and first step (1): one way each
        if (memo[i] != -1) return memo[i];
        memo[i] = ways(i - 1) + ways(i - 2);  // last move was a 1-step or a 2-step
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", climbStairs(5), 8);
        check("n = 2", climbStairs(2), 2);
        check("n = 3", climbStairs(3), 3);
        check("n = 1 (edge)", climbStairs(1), 1);
        check("n = 45 (LeetCode max)", climbStairs(45), 1836311903L);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
