/**
 * Longest Increasing Subsequence (LeetCode 300) — Brute force recursion
 *
 * Idea: Walk through the array left to right. At position `index` we remember only
 *       the index of the last element we took (`prevIndex`, or -1 if none yet).
 *       Choices: skip nums[index], or take it if it is strictly larger than
 *       nums[prevIndex]. The answer is the longer of the two choices.
 *
 * Time complexity:  O(2^n) — each element is either taken or skipped.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int lengthOfLIS(int[] nums) {
        return longest(nums, 0, -1);
    }

    // Length of the longest increasing subsequence of nums[index..] whose first element
    // is greater than nums[prevIndex] (no restriction when prevIndex == -1).
    private static int longest(int[] nums, int index, int prevIndex) {
        if (index == nums.length) return 0;   // nothing left to choose from

        int skip = longest(nums, index + 1, prevIndex);
        int take = 0;
        if (prevIndex == -1 || nums[index] > nums[prevIndex]) {
            take = 1 + longest(nums, index + 1, index);   // nums[index] becomes the new "last taken"
        }
        return Math.max(skip, take);
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
