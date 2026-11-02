/**
 * House Robber (LC 198) — 1D DP reduced to O(1) space
 *
 * Idea: dp[i] = max(dp[i-1], dp[i-2] + nums[i]). The footprint of cell i is
 * {i-1, i-2}: only the two previous cells are ever read. So instead of a whole
 * array we keep two variables, "prev1" (= dp[i-1]) and "prev2" (= dp[i-2]),
 * and shift them forward after every step.
 *
 * Time complexity:  O(n) for both versions — each house is processed once with O(1) work.
 * Space complexity: O(n) for the table version (one cell per house),
 *                   O(1) for the optimized version (two variables, whatever n is).
 *
 * Run: java HouseRobber.java
 */
import java.util.Random;

public class HouseRobber {

    /** Full-table version: dp[i] = best loot using houses 0..i. */
    static int robTable(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;
        if (n == 1) return nums[0];
        // dp[i] = maximum money from houses 0..i without robbing two adjacent houses
        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < n; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }
        return dp[n - 1];
    }

    /** O(1)-space version: only dp[i-1] and dp[i-2] are alive at any moment. */
    static int robConstantSpace(int[] nums) {
        int prev2 = 0; // dp[i-2]; before the first house this is "no houses" = 0
        int prev1 = 0; // dp[i-1]
        for (int money : nums) {
            int current = Math.max(prev1, prev2 + money);
            // Shift the window: the old prev2 is never read again, so it can be dropped.
            prev2 = prev1;
            prev1 = current;
        }
        return prev1;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        int[][] inputs = { {2, 7, 9, 3, 1}, {1, 2, 3, 1}, {}, {5}, {2, 1, 1, 2} };
        int[] expected = { 12, 4, 0, 5, 4 };
        for (int t = 0; t < inputs.length; t++) {
            String label = java.util.Arrays.toString(inputs[t]);
            check("table    " + label, robTable(inputs[t]), expected[t]);
            check("O(1)     " + label, robConstantSpace(inputs[t]), expected[t]);
        }

        // Randomized cross-check: the O(1) version must always agree with the table.
        Random random = new Random(2024);
        boolean allAgree = true;
        for (int trial = 0; trial < 2000 && allAgree; trial++) {
            int[] nums = new int[random.nextInt(12)];
            for (int i = 0; i < nums.length; i++) nums[i] = random.nextInt(100);
            allAgree = robTable(nums) == robConstantSpace(nums);
        }
        check("2000 random arrays agree", allAgree ? 1 : 0, 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
