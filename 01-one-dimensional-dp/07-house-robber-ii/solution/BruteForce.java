/**
 * House Robber II (LeetCode 213) — Brute force recursion
 *
 * Idea: in a circle, houses 0 and n-1 are neighbours, so at least one of them
 * is skipped in every valid plan. Solve the straight-line problem twice:
 * on houses 0..n-2 (house n-1 skipped) and on houses 1..n-1 (house 0 skipped),
 * and take the larger. Each line is solved with the plain pick/not-pick recursion.
 *
 * Time complexity:  O(2^n) — exponential line recursion, done twice.
 * Space complexity: O(n) — recursion depth.
 *
 * Run: java BruteForce.java
 */
public class BruteForce {

    static int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];                       // a lone house has no neighbour
        int withoutLast = robLine(nums, 0, n - 2);
        int withoutFirst = robLine(nums, 1, n - 1);
        return Math.max(withoutLast, withoutFirst);
    }

    // best loot from the straight street nums[lo..i] (no wrap-around)
    private static int robLine(int[] nums, int lo, int i) {
        if (i < lo) return 0;
        int skip = robLine(nums, lo, i - 1);
        int robIt = nums[i] + robLine(nums, lo, i - 2);
        return Math.max(skip, robIt);
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
