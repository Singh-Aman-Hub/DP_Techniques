/**
 * 918. Maximum Sum Circular Subarray — Tabulation (bottom-up)
 *
 * Idea: Fill maxEnd[] and minEnd[] with Kadane's recurrence. The best answer is either the
 * best ordinary subarray, or total minus the smallest block (a wrap), except when all numbers
 * are negative.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(n) — two arrays.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int maxSubarraySumCircular(int[] nums) {
        int n = nums.length;
        int[] maxEnd = new int[n];   // maxEnd[i] = largest sum of a subarray ending at i
        int[] minEnd = new int[n];   // minEnd[i] = smallest sum of a subarray ending at i
        maxEnd[0] = nums[0];
        minEnd[0] = nums[0];
        int total = nums[0], bestNormal = nums[0], minBlock = nums[0];
        for (int i = 1; i < n; i++) {
            maxEnd[i] = Math.max(nums[i], maxEnd[i - 1] + nums[i]);
            minEnd[i] = Math.min(nums[i], minEnd[i - 1] + nums[i]);
            total += nums[i];
            bestNormal = Math.max(bestNormal, maxEnd[i]);
            minBlock = Math.min(minBlock, minEnd[i]);
        }
        if (bestNormal < 0) return bestNormal;
        return Math.max(bestNormal, total - minBlock);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,-2,3,-2]", maxSubarraySumCircular(new int[]{1, -2, 3, -2}), 3);
        check("[5,-3,5]", maxSubarraySumCircular(new int[]{5, -3, 5}), 10);
        check("[-3,-2,-3] (all negative edge)", maxSubarraySumCircular(new int[]{-3, -2, -3}), -2);
        check("[3,-1,2,-1]", maxSubarraySumCircular(new int[]{3, -1, 2, -1}), 4);
        check("[7] (single)", maxSubarraySumCircular(new int[]{7}), 7);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
