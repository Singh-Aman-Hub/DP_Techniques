/**
 * House Robber II (LeetCode 213) — Space-optimized DP
 *
 * Idea: two runs of the two-variable House Robber loop, one excluding the last
 * house and one excluding the first, then take the larger result.
 *
 * Time complexity:  O(n) — two linear passes.
 * Space complexity: O(1) — two variables per pass.
 *
 * Run: java Optimized.java
 */
public class Optimized {

    static int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robRange(nums, 0, n - 2), robRange(nums, 1, n - 1));
    }

    private static int robRange(int[] nums, int lo, int hi) {
        int lootTwoBack = 0, lootOneBack = 0;
        for (int i = lo; i <= hi; i++) {
            int current = Math.max(lootOneBack, nums[i] + lootTwoBack);
            lootTwoBack = lootOneBack;
            lootOneBack = current;
        }
        return lootOneBack;
    }

    // ---------- self-check ----------
    public static void main(String[] args) {
        check("[2,7,9,3,1] (walkthrough example)", rob(new int[]{2, 7, 9, 3, 1}), 11);
        check("[2,3,2] (first and last are neighbours)", rob(new int[]{2, 3, 2}), 3);
        check("[1,2,3,1]", rob(new int[]{1, 2, 3, 1}), 4);
        check("[200,3,140,20,10] (best plan avoids the last house)", rob(new int[]{200, 3, 140, 20, 10}), 340);
        check("[5] (edge: one house is its own neighbour-free circle)", rob(new int[]{5}), 5);
        check("[1,1] (edge: two houses)", rob(new int[]{1, 1}), 1);
    }

    private static void check(String label, long actual, long expected) {
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.println(status + "  " + label + " -> " + actual + " (expected " + expected + ")");
        if (actual != expected) System.exit(1);
    }
}
