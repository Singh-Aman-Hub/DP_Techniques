/**
 * Target Sum (LC 494) — Memoization on (index, runningSum) with an OFFSET
 *
 * Idea: count(i, runningSum) depends only on those two numbers. runningSum can be negative
 * (anywhere in -total..+total), and Java arrays cannot have negative indices, so we store it
 * at column runningSum + total ("the offset"). Column 0 means -total, column total means 0,
 * column 2*total means +total.
 *
 * Time complexity:  O(n * total) — n * (2*total + 1) states, O(1) each.
 * Space complexity: O(n * total) for the memo, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][runningSum + offset] = number of ways to finish from here; null = not computed
    private static Integer[][] memo;
    private static int offset;

    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        if (Math.abs(target) > total) return 0;     // unreachable, and keeps target inside the table
        offset = total;
        memo = new Integer[nums.length][2 * total + 1];
        return count(nums, 0, 0, target);
    }

    private static int count(int[] nums, int i, int runningSum, int target) {
        if (i == nums.length) return (runningSum == target) ? 1 : 0;
        if (memo[i][runningSum + offset] != null) return memo[i][runningSum + offset];
        int plus = count(nums, i + 1, runningSum + nums[i], target);
        int minus = count(nums, i + 1, runningSum - nums[i], target);
        memo[i][runningSum + offset] = plus + minus;
        return plus + minus;
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
