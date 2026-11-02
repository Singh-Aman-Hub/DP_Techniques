/**
 * 1035. Uncrossed Lines — Space-optimized DP
 *
 * Idea: each row reads only the previous row. Keep one array; before
 * overwriting row[j], save it so it can serve as the diagonal dp[i-1][j-1]
 * for the next column.
 *
 * Time complexity:  O(n * m) — same loops as tabulation
 * Space complexity: O(m) — one row plus one saved diagonal value
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int maxUncrossedLines(int[] nums1, int[] nums2) {
        int m = nums2.length;
        // row[j] = dp[i][j] once written in this pass, dp[i-1][j] before that
        int[] row = new int[m + 1];
        for (int i = 1; i <= nums1.length; i++) {
            int diagonal = 0;                                 // dp[i-1][0]
            for (int j = 1; j <= m; j++) {
                int above = row[j];                           // dp[i-1][j], saved before overwrite
                if (nums1[i - 1] == nums2[j - 1]) {
                    row[j] = diagonal + 1;
                } else {
                    row[j] = Math.max(above, row[j - 1]);
                }
                diagonal = above;                             // this is dp[i-1][j-1] for the next j
            }
        }
        return row[m];
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
