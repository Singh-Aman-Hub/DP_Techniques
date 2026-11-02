/**
 * Partition Equal Subset Sum (LC 416) — Tabulation
 *
 * Idea: dp[i][s] = can a subset of the first i numbers sum to exactly s? Fill rows top to
 * bottom; the answer is dp[n][total / 2].
 *
 * Time complexity:  O(n * total/2) — every cell once, O(1) each.
 * Space complexity: O(n * total/2) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) return false;           // an odd total cannot be split into two equal integers
        int half = total / 2;
        int n = nums.length;
        // dp[i][s] = can a subset of nums[0..i-1] sum to exactly s?
        boolean[][] dp = new boolean[n + 1][half + 1];
        dp[0][0] = true;
        for (int i = 1; i <= n; i++) {
            int value = nums[i - 1];
            for (int s = 0; s <= half; s++) {
                dp[i][s] = dp[i - 1][s] || (value <= s && dp[i - 1][s - value]);
            }
        }
        return dp[n][half];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,5,11,5] -> {1,5,5} and {11}", canPartition(new int[]{1, 5, 11, 5}), true);
        check("[1,2,3,5] (total 11 is odd)", canPartition(new int[]{1, 2, 3, 5}), false);
        check("[1,2,5] (total 8, but no subset makes 4)", canPartition(new int[]{1, 2, 5}), false);
        check("[3,3,3,4,5] -> {4,5} and {3,3,3}", canPartition(new int[]{3, 3, 3, 4, 5}), true);
        check("[2,2] (edge: two equal items)", canPartition(new int[]{2, 2}), true);
        check("[1] (edge: a single element can never be split evenly)", canPartition(new int[]{1}), false);
    }

    private static void check(String label, boolean actual, boolean expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
