/**
 * 718. Maximum Length of Repeated Subarray — Memoization (top-down)
 *
 * Idea: f(i, j) = length of the longest common block that ends exactly at
 * nums1[i-1] and nums2[j-1]. If those values match, the block ending one step
 * earlier on the diagonal grows by one; otherwise no block ends here (0).
 * The answer is the maximum f(i, j) over all cells.
 *
 * Time complexity:  O(n * m) — n*m states, each computed once with O(1) work
 * Space complexity: O(n * m) — memo table; recursion depth is at most min(n, m)
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i][j] = longest common block ending at nums1[i-1], nums2[j-1]; -1 = not computed
    private static int[][] memo;
    private static int[] first;
    private static int[] second;

    static int findLength(int[] nums1, int[] nums2) {
        first = nums1;
        second = nums2;
        memo = new int[nums1.length + 1][nums2.length + 1];
        for (int[] row : memo) Arrays.fill(row, -1);

        int best = 0;
        for (int i = 1; i <= nums1.length; i++) {
            for (int j = 1; j <= nums2.length; j++) {
                best = Math.max(best, endingAt(i, j));
            }
        }
        return best;
    }

    private static int endingAt(int i, int j) {
        if (i == 0 || j == 0) return 0;                 // empty prefix: nothing ends here
        if (memo[i][j] != -1) return memo[i][j];
        int result;
        if (first[i - 1] == second[j - 1]) {
            result = 1 + endingAt(i - 1, j - 1);         // extend the diagonal block
        } else {
            result = 0;                                  // contiguous block is broken
        }
        memo[i][j] = result;
        return result;
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
