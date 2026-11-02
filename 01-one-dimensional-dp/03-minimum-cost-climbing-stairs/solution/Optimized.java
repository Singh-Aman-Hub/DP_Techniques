/**
 * Min Cost Climbing Stairs (LeetCode 746) — Space-optimized DP
 *
 * Idea: reach[i] reads only reach[i-1] and reach[i-2], so keep two variables.
 *
 * Time complexity:  O(n) — one loop.
 * Space complexity: O(1) — two variables.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int minCostClimbingStairs(int[] cost) {
        int twoBelow = 0;   // reach(i-2), starts as reach(0)
        int oneBelow = 0;   // reach(i-1), starts as reach(1)
        for (int i = 2; i <= cost.length; i++) {
            int current = Math.min(oneBelow + cost[i - 1], twoBelow + cost[i - 2]);
            twoBelow = oneBelow;
            oneBelow = current;
        }
        return oneBelow;    // reach(n), the top of the floor
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
