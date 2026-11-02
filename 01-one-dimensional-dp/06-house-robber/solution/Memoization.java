/**
 * House Robber (LeetCode 198) — Memoization (top-down DP)
 *
 * Idea: maxLoot(i) = max(maxLoot(i-1), nums[i] + maxLoot(i-2)), cached.
 *
 * Time complexity:  O(n) — n states, O(1) each.
 * Space complexity: O(n) — memo plus recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = maximum loot from houses 0..i; -1 = not computed (loot is never negative)
    private static int[] memo;

    static int rob(int[] nums) {
        memo = new int[nums.length];
        Arrays.fill(memo, -1);
        return maxLoot(nums, nums.length - 1);
    }

    private static int maxLoot(int[] nums, int i) {
        if (i < 0) return 0;
        if (memo[i] != -1) return memo[i];
        memo[i] = Math.max(maxLoot(nums, i - 1), nums[i] + maxLoot(nums, i - 2));
        return memo[i];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 12);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[2,1,1,2] (best picks are 3 apart)", rob(new int[]{2, 1, 1, 2}), 4);
        check("[5] (edge: one house)", rob(new int[]{5}), 5);
        check("[0,0] (edge: nothing to steal)", rob(new int[]{0, 0}), 0);
        int[] many = new int[100];                  // LeetCode's maximum length
        Arrays.fill(many, 400);                     // LeetCode's maximum value
        check("100 houses of 400 (rob every other one)", rob(many), 50 * 400);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
