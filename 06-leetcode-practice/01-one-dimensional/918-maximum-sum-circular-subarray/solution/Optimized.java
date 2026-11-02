/**
 * 918. Maximum Sum Circular Subarray — Space-optimized
 *
 * Idea: Kadane for the maximum and Kadane for the minimum in the same pass, each keeping only
 * its value at the previous index. Answer = max(bestNormal, total - minBlock), unless all
 * numbers are negative.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — five integers.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxSubarraySumCircular(int[] nums) {
        int curMax = 0, curMin = 0;                 // best / worst sum ending at the previous index
        int bestNormal = Integer.MIN_VALUE, minBlock = Integer.MAX_VALUE, total = 0;
        for (int x : nums) {
            curMax = Math.max(x, curMax + x);
            curMin = Math.min(x, curMin + x);
            bestNormal = Math.max(bestNormal, curMax);
            minBlock = Math.min(minBlock, curMin);
            total += x;
        }
        if (bestNormal < 0) return bestNormal;      // the "wrap" would be the empty subarray
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
