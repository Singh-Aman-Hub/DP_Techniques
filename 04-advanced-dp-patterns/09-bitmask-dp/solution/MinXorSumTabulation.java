/**
 * Minimum XOR Sum of Two Arrays (LeetCode 1879) — Tabulation over masks
 *
 * Idea: dp[mask] = min cost of pairing the first bitCount(mask) elements of nums1
 *       with exactly the nums2 elements in mask. To extend a finished state, give the
 *       next nums1 element (index bitCount(mask)) any free nums2 element j:
 *         dp[mask | (1 << j)] = min(..., dp[mask] + (nums1[i] ^ nums2[j])).
 *       Adding a bit always makes the number larger, so looping mask = 0, 1, 2, ...
 *       finishes every state before it is extended.
 *
 * Time complexity:  O(2^n * n) — every mask tries every j once.
 * Space complexity: O(2^n) — one cell per mask. No smaller table works: any mask can
 *                   be extended into many larger ones, so all must be kept.
 *
 * Run: java MinXorSumTabulation.java
 */
import java.util.Arrays;

public class MinXorSumTabulation {

    static int minimumXORSum(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int full = (1 << n) - 1;
        // dp[mask] = min cost so far when the nums2 elements in mask are used
        int[] dp = new int[1 << n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;                                       // nothing paired, nothing paid

        for (int mask = 0; mask < full; mask++) {
            if (dp[mask] == Integer.MAX_VALUE) continue; // never happens here, but keeps the add safe
            int i = Integer.bitCount(mask);              // next nums1 element to pair
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) continue;
                int next = mask | (1 << j);
                dp[next] = Math.min(dp[next], dp[mask] + (nums1[i] ^ nums2[j]));
            }
        }
        return dp[full];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,2] vs [2,3]",         minimumXORSum(new int[]{1, 2}, new int[]{2, 3}), 2);
        check("[1,0,3] vs [5,3,4]",     minimumXORSum(new int[]{1, 0, 3}, new int[]{5, 3, 4}), 8);
        check("single [5] vs [5]",      minimumXORSum(new int[]{5}, new int[]{5}), 0);
        check("same multiset reversed", minimumXORSum(new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1}), 0);
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
