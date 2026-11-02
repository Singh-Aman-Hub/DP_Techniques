/**
 * Min Cost Climbing Stairs (LeetCode 746) — Brute force recursion
 *
 * Idea: let reach(i) be the cheapest total price to STAND on step i, where the
 * "top of the floor" is step n (one past the last array index). We arrive at i
 * either from i-1 (paying cost[i-1] to leave it) or from i-2 (paying cost[i-2]).
 * Steps 0 and 1 are free starting points. Plain recursion tries every path.
 *
 * Time complexity:  O(2^n) — two recursive calls per level, no reuse.
 * Space complexity: O(n) — recursion depth up to n.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int minCostClimbingStairs(int[] cost) {
        return reach(cost, cost.length);      // the top is index n, not n-1
    }

    private static int reach(int[] cost, int i) {
        if (i <= 1) return 0;                 // we may start on step 0 or step 1 for free
        int fromOneBelow = reach(cost, i - 1) + cost[i - 1];
        int fromTwoBelow = reach(cost, i - 2) + cost[i - 2];
        return Math.min(fromOneBelow, fromTwoBelow);
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
