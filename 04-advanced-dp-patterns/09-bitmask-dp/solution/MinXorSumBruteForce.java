/**
 * Minimum XOR Sum of Two Arrays (LeetCode 1879) — Brute force over all pairings
 *
 * Idea: pair nums1[0], nums1[1], ... in order, each time trying every element of
 *       nums2 that is still free. A boolean array remembers which nums2 elements
 *       are used. This tries all n! pairings.
 *
 * Time complexity:  O(n!) — n choices for the first element, n-1 for the second, ...
 * Space complexity: O(n) — the used[] array and the recursion stack.
 *
 * Run: java MinXorSumBruteForce.java
 */
public class MinXorSumBruteForce {

    static int minimumXORSum(int[] nums1, int[] nums2) {
        return pair(nums1, nums2, 0, new boolean[nums2.length]);
    }

    /** Smallest cost to pair nums1[i..] with the nums2 elements not yet used. */
    private static int pair(int[] nums1, int[] nums2, int i, boolean[] used) {
        if (i == nums1.length) return 0;                 // everyone is paired
        int best = Integer.MAX_VALUE;
        for (int j = 0; j < nums2.length; j++) {
            if (used[j]) continue;
            used[j] = true;                              // choose nums2[j] for nums1[i]
            best = Math.min(best, (nums1[i] ^ nums2[j]) + pair(nums1, nums2, i + 1, used));
            used[j] = false;                             // undo, try the next option
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2] vs [2,3]",         minimumXORSum(new int[]{1, 2}, new int[]{2, 3}), 2);
        check("[1,0,3] vs [5,3,4]",     minimumXORSum(new int[]{1, 0, 3}, new int[]{5, 3, 4}), 8);
        check("single [5] vs [5]",      minimumXORSum(new int[]{5}, new int[]{5}), 0);
        check("same multiset reversed", minimumXORSum(new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1}), 0);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
