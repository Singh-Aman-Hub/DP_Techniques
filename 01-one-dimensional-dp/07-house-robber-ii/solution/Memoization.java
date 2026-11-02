/**
 * House Robber II (LeetCode 213) — Memoization (top-down DP)
 *
 * Idea: answer = max(line(0..n-2), line(1..n-1)), where line(lo..i) is the
 * House Robber recursion restricted to houses lo..i. Each of the two runs
 * gets its own fresh memo, because "best from lo..i" depends on lo.
 *
 * Time complexity:  O(n) — two runs of n-1 states, O(1) each.
 * Space complexity: O(n) — memo plus recursion stack.
 *
 * Run: java Memoization.java
 */
import java.util.Arrays;

public class Memoization {

    // memo[i] = best loot from houses lo..i for the CURRENT run; -1 = not computed
    private static int[] memo;

    static int rob(int[] nums) {
        int n = nums.length;
        if (n == 1) return nums[0];
        return Math.max(robRange(nums, 0, n - 2), robRange(nums, 1, n - 1));
    }

    private static int robRange(int[] nums, int lo, int hi) {
        memo = new int[nums.length];                  // fresh memo per run: answers depend on lo
        Arrays.fill(memo, -1);
        return robLine(nums, lo, hi);
    }

    private static int robLine(int[] nums, int lo, int i) {
        if (i < lo) return 0;
        if (memo[i] != -1) return memo[i];
        memo[i] = Math.max(robLine(nums, lo, i - 1), nums[i] + robLine(nums, lo, i - 2));
        return memo[i];
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
