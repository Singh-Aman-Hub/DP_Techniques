/**
 * Climbing Stairs (LeetCode 70) — the recurrence written three ways
 *
 * Idea: State: ways(i) = "the number of distinct ways to stand on step i,
 *       starting from the ground (step 0)". The last move onto step i was
 *       either a 1-step (from i-1) or a 2-step (from i-2). Those two choices
 *       never overlap and cover every possibility, so we ADD them:
 *           ways(i) = ways(i-1) + ways(i-2),   ways(0) = 1,   ways(1) = 1.
 *       The same recurrence is evaluated by plain recursion, memoization and a
 *       table, and all three must agree. A deliberately buggy base case is
 *       included to show an off-by-one trap.
 *
 * Time complexity:  recursion O(2^n) — every call branches in two;
 *                   memo and table O(n) — n + 1 states, O(1) work each.
 * Space complexity: recursion and memo O(n) stack (+ O(n) cache for memo);
 *                   table O(n) for the array.
 *
 * Run: java ClimbingStairsRecurrence.java
 */
import java.util.Arrays;

public class ClimbingStairsRecurrence {

    // ---------- 1. plain recursion: the recurrence, word for word ----------
    static long waysRecursive(int i) {
        if (i == 0) return 1;   // standing on the ground: one way (the empty plan)
        if (i == 1) return 1;   // only one way: a single 1-step
        return waysRecursive(i - 1) + waysRecursive(i - 2);
    }

    // ---------- 2. memoization ----------
    // memo[i] = number of ways to reach step i; -1 means "not computed yet"
    private static long[] memo;

    static long waysMemo(int n) {
        memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return waysMemoHelper(n);
    }

    private static long waysMemoHelper(int i) {
        if (i == 0 || i == 1) return 1;
        if (memo[i] != -1) return memo[i];
        memo[i] = waysMemoHelper(i - 1) + waysMemoHelper(i - 2);
        return memo[i];
    }

    // ---------- 3. tabulation ----------
    static long waysTable(int n) {
        // dp[i] = number of ways to reach step i; at least 2 cells so dp[1] exists even when n = 0
        long[] dp = new long[Math.max(n + 1, 2)];
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) dp[i] = dp[i - 1] + dp[i - 2];
        return dp[n];
    }

    // ---------- 4. a BUGGY base case (off-by-one trap) ----------
    // Writing ways(0) = 0 ("zero steps, zero ways") feels natural but is wrong:
    // ways(2) = ways(1) + ways(0) = 1 + 0 = 1, yet {1+1, 2} are two ways.
    static long waysBuggyBase(int i) {
        if (i == 0) return 0;   // BUG: should be 1
        if (i == 1) return 1;
        return waysBuggyBase(i - 1) + waysBuggyBase(i - 2);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[] inputs = {5, 10, 1, 2};
        long[] expected = {8, 89, 1, 2};
        for (int t = 0; t < inputs.length; t++) {
            int n = inputs[t];
            check("recursion n = " + n, waysRecursive(n), expected[t]);
            check("memo      n = " + n, waysMemo(n), expected[t]);
            check("table     n = " + n, waysTable(n), expected[t]);
        }
        // The buggy base case shifts the whole sequence by one: 0,1,1,2,3,5 -> ways(5) = 5, not 8.
        check("buggy base case gives WRONG ways(5)", waysBuggyBase(5), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
