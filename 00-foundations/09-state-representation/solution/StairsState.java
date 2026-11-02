/**
 * State representation — 1D state for Climbing Stairs
 *
 * Idea: The only thing that changes as we climb is which step we are on. So
 *       the state is one integer: ways(i) = "the number of distinct ways to
 *       reach step i starting from the ground (step 0), taking 1 or 2 steps at
 *       a time". One parameter is enough, so the cache is a 1D array of size
 *       n + 1. The answer to the whole problem is ways(n).
 *
 * Time complexity:  O(n) — there are n + 1 states (i = 0..n) and each one does
 *                   O(1) work (two cache lookups and an addition).
 * Space complexity: O(n) — the cache holds n + 1 numbers and the recursion
 *                   stack can be up to n calls deep.
 *
 * Run: java StairsState.java
 */
import java.util.Arrays;

public class StairsState {

    // memo[i] = number of ways to reach step i from the ground; -1 means "not computed yet"
    private static long[] memo;

    static long countWays(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return ways(n);
    }

    // The state is a single integer: the step we want to reach.
    private static long ways(int i) {
        if (i == 0 || i == 1) return 1;   // ground: the empty plan; step 1: a single 1-step
        if (memo[i] != -1) return memo[i];
        memo[i] = ways(i - 1) + ways(i - 2);  // last move was a 1-step or a 2-step
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("n = 5", countWays(5), 8);
        check("n = 10", countWays(10), 89);
        check("n = 1 (edge: one step)", countWays(1), 1);
        check("n = 2", countWays(2), 2);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
