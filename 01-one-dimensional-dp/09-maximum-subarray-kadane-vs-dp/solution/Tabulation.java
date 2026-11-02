/**
 * Maximum Subarray (LeetCode 53) — Tabulation (bottom-up DP)
 *
 * Idea: endingAt[i] = nums[i] + max(0, endingAt[i-1]), filled left to right;
 * the answer is the maximum cell of the table (not the last cell!).
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(n) — the table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxSubArray(int[] nums) {
        int n = nums.length;
        // endingAt[i] = largest sum of a non-empty subarray whose last element is nums[i]
        int[] endingAt = new int[n];
        endingAt[0] = nums[0];
        int best = endingAt[0];
        for (int i = 1; i < n; i++) {
            endingAt[i] = nums[i] + Math.max(0, endingAt[i - 1]);  // restart or extend
            best = Math.max(best, endingAt[i]);
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
