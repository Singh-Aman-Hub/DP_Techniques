/**
 * Maximum Subarray (LeetCode 53) — Brute force over all subarrays
 *
 * Idea: try every start index; extend the end one step at a time while keeping
 * a running sum, so each subarray's sum costs O(1) instead of O(length).
 * (The even more naive triple loop that re-adds every subarray is O(n^3).)
 *
 * Time complexity:  O(n^2) — n(n+1)/2 subarrays, O(1) each with the running sum.
 * Space complexity: O(1) — a few variables.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int maxSubArray(int[] nums) {
        int best = Integer.MIN_VALUE;              // a subarray must be non-empty
        for (int start = 0; start < nums.length; start++) {
            int runningSum = 0;                    // sum of nums[start..end]
            for (int end = start; end < nums.length; end++) {
                runningSum += nums[end];
                best = Math.max(best, runningSum);
            }
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[-2,1,-3,4,-1,2,1,-5,4] (walkthrough example)", maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}), 6);
        check("[5,4,-1,7,8] (whole array)", maxSubArray(new int[]{5, 4, -1, 7, 8}), 23);
        check("[1] (edge: single element)", maxSubArray(new int[]{1}), 1);
        check("[-3,-1,-2] (edge: all negative, must pick one)", maxSubArray(new int[]{-3, -1, -2}), -1);
        check("[-1] (edge: single negative)", maxSubArray(new int[]{-1}), -1);
        check("[2,-5,3] (restart beats extending)", maxSubArray(new int[]{2, -5, 3}), 3);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
