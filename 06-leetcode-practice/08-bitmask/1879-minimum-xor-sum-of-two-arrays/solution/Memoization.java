/**
 * 1879. Minimum XOR Sum of Two Arrays — Memoization (bitmask assignment DP)
 *
 * Idea: pair nums1[0], nums1[1], ... in order. The mask records which nums2 elements are
 * taken; the next nums1 index is bitCount(mask). best(mask) = cheapest way to pair the rest.
 *
 * Time complexity:  O(2^n * n) — each mask is solved once and tries n partners.
 * Space complexity: O(2^n) — the memo (recursion depth at most n).
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    private static int[] first, second;
    private static int fullMask;
    // memo[mask] = min XOR sum for pairing the remaining nums1 items with nums2 items outside mask
    private static int[] memo;

    static int minimumXORSum(int[] nums1, int[] nums2) {
        first = nums1;
        second = nums2;
        fullMask = (1 << nums1.length) - 1;
        memo = new int[1 << nums1.length];
        Arrays.fill(memo, -1);
        return best(0);
    }

    private static int best(int usedMask) {
        if (usedMask == fullMask) return 0;                 // everybody is paired
        if (memo[usedMask] != -1) return memo[usedMask];
        int i = Integer.bitCount(usedMask);                 // next nums1 index to pair
        int result = Integer.MAX_VALUE;
        for (int j = 0; j < second.length; j++) {
            if ((usedMask & (1 << j)) != 0) continue;       // nums2[j] already taken
            result = Math.min(result, (first[i] ^ second[j]) + best(usedMask | (1 << j)));
        }
        memo[usedMask] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2] vs [2,3]", minimumXORSum(new int[]{1, 2}, new int[]{2, 3}), 2);
        check("[1,0,3] vs [5,3,4]", minimumXORSum(new int[]{1, 0, 3}, new int[]{5, 3, 4}), 8);
        check("[5] vs [5] (edge: single pair)", minimumXORSum(new int[]{5}, new int[]{5}), 0);
        check("[1] vs [2]", minimumXORSum(new int[]{1}, new int[]{2}), 3);
        // 14 elements, nums2 is nums1 reversed: the best is to pair equal numbers -> 0
        int[] a = new int[14], b = new int[14];
        for (int i = 0; i < 14; i++) { a[i] = i * 7 + 1; b[13 - i] = i * 7 + 1; }
        check("n = 14, permutation of each other", minimumXORSum(a, b), 0);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
