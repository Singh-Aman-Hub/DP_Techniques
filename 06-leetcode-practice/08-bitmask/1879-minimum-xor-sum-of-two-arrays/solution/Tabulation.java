/**
 * 1879. Minimum XOR Sum of Two Arrays — Tabulation (push-style bitmask DP)
 *
 * Idea: cost[mask] = min XOR sum after pairing nums1[0..i-1] (i = bitCount(mask)) with exactly
 * the nums2 elements in mask. From each mask, pair nums1[i] with any unused nums2[j].
 * Transitions add a bit, so iterate masks upward.
 *
 * Time complexity:  O(2^n * n) — each mask pushes to n successors.
 * Space complexity: O(2^n) — one int per mask.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    static int minimumXORSum(int[] nums1, int[] nums2) {
        int n = nums1.length;
        // cost[mask] = cheapest pairing of the first bitCount(mask) nums1 items with nums2 items in mask
        int[] cost = new int[1 << n];
        Arrays.fill(cost, Integer.MAX_VALUE);
        cost[0] = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            int i = Integer.bitCount(mask);
            if (i == n) continue;                           // full mask: nothing left to pair
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) continue;
                int next = mask | (1 << j);
                cost[next] = Math.min(cost[next], cost[mask] + (nums1[i] ^ nums2[j]));
            }
        }
        return cost[(1 << n) - 1];
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
