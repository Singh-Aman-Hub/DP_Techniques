/**
 * House Robber (LeetCode 198) — Tabulation (bottom-up DP)
 *
 * Idea: fill maxLoot[0..n-1] left to right. Also includes the equivalent
 * "two-state" table (robbed[i] / skipped[i]), which says explicitly whether
 * house i was robbed — the first glimpse of state-machine DP.
 *
 * Time complexity:  O(n) — one pass for each version.
 * Space complexity: O(n) — the tables.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int rob(int[] nums) {
        int n = nums.length;
        // maxLoot[i] = maximum loot from houses 0..i
        int[] maxLoot = new int[n];
        for (int i = 0; i < n; i++) {
            int skip = (i >= 1) ? maxLoot[i - 1] : 0;
            int robIt = nums[i] + ((i >= 2) ? maxLoot[i - 2] : 0);
            maxLoot[i] = Math.max(skip, robIt);
        }
        return maxLoot[n - 1];
    }

    static int robTwoStates(int[] nums) {
        int n = nums.length;
        // robbed[i]  = best loot from houses 0..i given that house i IS robbed
        // skipped[i] = best loot from houses 0..i given that house i is NOT robbed
        int[] robbed = new int[n];
        int[] skipped = new int[n];
        robbed[0] = nums[0];
        skipped[0] = 0;
        for (int i = 1; i < n; i++) {
            robbed[i] = skipped[i - 1] + nums[i];                 // neighbour must be skipped
            skipped[i] = Math.max(robbed[i - 1], skipped[i - 1]); // neighbour may be either
        }
        return Math.max(robbed[n - 1], skipped[n - 1]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 12);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[2,1,1,2] (best picks are 3 apart)", rob(new int[]{2, 1, 1, 2}), 4);
        check("[5] (edge: one house)", rob(new int[]{5}), 5);
        check("[0,0] (edge: nothing to steal)", rob(new int[]{0, 0}), 0);
        check("two-state version: [2,7,9,3,1]", robTwoStates(new int[]{2, 7, 9, 3, 1}), 12);
        check("two-state version: [2,1,1,2]", robTwoStates(new int[]{2, 1, 1, 2}), 4);
        check("two-state version: [5]", robTwoStates(new int[]{5}), 5);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
