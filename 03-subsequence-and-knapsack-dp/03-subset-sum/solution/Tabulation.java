/**
 * Subset Sum — Tabulation (bottom-up boolean DP)
 *
 * Idea: dp[i][s] = true if some subset of the FIRST i elements sums to exactly s.
 * dp[0][0] = true (empty subset), dp[0][s>0] = false. Then
 *   dp[i][s] = dp[i-1][s]  ||  (arr[i-1] <= s && dp[i-1][s - arr[i-1]]).
 * The whole last row tells us EVERY reachable sum, not just the target — topic 05 uses that.
 *
 * Time complexity:  O(n * target) — (n + 1) * (target + 1) cells, O(1) each.
 * Space complexity: O(n * target) — the full boolean table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean canMakeSum(int[] arr, int target) {
        int n = arr.length;
        // dp[i][s] = can a subset of arr[0..i-1] sum to exactly s?
        boolean[][] dp = new boolean[n + 1][target + 1];
        dp[0][0] = true;                                  // the empty subset sums to 0
        for (int i = 1; i <= n; i++) {
            int value = arr[i - 1];
            for (int s = 0; s <= target; s++) {
                dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
            }
        }
        return dp[n][target];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[3,4,5,2], target 9 (4+5)", canMakeSum(new int[]{3, 4, 5, 2}, 9), true);
        check("[3,4,5,2], target 13 (only 1 short of the total, but there is no 1)", canMakeSum(new int[]{3, 4, 5, 2}, 13), false);
        check("[3,4,5,2], target 1 (smaller than every element)", canMakeSum(new int[]{3, 4, 5, 2}, 1), false);
        check("[3,4,5,2], target 0 (edge: the empty subset)", canMakeSum(new int[]{3, 4, 5, 2}, 0), true);
        check("[3,34,4,12,5,2], target 9", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 9), true);
        check("[3,34,4,12,5,2], target 30", canMakeSum(new int[]{3, 34, 4, 12, 5, 2}, 30), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
