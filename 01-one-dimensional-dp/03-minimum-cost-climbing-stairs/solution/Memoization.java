/**
 * Min Cost Climbing Stairs (LeetCode 746) — Memoization (top-down DP)
 *
 * Idea: same recursion as BruteForce,
 *   reach(i) = min(reach(i-1) + cost[i-1], reach(i-2) + cost[i-2]),
 * with reach(0) = reach(1) = 0, but each reach(i) is cached.
 *
 * Time complexity:  O(n) — n+1 states, each does one min of two sums.
 * Space complexity: O(n) — memo array plus recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = cheapest total cost to stand on step i (i = n is the top); -1 = unknown
    private static int[] memo;

    static int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        memo = new int[n + 1];
        Arrays.fill(memo, -1);                // costs are >= 0, so -1 is never a real answer
        return reach(cost, n);
    }

    private static int reach(int[] cost, int i) {
        if (i <= 1) return 0;
        if (memo[i] != -1) return memo[i];
        int fromOneBelow = reach(cost, i - 1) + cost[i - 1];
        int fromTwoBelow = reach(cost, i - 2) + cost[i - 2];
        memo[i] = Math.min(fromOneBelow, fromTwoBelow);
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,5,1,4,3] (walkthrough example)", minCostClimbingStairs(new int[]{2, 5, 1, 4, 3}), 6);
        check("[10,15,20]", minCostClimbingStairs(new int[]{10, 15, 20}), 15);
        check("[1,100,1,1,1,100,1,1,100,1]", minCostClimbingStairs(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}), 6);
        check("[5,7] (edge: shortest allowed input)", minCostClimbingStairs(new int[]{5, 7}), 5);
        check("[0,0,0] (edge: all free)", minCostClimbingStairs(new int[]{0, 0, 0}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
