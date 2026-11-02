/**
 * Min Cost Climbing Stairs (LeetCode 746) — Tabulation (bottom-up DP)
 *
 * Idea: fill reach[0..n] left to right. reach[n] (one past the last step) is
 * the top of the floor, so the table has n+1 cells, not n.
 *
 * Time complexity:  O(n) — one loop, O(1) work per cell.
 * Space complexity: O(n) — the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        // reach[i] = cheapest total cost to stand on step i; reach[n] is the top
        int[] reach = new int[n + 1];
        reach[0] = 0;                         // free start
        reach[1] = 0;                         // free start
        for (int i = 2; i <= n; i++) {
            reach[i] = Math.min(reach[i - 1] + cost[i - 1],   // 1-step from i-1
                                reach[i - 2] + cost[i - 2]);  // 2-step from i-2
        }
        return reach[n];
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
