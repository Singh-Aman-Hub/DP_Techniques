/**
 * 1035. Uncrossed Lines — Memoization (top-down)
 *
 * Idea: non-crossing segments between equal values pair up positions that
 * increase in both arrays, which is a common subsequence. So the answer is the
 * LCS length. f(i, j) = best using the first i numbers of nums1 and the first j
 * of nums2; equal last values connect, otherwise drop one side.
 *
 * Time complexity:  O(n * m) — (n+1)*(m+1) states, each solved once with O(1) work
 * Space complexity: O(n * m) — memo table plus recursion depth up to n + m
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;
public class Memoization {

    // memo[i][j] = max segments using prefixes of length i and j; -1 = not computed
    private static int[][] memo;
    private static int[] top, bottom;

    static int maxUncrossedLines(int[] nums1, int[] nums2) {
        top = nums1;
        bottom = nums2;
        memo = new int[nums1.length + 1][nums2.length + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return best(nums1.length, nums2.length);
    }

    private static int best(int i, int j) {
        if (i == 0 || j == 0) return 0;                       // an empty array has nothing to connect
        if (memo[i][j] != -1) return memo[i][j];
        int result;
        if (top[i - 1] == bottom[j - 1]) {
            result = 1 + best(i - 1, j - 1);                  // connecting equal last values is always safe
        } else {
            result = Math.max(best(i - 1, j), best(i, j - 1)); // one of the two last values stays unused
        }
        memo[i][j] = result;
        return result;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [1,4,2] / [1,2,4]", maxUncrossedLines(new int[]{1, 4, 2}, new int[]{1, 2, 4}), 2);
        check("[2,5,1,2,5] / [10,5,2,1,5,2]", maxUncrossedLines(new int[]{2, 5, 1, 2, 5}, new int[]{10, 5, 2, 1, 5, 2}), 3);
        check("[1,3,7,1,7,5] / [1,9,2,5,1]", maxUncrossedLines(new int[]{1, 3, 7, 1, 7, 5}, new int[]{1, 9, 2, 5, 1}), 2);
        check("single, no match (edge)", maxUncrossedLines(new int[]{1}, new int[]{2}), 0);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
