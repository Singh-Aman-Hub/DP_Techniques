/**
 * Partition Equal Subset Sum (LC 416) — Brute force
 *
 * Idea: if the array splits into two groups of equal sum, each group sums to total / 2.
 * So the question becomes "is there a subset with sum total / 2?" — subset sum. The odd-total
 * shortcut answers false immediately. Then plain take/skip recursion searches for half.
 *
 * Time complexity:  O(2^n) — two branches per element.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) return false;           // an odd total cannot be split into two equal integers
        int half = total / 2;
        return canMake(nums, 0, half);
    }

    // can some subset of nums[i..n-1] sum to exactly remaining?
    private static boolean canMake(int[] nums, int i, int remaining) {
        if (remaining == 0) return true;
        if (i == nums.length) return false;
        boolean skip = canMake(nums, i + 1, remaining);
        boolean take = nums[i] <= remaining && canMake(nums, i + 1, remaining - nums[i]);
        return skip || take;
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
