/**
 * 801. Minimum Swaps To Make Sequences Increasing — Space-optimized (two variables)
 *
 * Idea: The keep/swap recurrence reads only index i-1, so keep two numbers and compute both
 * new values from the old pair before overwriting.
 *
 * Time complexity:  O(n) — one pass.
 * Space complexity: O(1) — four integers.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    private static final int INF = Integer.MAX_VALUE / 2;

    static int minSwap(int[] nums1, int[] nums2) {
        int keep = 0, swap = 1;                  // states for index 0
        for (int i = 1; i < nums1.length; i++) {
            boolean sameOK = nums1[i - 1] < nums1[i] && nums2[i - 1] < nums2[i];
            boolean crossOK = nums1[i - 1] < nums2[i] && nums2[i - 1] < nums1[i];
            int newKeep = INF, newSwap = INF;
            if (sameOK) {
                newKeep = Math.min(newKeep, keep);
                newSwap = Math.min(newSwap, swap + 1);
            }
            if (crossOK) {
                newKeep = Math.min(newKeep, swap);
                newSwap = Math.min(newSwap, keep + 1);
            }
            keep = newKeep;
            swap = newSwap;
        }
        return Math.min(keep, swap);
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
