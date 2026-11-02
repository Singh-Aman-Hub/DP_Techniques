/**
 * 877. Stone Game — Space-optimized DP
 *
 * Idea: row i reads diff[i+1][j] (same column, previous row) and diff[i][j-1]
 * (left, current row). One array holds both: dp[j] before overwrite is the row
 * below, dp[j-1] is already the current row; the first player wins when the lead is > 0.
 *
 * Time complexity:  O(n^2) — same loops as tabulation
 * Space complexity: O(n) — one row
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static boolean stoneGame(int[] nums) {
        return scoreDifference(nums) > 0;
    }

    static int scoreDifference(int[] nums) {
        int n = nums.length;
        // dp[j] = diff[i][j] once updated in this pass, diff[i+1][j] before that
        int[] dp = nums.clone();                              // bottom row: diff[j][j] = nums[j]
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                dp[j] = Math.max(nums[i] - dp[j], nums[j] - dp[j - 1]);
            }
        }
        return dp[n - 1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("example [5,3,4,5] lead", scoreDifference(new int[]{5, 3, 4, 5}), 1);
        check("[3,7,2,3] lead", scoreDifference(new int[]{3, 7, 2, 3}), 5);
        check("[2,8,3,1] lead", scoreDifference(new int[]{2, 8, 3, 1}), 4);
        check("two piles (edge) lead", scoreDifference(new int[]{1, 2}), 1);
        check("Alice wins [5,3,4,5] (1 = true)", stoneGame(new int[]{5, 3, 4, 5}) ? 1 : 0, 1);
    }

    private static void check(String label, int actual, int expected) {
        boolean ok = actual == expected;
        System.out.println((ok ? "PASS" : "FAIL") + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (!ok) System.exit(1);
    }
}
