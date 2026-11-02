/**
 * 718. Maximum Length of Repeated Subarray — Brute force
 *
 * Idea: try every pair of starting positions (i in nums1, j in nums2) and walk
 * forward while the values match. The longest walk is the answer. Shown only to
 * expose the repeated work that the DP removes.
 *
 * Time complexity:  O(n * m * min(n, m)) — n*m starting pairs, each walk is up to min(n, m) long
 * Space complexity: O(1) — only counters
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int findLength(int[] nums1, int[] nums2) {
        int best = 0;
        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                int length = 0;
                // extend the block while both arrays still agree
                while (i + length < nums1.length && j + length < nums2.length
                        && nums1[i + length] == nums2[j + length]) {
                    length++;
                }
                best = Math.max(best, length);
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
