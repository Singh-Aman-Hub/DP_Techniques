/**
 * Longest Increasing Subsequence (LeetCode 300) — Memoization on (index, prevIndex)
 *
 * Idea: The brute-force state is (index, prevIndex). prevIndex ranges over -1..n-1,
 *       so we store it shifted by one: column prevIndex + 1. There are n * (n + 1)
 *       states and each is computed once.
 *
 * Time complexity:  O(n^2) — n * (n + 1) states, O(1) work each.
 * Space complexity: O(n^2) for the memo + O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[index][prevIndex + 1] = LIS length of nums[index..] given the last taken index;
    // -1 means "not computed yet".
    private static int[][] memo;

    static int lengthOfLIS(int[] nums) {
        int n = nums.length;
        memo = new int[n][n + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return longest(nums, 0, -1);
    }

    private static int longest(int[] nums, int index, int prevIndex) {
        if (index == nums.length) return 0;
        if (memo[index][prevIndex + 1] != -1) return memo[index][prevIndex + 1];

        int skip = longest(nums, index + 1, prevIndex);
        int take = 0;
        if (prevIndex == -1 || nums[index] > nums[prevIndex]) {
            take = 1 + longest(nums, index + 1, index);
        }
        memo[index][prevIndex + 1] = Math.max(skip, take);
        return memo[index][prevIndex + 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[10,9,2,5,3,7,101,18]", lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}), 4);
        check("[0,1,0,3,2,3]", lengthOfLIS(new int[]{0, 1, 0, 3, 2, 3}), 4);
        check("[7,7,7,7,7,7,7] (strictly increasing!)", lengthOfLIS(new int[]{7, 7, 7, 7, 7, 7, 7}), 1);
        check("[3,4,5,1]", lengthOfLIS(new int[]{3, 4, 5, 1}), 3);
        check("[5] (edge: single element)", lengthOfLIS(new int[]{5}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
