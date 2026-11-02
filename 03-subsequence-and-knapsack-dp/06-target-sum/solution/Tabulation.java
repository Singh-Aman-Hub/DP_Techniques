/**
 * Target Sum (LC 494) — Tabulation after the algebra trick (count subsets with sum s1)
 *
 * Idea: put the '+' numbers in group P and the '-' numbers in group N. Then
 * P - N = target and P + N = total, so P = (total + target) / 2. Every sign assignment is
 * exactly one subset P, so the answer is "how many subsets sum to (total + target) / 2".
 * dp[i][s] = number of subsets of the first i numbers with sum exactly s. The sum loop starts
 * at s = 0 so that zeros are counted twice (as '+0' and '-0').
 *
 * Time complexity:  O(n * s1) — (n + 1) * (s1 + 1) cells, O(1) each.
 * Space complexity: O(n * s1) — the full table.
 *
 * Run: java Tabulation.java
 */
public class Tabulation {

    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        // s1 - s2 = target and s1 + s2 = total  =>  s1 = (total + target) / 2
        if (Math.abs(target) > total) return 0;          // even all-plus or all-minus cannot reach it
        if ((total + target) % 2 != 0) return 0;         // s1 would not be a whole number
        int s1 = (total + target) / 2;                   // 0 <= s1 <= total thanks to the checks above
        int n = nums.length;
        // dp[i][s] = number of subsets of nums[0..i-1] whose sum is exactly s
        int[][] dp = new int[n + 1][s1 + 1];
        dp[0][0] = 1;                                    // the empty subset
        for (int i = 1; i <= n; i++) {
            int value = nums[i - 1];
            for (int s = 0; s <= s1; s++) {              // s starts at 0: a zero can be taken at sum 0
                dp[i][s] = dp[i - 1][s];
                if (value <= s) dp[i][s] += dp[i - 1][s - value];
            }
        }
        return dp[n][s1];
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[1,1,1,1,1], target 3", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3), 5);
        check("[1,1,1,1,1], target -3 (negative target)", findTargetSumWays(new int[]{1, 1, 1, 1, 1}, -3), 5);
        check("[1,2,1], target 0 (+1-2+1 and -1+2-1)", findTargetSumWays(new int[]{1, 2, 1}, 0), 2);
        check("[0,0,1], target 1 (each zero doubles the count)", findTargetSumWays(new int[]{0, 0, 1}, 1), 4);
        check("[2], target 1 (parity: total + target is odd)", findTargetSumWays(new int[]{2}, 1), 0);
        check("[100], target -200 (|target| > total)", findTargetSumWays(new int[]{100}, -200), 0);
        check("[1], target 1 (edge: single element)", findTargetSumWays(new int[]{1}, 1), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
