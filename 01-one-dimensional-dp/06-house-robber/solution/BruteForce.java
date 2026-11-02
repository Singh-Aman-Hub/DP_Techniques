/**
 * House Robber (LeetCode 198) — Brute force recursion
 *
 * Idea: the pick / not-pick recursion from "Maximum Sum of Non-Adjacent Elements".
 * maxLoot(i) = best loot from houses 0..i:
 *   skip house i -> maxLoot(i-1);   rob house i -> nums[i] + maxLoot(i-2).
 *
 * Time complexity:  O(2^n) — two branches per house, no reuse.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int rob(int[] nums) {
        return maxLoot(nums, nums.length - 1);
    }

    private static int maxLoot(int[] nums, int i) {
        if (i < 0) return 0;                              // no houses left
        int skip = maxLoot(nums, i - 1);
        int robIt = nums[i] + maxLoot(nums, i - 2);       // neighbour i-1 is now off-limits
        return Math.max(skip, robIt);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 12);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[2,1,1,2] (best picks are 3 apart)", rob(new int[]{2, 1, 1, 2}), 4);
        check("[5] (edge: one house)", rob(new int[]{5}), 5);
        check("[0,0] (edge: nothing to steal)", rob(new int[]{0, 0}), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
