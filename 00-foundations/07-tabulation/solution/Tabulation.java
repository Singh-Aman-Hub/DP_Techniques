/**
 * Tabulation — filling a table bottom-up (Fibonacci and Climbing Stairs)
 *
 * Idea: Instead of starting at the big question and recursing down, start at
 *       the smallest subproblems (the base cases), write them into a table,
 *       and loop upward so that every cell's ingredients are already filled
 *       when we reach it. No recursion, no call stack, no "is it cached?" check.
 *         dp[i] = fib(i):        dp[0] = 0, dp[1] = 1, dp[i] = dp[i-1] + dp[i-2]
 *         ways[i] = ways to climb i stairs with 1- or 2-steps:
 *                                ways[0] = 1, ways[1] = 1, ways[i] = ways[i-1] + ways[i-2]
 *       The loop direction matters: i must go UP, because dp[i] reads smaller indices.
 *
 * Time complexity:  O(n) — the loop runs n-1 times and each step does one addition.
 * Space complexity: O(n) — the table has n + 1 cells (no recursion stack at all).
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static boolean showSteps = false;

    static long fib(int n) {
        if (n <= 1) return n;                 // the table needs at least 2 cells; answer directly
        // dp[i] = the i-th Fibonacci number
        long[] dp = new long[n + 1];
        dp[0] = 0;                            // base cases first
        dp[1] = 1;
        if (showSteps) System.out.println("start      " + Arrays.toString(dp));
        for (int i = 2; i <= n; i++) {        // small to large: dp[i-1] and dp[i-2] are ready
            dp[i] = dp[i - 1] + dp[i - 2];
            if (showSteps) System.out.println("fill i = " + i + "   " + Arrays.toString(dp)
                    + "   dp[" + i + "] = " + dp[i - 1] + " + " + dp[i - 2]);
        }
        return dp[n];
    }

    /** Same recurrence, but the loop runs in the WRONG direction (a classic mistake). */
    static long fibWrongOrder(int n) {
        if (n <= 1) return n;
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        for (int i = n; i >= 2; i--) {        // BUG: dp[i-1] has not been filled yet (still 0)
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n];
    }

    static long waysToClimb(int n) {
        // ways[i] = number of ways to reach stair i using 1- or 2-steps
        long[] ways = new long[Math.max(n + 1, 2)];
        ways[0] = 1;                          // stand still at the bottom
        ways[1] = 1;                          // one single step
        for (int i = 2; i <= n; i++) {
            ways[i] = ways[i - 1] + ways[i - 2];   // last move was a 1-step or a 2-step
        }
        return ways[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        System.out.println("Filling the table for fib(6):");
        showSteps = true;
        long six = fib(6);
        showSteps = false;
        System.out.println();

        check("fib(6)", six, 8);
        check("fib(0)  [edge]", fib(0), 0);
        check("fib(1)  [edge]", fib(1), 1);
        check("fib(10)", fib(10), 55);
        check("fib(50)", fib(50), 12586269025L);
        check("fib(90)", fib(90), 2880067194370816120L);
        check("wrong loop order gives a wrong fib(6)", fibWrongOrder(6) == 8 ? 1 : 0, 0);

        check("waysToClimb(1)  [edge]", waysToClimb(1), 1);
        check("waysToClimb(2)", waysToClimb(2), 2);
        check("waysToClimb(5)", waysToClimb(5), 8);
        check("waysToClimb(10)", waysToClimb(10), 89);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
