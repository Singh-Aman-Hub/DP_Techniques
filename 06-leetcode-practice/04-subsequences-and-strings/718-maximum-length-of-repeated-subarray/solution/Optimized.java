/**
 * 718. Maximum Length of Repeated Subarray — Space-optimized DP
 *
 * Idea: row i of the table reads only the diagonal cell of row i-1. Keep one
 * array and sweep j from right to left, so cur[j-1] still holds the previous
 * row's value when we read it. A mismatch must explicitly write 0.
 *
 * Time complexity:  O(n * m) — same loops as tabulation
 * Space complexity: O(m) — one row of length m+1
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int findLength(int[] nums1, int[] nums2) {
        int m = nums2.length;
        // cur[j] = longest common block ending at the current nums1 element and nums2[j-1]
        int[] cur = new int[m + 1];
        int best = 0;
        for (int i = 1; i <= nums1.length; i++) {
            for (int j = m; j >= 1; j--) {               // right to left keeps cur[j-1] = previous row
                if (nums1[i - 1] == nums2[j - 1]) {
                    cur[j] = cur[j - 1] + 1;
                    best = Math.max(best, cur[j]);
                } else {
                    cur[j] = 0;                           // must overwrite the stale previous-row value
                }
            }
        }
        return best;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,2,3,2,1] / [3,2,1,4,7]", findLength(new int[]{1, 2, 3, 2, 1}, new int[]{3, 2, 1, 4, 7}), 3);
        check("all zeros", findLength(new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}), 5);
        check("nothing shared (edge)", findLength(new int[]{1, 2}, new int[]{3, 4}), 0);
        check("gap not allowed [1,9,2] / [1,2]", findLength(new int[]{1, 9, 2}, new int[]{1, 2}), 1);
    }

    private static void check(String label, int actual, int expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
