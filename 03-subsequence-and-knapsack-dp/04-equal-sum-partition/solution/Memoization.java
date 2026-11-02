/**
 * Partition Equal Subset Sum (LC 416) — Memoization
 *
 * Idea: reduce to subset sum with target total / 2, then cache canMake(i, remaining).
 *
 * Time complexity:  O(n * total/2) — n * (half + 1) states, O(1) each. With LC limits
 *                   (n <= 200, values <= 100) that is at most 200 * 10001 ≈ 2 * 10^6.
 * Space complexity: O(n * total/2) for the memo, plus O(n) recursion stack.
 *
 * Run: java Memoization.java
 */
public class Memoization {

    // memo[i][r] = can a subset of nums[i..n-1] sum to exactly r?  null = not computed
    private static Boolean[][] memo;

    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) return false;           // an odd total cannot be split into two equal integers
        int half = total / 2;
        memo = new Boolean[nums.length + 1][half + 1];
        return canMake(nums, 0, half);
    }

    private static boolean canMake(int[] nums, int i, int remaining) {
        if (remaining == 0) return true;
        if (i == nums.length) return false;
        if (memo[i][remaining] != null) return memo[i][remaining];
        boolean skip = canMake(nums, i + 1, remaining);
        boolean take = nums[i] <= remaining && canMake(nums, i + 1, remaining - nums[i]);
        memo[i][remaining] = skip || take;
        return memo[i][remaining];
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
