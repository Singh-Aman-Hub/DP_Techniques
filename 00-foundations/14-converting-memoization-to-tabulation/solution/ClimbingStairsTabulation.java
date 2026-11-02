/**
 * Climbing Stairs — Tabulation, produced from the memoized version by a recipe
 *
 * Idea: translate the memoized function line by line.
 *   1. The cache becomes the table, same shape: long[n + 1].
 *   2. Base cases become table initialisation: ways[0] = ways[1] = 1.
 *   3. Recursive calls ways(i-1), ways(i-2) become table reads ways[i-1], ways[i-2].
 *   4. Loop direction from the dependencies: i reads smaller indices -> i goes up.
 *   5. Return the cell matching the top-level call: ways[n].
 * The memoized version is kept in this file so the two can be checked against each other.
 *
 * Time complexity:  O(n) — the loop fills n - 1 cells with O(1) work each.
 * Space complexity: O(n) — the table; no recursion stack at all.
 *
 * Run: java ClimbingStairsTabulation.java
 */
import java.util.Arrays;

public class ClimbingStairsTabulation {

    // ---------- tabulation (the "after") ----------

    static long climbStairs(int n) {
        // ways[i] = number of distinct ways to reach step i using 1- and 2-steps
        long[] ways = new long[Math.max(n + 1, 2)]; // at least 2 cells so ways[1] exists when n = 0
        ways[0] = 1;                                 // base case: the empty plan
        ways[1] = 1;                                 // base case: a single 1-step
        for (int i = 2; i <= n; i++) {               // i reads i-1 and i-2, so go upwards
            ways[i] = ways[i - 1] + ways[i - 2];     // was: ways(i - 1) + ways(i - 2)
        }
        return ways[n];                              // top-level call was ways(n)
    }

    // ---------- memoization (the "before"), for cross-checking ----------

    // memo[i] = number of ways to reach step i; -1 = not computed yet
    private static long[] memo;

    static long climbStairsMemo(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return waysMemo(n);
    }

    private static long waysMemo(int i) {
        if (i <= 1) return 1;
        if (memo[i] != -1) return memo[i];
        memo[i] = waysMemo(i - 1) + waysMemo(i - 2);
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", climbStairs(5), 8);
        check("n = 10", climbStairs(10), 89);
        check("n = 45", climbStairs(45), 1_836_311_903L);
        check("n = 1 (edge)", climbStairs(1), 1);
        check("n = 0 (edge: table still needs ways[1])", climbStairs(0), 1);

        for (int n = 0; n <= 60; n++) {
            check("tabulation == memoization for n = " + n, climbStairs(n), climbStairsMemo(n));
        }
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
