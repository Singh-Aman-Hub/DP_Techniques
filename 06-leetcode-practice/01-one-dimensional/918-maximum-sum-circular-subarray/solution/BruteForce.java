/**
 * 918. Maximum Sum Circular Subarray — Brute force
 *
 * Idea: Try every start index and every length 1..n, walking around the circle with % n and
 * keeping a running sum. Useful as a correctness reference for the DP versions.
 *
 * Time complexity:  O(n^2) — n starts times n lengths, O(1) per step thanks to the running sum.
 * Space complexity: O(1) — only counters.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int maxSubarraySumCircular(int[] nums) {
        int n = nums.length;
        int best = Integer.MIN_VALUE;
        for (int start = 0; start < n; start++) {
            int runningSum = 0;
            for (int length = 1; length <= n; length++) {      // never longer than n: no reuse
                runningSum += nums[(start + length - 1) % n];
                best = Math.max(best, runningSum);
            }
        }
        return best;
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
