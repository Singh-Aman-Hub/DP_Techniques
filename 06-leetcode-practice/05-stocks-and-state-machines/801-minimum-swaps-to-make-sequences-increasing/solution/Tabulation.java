/**
 * 801. Minimum Swaps To Make Sequences Increasing — Tabulation (bottom-up)
 *
 * Idea: keep[i] / swap[i] = fewest swaps making prefixes 0..i increasing with index i not
 * swapped / swapped. sameOK allows keep->keep and swap->swap; crossOK allows keep<->swap.
 *
 * Time complexity:  O(n) — one pass, O(1) per index.
 * Space complexity: O(n) — two arrays.
 *
 * Run: java Tabulation.java
 */
import java.util.Arrays;

public class Tabulation {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int minSwap(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int[] keep = new int[n];   // keep[i] = min swaps for 0..i with index i left in place
        int[] swap = new int[n];   // swap[i] = min swaps for 0..i with index i swapped
        Arrays.fill(keep, INF);
        Arrays.fill(swap, INF);
        keep[0] = 0;
        swap[0] = 1;
        for (int i = 1; i < n; i++) {
            boolean sameOK = nums1[i - 1] < nums1[i] && nums2[i - 1] < nums2[i];
            boolean crossOK = nums1[i - 1] < nums2[i] && nums2[i - 1] < nums1[i];
            if (sameOK) {
                keep[i] = Math.min(keep[i], keep[i - 1]);        // neither swapped
                swap[i] = Math.min(swap[i], swap[i - 1] + 1);    // both swapped
            }
            if (crossOK) {
                keep[i] = Math.min(keep[i], swap[i - 1]);        // only i-1 swapped
                swap[i] = Math.min(swap[i], keep[i - 1] + 1);    // only i swapped
            }
        }
        return Math.min(keep[n - 1], swap[n - 1]);
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example 1", minSwap(new int[]{1, 3, 5, 4}, new int[]{1, 2, 3, 7}), 1);
        check("example 2", minSwap(new int[]{0, 3, 5, 8, 9}, new int[]{2, 1, 4, 6, 9}), 1);
        check("single element (edge)", minSwap(new int[]{1}, new int[]{1}), 0);
        check("swap needed early", minSwap(new int[]{0, 4, 4, 5, 9}, new int[]{0, 1, 6, 8, 10}), 1);
        check("two elements", minSwap(new int[]{3, 2}, new int[]{1, 4}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
