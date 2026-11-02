/**
 * 918. Maximum Sum Circular Subarray — Memoization (top-down)
 *
 * Idea: maxEnd(i) / minEnd(i) = largest / smallest sum of a subarray ending at i (Kadane as
 * recursion). The best wrapping subarray is total minus the smallest block, unless every
 * number is negative (then that "wrap" would be empty and we return the best normal sum).
 *
 * Time complexity:  O(n) — 2n cached states, O(1) each.
 * Space complexity: O(n) — two memo arrays plus a recursion chain of depth n.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    private static int[] nums;
    private static Integer[] maxMemo;   // maxMemo[i] = largest sum of a subarray ending at i
    private static Integer[] minMemo;   // minMemo[i] = smallest sum of a subarray ending at i

    static int maxSubarraySumCircular(int[] input) {
        nums = input;
        maxMemo = new Integer[nums.length];
        minMemo = new Integer[nums.length];
        int total = 0, bestNormal = Integer.MIN_VALUE, minBlock = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            total += nums[i];
            bestNormal = Math.max(bestNormal, maxEnd(i));
            minBlock = Math.min(minBlock, minEnd(i));
        }
        if (bestNormal < 0) return bestNormal;            // all negative: wrap would be empty
        return Math.max(bestNormal, total - minBlock);
    }

    private static int maxEnd(int i) {
        if (i == 0) return nums[0];
        if (maxMemo[i] == null) maxMemo[i] = Math.max(nums[i], maxEnd(i - 1) + nums[i]);
        return maxMemo[i];
    }

    private static int minEnd(int i) {
        if (i == 0) return nums[0];
        if (minMemo[i] == null) minMemo[i] = Math.min(nums[i], minEnd(i - 1) + nums[i]);
        return minMemo[i];
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
