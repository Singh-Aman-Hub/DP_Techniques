/**
 * Minimum XOR Sum of Two Arrays (LeetCode 1879) — Memoization over a bitmask
 *
 * Idea: in the brute force, the future only depends on WHICH nums2 elements are used,
 *       not on the order in which they were picked. Encode that set as a bitmask.
 *       The index i of the next nums1 element is the number of used elements,
 *       i = bitCount(mask), so the mask alone is the whole state.
 *       best(mask) = min cost to pair the remaining nums1 elements, given mask is used.
 *
 * Time complexity:  O(2^n * n) — 2^n masks, each tries n candidates once.
 * Space complexity: O(2^n) — one memo cell per mask (plus O(n) recursion depth).
 *
 * Run: java MinXorSumMemoization.java
 */
import java.util.Arrays;

public class MinXorSumMemoization {

    // memo[mask] = min extra cost once the nums2 elements in mask are taken; -1 = unknown
    private static int[] memo;

    static int minimumXORSum(int[] nums1, int[] nums2) {
        int n = nums1.length;
        memo = new int[1 << n];
        Arrays.fill(memo, -1);
        return best(nums1, nums2, 0);
    }

    private static int best(int[] nums1, int[] nums2, int usedMask) {
        int n = nums1.length;
        int i = Integer.bitCount(usedMask);              // how many are paired = index of next nums1 element
        if (i == n) return 0;
        if (memo[usedMask] != -1) return memo[usedMask];

        int result = Integer.MAX_VALUE;
        for (int j = 0; j < n; j++) {
            if ((usedMask & (1 << j)) != 0) continue;    // nums2[j] already taken
            int cost = (nums1[i] ^ nums2[j]) + best(nums1, nums2, usedMask | (1 << j));
            result = Math.min(result, cost);
        }
        memo[usedMask] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2] vs [2,3]",         minimumXORSum(new int[]{1, 2}, new int[]{2, 3}), 2);
        check("[1,0,3] vs [5,3,4]",     minimumXORSum(new int[]{1, 0, 3}, new int[]{5, 3, 4}), 8);
        check("single [5] vs [5]",      minimumXORSum(new int[]{5}, new int[]{5}), 0);
        check("same multiset reversed", minimumXORSum(new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1}), 0);
        // n = 14 would need 14! ~ 8.7e10 pairings by brute force; the mask DP needs 2^14 * 14 steps.
        int[] a = new int[14], b = new int[14];
        for (int k = 0; k < 14; k++) { a[k] = k; b[k] = 13 - k; }
        check("n = 14, b is a reversed", minimumXORSum(a, b), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
