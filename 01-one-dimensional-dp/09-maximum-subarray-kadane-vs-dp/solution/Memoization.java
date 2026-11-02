/**
 * Maximum Subarray (LeetCode 53) — Memoization (top-down DP)
 *
 * Idea: endingAt(i) = largest sum of a subarray that ENDS EXACTLY at index i.
 * Such a subarray is either nums[i] alone, or nums[i] appended to the best
 * subarray ending at i-1:  endingAt(i) = nums[i] + max(0, endingAt(i-1)).
 * The answer is the maximum of endingAt(i) over all i, because the best
 * subarray ends somewhere.
 *
 * Time complexity:  O(n) — n states, O(1) each.
 * Space complexity: O(n) — memo plus a recursion stack up to n deep.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i] = endingAt(i); null = not computed (sums can be negative, so no numeric sentinel)
    private static Integer[] memo;

    static int maxSubArray(int[] nums) {
        memo = new Integer[nums.length];
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < nums.length; i++) {
            best = Math.max(best, endingAt(nums, i));   // the best subarray ends at some i
        }
        return best;
    }

    private static int endingAt(int[] nums, int i) {
        if (i == 0) return nums[0];                     // only one subarray ends at index 0
        if (memo[i] != null) return memo[i];
        int extend = endingAt(nums, i - 1);             // best subarray ending just before i
        memo[i] = nums[i] + Math.max(0, extend);        // extend it only if it helps
        return memo[i];
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
