/**
 * Target Sum (LC 494) — Brute force over all sign assignments
 *
 * Idea: each number gets a '+' or a '-'. Recurse over the indices carrying the running sum;
 * at the end, count 1 if the running sum equals target. 2^n assignments in total.
 *
 * Time complexity:  O(2^n) — two signs per number (n <= 20 on LeetCode, so about 10^6 leaves).
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int findTargetSumWays(int[] nums, int target) {
        return count(nums, 0, 0, target);
    }

    private static int count(int[] nums, int i, int runningSum, int target) {
        if (i == nums.length) return (runningSum == target) ? 1 : 0;
        int plus = count(nums, i + 1, runningSum + nums[i], target);
        int minus = count(nums, i + 1, runningSum - nums[i], target);
        return plus + minus;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,1,1,1,1], target 3", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3), 5);
        check("[1,1,1,1,1], target -3 (negative target)", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, -3), 5);
        check("[1,2,1], target 0 (+1-2+1 and -1+2-1)", findTargetSumWays(new int[]{1, 2, 1}, 0), 2);
        check("[0,0,1], target 1 (each zero doubles the count)", findTargetSumWays(new int[]{0, 0, 1}, 1), 4);
        check("[2], target 1 (parity: total + target is odd)", findTargetSumWays(new int[]{2}, 1), 0);
        check("[100], target -200 (|target| > total)", findTargetSumWays(new int[]{100}, -200), 0);
        check("[1], target 1 (edge: single element)", findTargetSumWays(new int[]{1}, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
