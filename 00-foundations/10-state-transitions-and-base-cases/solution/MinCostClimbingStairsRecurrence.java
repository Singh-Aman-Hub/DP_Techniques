/**
 * Min Cost Climbing Stairs (LeetCode 746) — the recurrence written three ways
 *
 * Idea: cost[i] is the fee for stepping OFF step i (you pay it, then climb 1
 *       or 2). You may start on step 0 or step 1 for free, and the "top" is
 *       the position n just past the last step.
 *       State: minCost(i) = "the cheapest total fee to arrive at position i".
 *       Choices for the last move: from i-1 (pay cost[i-1]) or from i-2 (pay
 *       cost[i-2]). We want the cheaper one, so we combine with MIN:
 *           minCost(i) = min(minCost(i-1) + cost[i-1], minCost(i-2) + cost[i-2])
 *           minCost(0) = 0, minCost(1) = 0   (we may start on either for free)
 *       Recursion, memoization and a table all evaluate this and must agree.
 *
 * Time complexity:  recursion O(2^n); memo and table O(n) — n + 1 states, O(1) each.
 * Space complexity: recursion O(n) stack; memo O(n) cache + stack; table O(n) array.
 *
 * Run: java MinCostClimbingStairsRecurrence.java
 */
import java.util.Arrays;

public class MinCostClimbingStairsRecurrence {

    // ---------- 1. plain recursion ----------
    static long minCostRecursive(int[] cost, int i) {
        if (i == 0 || i == 1) return 0;   // free starting positions
        long fromOneBelow = minCostRecursive(cost, i - 1) + cost[i - 1];
        long fromTwoBelow = minCostRecursive(cost, i - 2) + cost[i - 2];
        return Math.min(fromOneBelow, fromTwoBelow);
    }

    // ---------- 2. memoization ----------
    // memo[i] = cheapest fee to arrive at position i; -1 means "not computed yet"
    private static long[] memo;

    static long minCostMemo(int[] cost) {
        memo = new long[cost.length + 1];
        Arrays.fill(memo, -1);
        return memoHelper(cost, cost.length);
    }

    private static long memoHelper(int[] cost, int i) {
        if (i == 0 || i == 1) return 0;
        if (memo[i] != -1) return memo[i];
        memo[i] = Math.min(memoHelper(cost, i - 1) + cost[i - 1],
                           memoHelper(cost, i - 2) + cost[i - 2]);
        return memo[i];
    }

    // ---------- 3. tabulation ----------
    static long minCostTable(int[] cost) {
        int n = cost.length;
        // dp[i] = cheapest fee to arrive at position i (position n is the top)
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = 0;
        for (int i = 2; i <= n; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
        }
        return dp[n];
    }

    // ---------- 4. a BUGGY base case ----------
    // "You start at step 0, so reaching step 1 costs cost[0]" forgets the free
    // start on step 1. For [10, 15, 20] this forces paying 10 extra somewhere.
    static long minCostBuggyBase(int[] cost) {
        int n = cost.length;
        long[] dp = new long[n + 1];
        dp[0] = 0;
        dp[1] = cost[0];   // BUG: should be 0
        for (int i = 2; i <= n; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
        }
        return dp[n];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = {
            {10, 15, 20},
            {1, 100, 1, 1, 1, 100, 1, 1, 100, 1},
            {5, 7},          // edge: smallest allowed input (two steps)
            {0, 0, 0}
        };
        long[] expected = {15, 6, 5, 0};
        for (int t = 0; t < inputs.length; t++) {
            int[] cost = inputs[t];
            String name = Arrays.toString(cost);
            check("recursion " + name, minCostRecursive(cost, cost.length), expected[t]);
            check("memo      " + name, minCostMemo(cost), expected[t]);
            check("table     " + name, minCostTable(cost), expected[t]);
        }
        check("buggy base case gives WRONG answer for [10, 15, 20]",
              minCostBuggyBase(new int[]{10, 15, 20}), 25);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
